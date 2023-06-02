import mpi.*;

import static java.lang.System.exit;

public class BlockingMPI implements IMatrixMultiplicationAlgorithm {
    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;
    private static final int MASTER_ID = 0;
    private final String[] args;
    private int columnsCount;
    private int rowsCount;
    public BlockingMPI(String[] args) {
        this.args = args;
    }

    @Override
    public Result multiply(Matrix matrixA, Matrix matrixB) {
        try {
            long startTime = System.currentTimeMillis();
            rowsCount = matrixA.getRowsCount();
            columnsCount = matrixB.getColumnsCount();
            Matrix resultMatrix = new Matrix(rowsCount, columnsCount);

            MPI.Init(args);

            int tasksCount = MPI.COMM_WORLD.Size();
            int taskID = MPI.COMM_WORLD.Rank();

            int workersCount = tasksCount - 1;

            if (tasksCount < 2) {
                MPI.COMM_WORLD.Abort(1);
                exit(1);
            }

            if (taskID == MASTER_ID) {
                masterProcess(matrixA, matrixB, resultMatrix, workersCount);
                return new Result(resultMatrix, (System.currentTimeMillis() - startTime));
            } else {
                workerProcess();
            }
            return null;
        } finally {
            MPI.Finalize();
        }
    }


    private void workerProcess() {
        int[] startRowIndex = new int[1];
        int[] endRowIndex = new int[1];
        MPI.COMM_WORLD.Recv(startRowIndex, 0, 1, MPI.INT, 0, TAG_MASTER);
        MPI.COMM_WORLD.Recv(endRowIndex, 0, 1, MPI.INT, 0, TAG_MASTER);

        int sizeSubMatrix1Buffer = (endRowIndex[0] - startRowIndex[0] + 1) * columnsCount * Integer.BYTES;
        int sizeMatrix2Buffer = rowsCount * columnsCount * Integer.BYTES;
        int[] subMatrix1Buffer = new int[sizeSubMatrix1Buffer];
        int[] matrix2Buffer = new int[sizeMatrix2Buffer];
        MPI.COMM_WORLD.Recv(subMatrix1Buffer, 0, sizeSubMatrix1Buffer, MPI.INT, 0, TAG_MASTER);
        MPI.COMM_WORLD.Recv(matrix2Buffer, 0, sizeMatrix2Buffer, MPI.INT, 0, TAG_MASTER);

        Matrix subMatrix1 = MatrixHelper.createMatrixFromBuffer(subMatrix1Buffer,
                endRowIndex[0] - startRowIndex[0] + 1, columnsCount);
        Matrix matrix2 = MatrixHelper.createMatrixFromBuffer(matrix2Buffer, rowsCount, columnsCount);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuffer = resultMatrix.toIntBuffer();

        MPI.COMM_WORLD.Send(startRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(endRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(resultMatrixBuffer, 0, resultMatrixBuffer.length, MPI.INT, 0, TAG_WORKER);
    }

    private void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int countWorkers) {
        int rowsForOneWorker = rowsCount / countWorkers;
        int extraRows = rowsCount % countWorkers;

        sendAssignmentsToWorkers(matrix1, matrix2, countWorkers, rowsForOneWorker, extraRows);

        receiveResultsFromWorkers(resultMatrix, countWorkers);
    }

    private void receiveResultsFromWorkers(Matrix resultMatrix, int countWorkers) {
        for (int i = 1; i <= countWorkers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];
            MPI.COMM_WORLD.Recv(startRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);
            MPI.COMM_WORLD.Recv(endRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);

            int countElemsResultBuffer = (endRowIndex[0] - startRowIndex[0] + 1) * columnsCount * Integer.BYTES;
            int[] resultMatrixBuffer = new int[countElemsResultBuffer];
            MPI.COMM_WORLD.Recv(resultMatrixBuffer, 0,
                    countElemsResultBuffer, MPI.INT, i, TAG_WORKER);
            Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(resultMatrixBuffer,
                    endRowIndex[0] - startRowIndex[0] + 1, columnsCount);

            resultMatrix.updateMatrixSlice(subMatrix, startRowIndex[0], endRowIndex[0], columnsCount);
        }
    }

    private void sendAssignmentsToWorkers(Matrix matrix1, Matrix matrix2, int countWorkers, int rowsForOneWorker, int extraRows) {
        for (int i = 1; i <= countWorkers; i++) {
            int startRowIndex = (i - 1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if (i == countWorkers) {
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, columnsCount);
            int[] subMatrix1Buffer = subMatrix1.toIntBuffer();
            int[] matrix2Buffer = matrix2.toIntBuffer();

            sendAssignmentToWorker(i, startRowIndex, endRowIndex, subMatrix1Buffer, matrix2Buffer);
        }
    }

    private void sendAssignmentToWorker(int workerIndex, int startRowIndex, int endRowIndex,
                                        int[] subMatrix1Buffer, int[] matrix2Buffer) {
        MPI.COMM_WORLD.Send(new int[]{startRowIndex}, 0, 1, MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Send(new int[]{endRowIndex}, 0, 1, MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Send(subMatrix1Buffer, 0, subMatrix1Buffer.length, MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Send(matrix2Buffer, 0, matrix2Buffer.length, MPI.INT, workerIndex, TAG_MASTER);
    }
}