import mpi.*;

import static java.lang.System.exit;

public class BlockingMPI {
    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;
    private static final int MASTER_ID = 0;
    public static void main(String[] args) {
        Matrix matrixA = MatrixHelper.generateRandomMatrix(1500);
        Matrix matrixB = MatrixHelper.generateRandomMatrix(1500);

        try {
            long startTime = System.currentTimeMillis();
            int rowsCount = matrixA.getRowsCount();
            int columnsCount = matrixB.getColumnsCount();
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

                System.out.println("BlockingMPI");
                System.out.println("Total time: " + (System.currentTimeMillis() - startTime) + " ms");
                System.out.println("Matrix size: " + rowsCount + "x" + columnsCount);
                System.out.println("Workers count: " + workersCount);
            } else {
                workerProcess(rowsCount, columnsCount);
            }
        } finally {
            MPI.Finalize();
        }
    }


    private static void workerProcess(int rowsCount, int columnsCount) {
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

        Matrix subMatrix1 = Matrix.fromIntArray(subMatrix1Buffer,
                endRowIndex[0] - startRowIndex[0] + 1, columnsCount);
        Matrix matrix2 = Matrix.fromIntArray(matrix2Buffer, rowsCount, columnsCount);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuffer = resultMatrix.toIntArray();

        MPI.COMM_WORLD.Send(startRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(endRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(resultMatrixBuffer, 0, resultMatrixBuffer.length, MPI.INT, 0, TAG_WORKER);
    }

    private static void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int workers) {
        int rowsForOneWorker = resultMatrix.getRowsCount() / workers;
        int extraRows = resultMatrix.getRowsCount() % workers;

        for (int i = 1; i <= workers; i++) {
            int startRowIndex = (i - 1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if (i == workers) {
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, resultMatrix.getColumnsCount());
            int[] subMatrix1Buffer = subMatrix1.toIntArray();
            int[] matrix2Buffer = matrix2.toIntArray();

            MPI.COMM_WORLD.Send(new int[]{startRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Send(new int[]{endRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Send(subMatrix1Buffer, 0, subMatrix1Buffer.length, MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Send(matrix2Buffer, 0, matrix2Buffer.length, MPI.INT, i, TAG_MASTER);
        }

        for (int i = 1; i <= workers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];
            MPI.COMM_WORLD.Recv(startRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);
            MPI.COMM_WORLD.Recv(endRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);

            int countElemsResultBuffer = (endRowIndex[0] - startRowIndex[0] + 1) * resultMatrix.getColumnsCount() * Integer.BYTES;
            int[] resultMatrixBuffer = new int[countElemsResultBuffer];
            MPI.COMM_WORLD.Recv(resultMatrixBuffer, 0,
                    countElemsResultBuffer, MPI.INT, i, TAG_WORKER);
            Matrix subMatrix = Matrix.fromIntArray(resultMatrixBuffer,
                    endRowIndex[0] - startRowIndex[0] + 1, resultMatrix.getColumnsCount());

            resultMatrix.changeSlice(subMatrix, startRowIndex[0], endRowIndex[0], resultMatrix.getColumnsCount());
        }
    }
}