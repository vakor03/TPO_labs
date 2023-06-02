import mpi.MPI;
import mpi.Request;

import static java.lang.System.exit;

public class NonBlockingMPI {
    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;
    private static final int MASTER_ID = 0;

    public static void main(String[] args) {
        Matrix matrixA = MatrixHelper.generateRandomMatrix(1500);
        Matrix matrixB = MatrixHelper.generateRandomMatrix(1500);

        try{
            long startTime = System.currentTimeMillis();
            int rowsCount = matrixA.getRowsCount();
            int columnsCount = matrixB.getColumnsCount();
            Matrix resultMatrix = new Matrix(rowsCount, columnsCount);

            MPI.Init(args);

            int countTasks = MPI.COMM_WORLD.Size();
            int taskID = MPI.COMM_WORLD.Rank();

            int workers = countTasks - 1;

            if(countTasks < 2){
                MPI.COMM_WORLD.Abort(1);
                exit(1);
            }

            if(taskID == MASTER_ID){
                masterProcess(matrixA, matrixB, resultMatrix, workers);

                System.out.println("NonBlockingMPI");
                System.out.println("Total time: " + (System.currentTimeMillis() - startTime) + " ms");
                System.out.println("Matrix size: " + rowsCount + "x" + columnsCount);
                System.out.println("Workers count: " + workers);
            }
            else {
                workerProcess(columnsCount, rowsCount);
            }
        }
        finally {
            MPI.Finalize();
        }
    }

    private static void workerProcess(int columnsCount, int rowsCount  ) {
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

        Matrix subMatrix1 = Matrix.fromIntArray(subMatrix1Buffer,
                endRowIndex[0] - startRowIndex[0] + 1, columnsCount);
        Matrix matrix2 = Matrix.fromIntArray(matrix2Buffer, rowsCount, columnsCount);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuff = resultMatrix.toIntArray();

        MPI.COMM_WORLD.Isend(startRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(endRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(resultMatrixBuff,0, resultMatrixBuff.length, MPI.INT, 0, TAG_WORKER);
    }

    private static void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int workers) {
        int rowsForOneWorker = resultMatrix.getRowsCount() / workers;
        int extraRows = resultMatrix.getRowsCount() % workers;

        for (int i = 1; i <= workers; i++) {
            int startRowIndex = (i-1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if(i == workers){
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, resultMatrix.getColumnsCount());
            int[] subMatrix1Buff = subMatrix1.toIntArray();
            int[] matrix2Buff = matrix2.toIntArray();

            MPI.COMM_WORLD.Isend(new int[]{startRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Isend(new int[]{endRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Isend(subMatrix1Buff, 0, subMatrix1Buff.length , MPI.INT, i, TAG_MASTER);
            MPI.COMM_WORLD.Isend(matrix2Buff, 0, matrix2Buff.length, MPI.INT, i, TAG_MASTER);
        }

        for (int i = 1; i <= workers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];

            Request recStartIndex = MPI.COMM_WORLD.Irecv(startRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);
            Request recEndIndex = MPI.COMM_WORLD.Irecv(endRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);
            recStartIndex.Wait();
            recEndIndex.Wait();

            int resultBufferElementsCount = (endRowIndex[0] - startRowIndex[0] + 1) * resultMatrix.getColumnsCount();
            int[] resultMatrixBuff = new int[resultBufferElementsCount];

            Request recRes = MPI.COMM_WORLD.Irecv(resultMatrixBuff, 0, resultBufferElementsCount,
                    MPI.INT, i, TAG_WORKER);
            recRes.Wait();

            Matrix subMatrix = Matrix.fromIntArray(resultMatrixBuff,
                    endRowIndex[0] - startRowIndex[0] + 1, resultMatrix.getColumnsCount());
            resultMatrix.changeSlice(subMatrix, startRowIndex[0], endRowIndex[0], resultMatrix.getColumnsCount());
        }
    }

}