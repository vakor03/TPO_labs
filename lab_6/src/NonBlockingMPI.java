import mpi.MPI;
import mpi.Request;

import static java.lang.System.exit;

public class NonBlockingMPI implements IMatrixMultiplicationAlgorithm {

    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;
    private static final int MASTER_ID = 0;

    private final String[] args;
    private int columnsCount;
    private int rowsCount;

    public NonBlockingMPI(String[] args) {
        this.args = args;
    }

    @Override
    public Result multiply(Matrix matrixA, Matrix matrixB) {
        try{
            long startTime = System.currentTimeMillis();
            rowsCount = matrixA.getRowsCount();
            columnsCount = matrixB.getColumnsCount();
            Matrix resultMatrix = new Matrix(rowsCount, columnsCount);

            MPI.Init(args);

            int countTasks = MPI.COMM_WORLD.Size();
            int taskID = MPI.COMM_WORLD.Rank();

            int countWorkers = countTasks - 1;

            if(countTasks < 2){
                MPI.COMM_WORLD.Abort(1);
                exit(1);
            }

            if(taskID == MASTER_ID){
                masterProcess(matrixA, matrixB, resultMatrix, countWorkers);

                return new Result(resultMatrix, (System.currentTimeMillis() - startTime));
            }
            else {
                workerProcess();
            }
            return null;
        }
        finally {
            MPI.Finalize();
        }
    }

    private void workerProcess() {
        int[] startRowIndex = new int[1];
        int[] endRowIndex = new int[1];
        Request recStartIndex = MPI.COMM_WORLD.Irecv(startRowIndex,0,1, MPI.INT, 0, TAG_MASTER);
        Request recEndIndex = MPI.COMM_WORLD.Irecv(endRowIndex,0,1, MPI.INT, 0, TAG_MASTER);
        recStartIndex.Wait();
        recEndIndex.Wait();

        int sizeSubMatrix1Buffer = (endRowIndex[0] - startRowIndex[0] + 1) * columnsCount;
        int sizeMatrix2Buffer = rowsCount * columnsCount;
        int[] subMatrix1Buffer = new int[sizeSubMatrix1Buffer];
        int[] matrix2Buffer = new int[sizeMatrix2Buffer];
        Request recSubMatrix1 = MPI.COMM_WORLD.Irecv(subMatrix1Buffer,0, sizeSubMatrix1Buffer,
                MPI.INT,0, TAG_MASTER);
        Request recMatrix2 = MPI.COMM_WORLD.Irecv(matrix2Buffer,0,sizeMatrix2Buffer, MPI.INT,0, TAG_MASTER);
        recSubMatrix1.Wait();
        recMatrix2.Wait();

        Matrix subMatrix1 = MatrixHelper.createMatrixFromBuffer(subMatrix1Buffer,
                endRowIndex[0] - startRowIndex[0] + 1, columnsCount);
        Matrix matrix2 = MatrixHelper.createMatrixFromBuffer(matrix2Buffer, rowsCount, columnsCount);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuff = resultMatrix.toIntBuffer();

        MPI.COMM_WORLD.Isend(startRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(endRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(resultMatrixBuff,0, resultMatrixBuff.length, MPI.INT, 0, TAG_WORKER);
    }

    private void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int countWorkers) {
        int rowsForOneWorker = rowsCount / countWorkers;
        int extraRows = rowsCount % countWorkers;

        sendAssignmentsToWorkers(matrix1, matrix2, countWorkers, rowsForOneWorker, extraRows);

        receiveResultsFromWorkers(resultMatrix, countWorkers);
    }

    private void sendAssignmentsToWorkers(Matrix matrix1, Matrix matrix2, int countWorkers,
                                          int rowsForOneWorker, int extraRows) {
        for (int i = 1; i <= countWorkers; i++) {
            int startRowIndex = (i-1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if(i == countWorkers){
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, columnsCount);
            int[] subMatrix1Buff = subMatrix1.toIntBuffer();
            int[] matrix2Buff = matrix2.toIntBuffer();

            sendAssignmentToWorker(i, startRowIndex, endRowIndex, subMatrix1Buff, matrix2Buff);
        }
    }

    private void receiveResultsFromWorkers(Matrix resultMatrix, int countWorkers) {
        for (int i = 1; i <= countWorkers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];

            Request recStartIndex =  MPI.COMM_WORLD.Irecv(startRowIndex,0,1, MPI.INT, i, TAG_WORKER);
            Request recEndIndex =MPI.COMM_WORLD.Irecv(endRowIndex,0,1, MPI.INT, i, TAG_WORKER);
            recStartIndex.Wait();
            recEndIndex.Wait();

            int resultBufferElementsCount = (endRowIndex[0] - startRowIndex[0] + 1) * columnsCount;
            int[] resultMatrixBuff = new int[resultBufferElementsCount];

            Request recRes = MPI.COMM_WORLD.Irecv(resultMatrixBuff,0, resultBufferElementsCount ,
                    MPI.INT, i, TAG_WORKER);
            recRes.Wait();

            Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(resultMatrixBuff,
                    endRowIndex[0] - startRowIndex[0] + 1, columnsCount);
            resultMatrix.updateMatrixSlice(subMatrix, startRowIndex[0], endRowIndex[0], columnsCount);
        }
    }

    private void sendAssignmentToWorker(int workerIndex, int startRowIndex, int endRowIndex,
                                        int[] subMatrix1Buff, int[] matrix2Buff) {
        MPI.COMM_WORLD.Isend(new int[]{startRowIndex}, 0, 1, MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Isend(new int[]{endRowIndex}, 0, 1, MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Isend(subMatrix1Buff, 0, subMatrix1Buff.length , MPI.INT, workerIndex, TAG_MASTER);
        MPI.COMM_WORLD.Isend(matrix2Buff, 0, matrix2Buff.length, MPI.INT, workerIndex, TAG_MASTER);
    }
}