import mpi.*;

import static java.lang.System.exit;

public class BlockingMPI {
    private static final int countRows = 1000;
    private static final int countColumn = 1000;

    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;

    public static void main(String[] args) {
        Matrix matrix1 = MatrixHelper.generateRandomMatrix(countRows, countColumn, 0, 10);
        Matrix matrix2 = MatrixHelper.generateRandomMatrix(countRows, countColumn, 0, 10);
        Matrix resultMatrix = new Matrix(countRows, countColumn);

        long startTime = System.currentTimeMillis();
        MPI.Init(args);

        int tasksCount = MPI.COMM_WORLD.Size();
        int taskID = MPI.COMM_WORLD.Rank();
//        System.out.println("Tasks: " + tasksCount);

        int masterID = 0;
        int countWorkers = tasksCount - 1;

        if (tasksCount < 2) {
            MPI.COMM_WORLD.Abort(1);
            exit(1);
        }


        if (taskID == masterID) {
            masterProcess(matrix1, matrix2, resultMatrix, countWorkers);
            long endTime = System.currentTimeMillis();
            System.out.println("Master process time: " + (endTime - startTime) + " ms");
        } else {
            workerProcess();
            long endTime = System.currentTimeMillis();
            System.out.println("Worker process time: " + (endTime - startTime) + " ms");
        }

        MPI.Finalize();
    }

    private static void workerProcess() {
        System.out.println("Worker process started");

        int[] startRowIndex = new int[1];
        int[] endRowIndex = new int[1];
        MPI.COMM_WORLD.Recv(startRowIndex, 0, 1, MPI.INT, 0, TAG_MASTER);
        MPI.COMM_WORLD.Recv(endRowIndex, 0, 1, MPI.INT, 0, TAG_MASTER);

        int sizeSubMatrix1Buffer = (endRowIndex[0] - startRowIndex[0] + 1) * countColumn * Integer.BYTES;
        int sizeMatrix2Buffer = countRows * countColumn * Integer.BYTES;
        int[] subMatrix1Buffer = new int[sizeSubMatrix1Buffer];
        int[] matrix2Buffer = new int[sizeMatrix2Buffer];
        MPI.COMM_WORLD.Recv(subMatrix1Buffer, 0, sizeSubMatrix1Buffer, MPI.INT, 0, TAG_MASTER);
        MPI.COMM_WORLD.Recv(matrix2Buffer, 0, sizeMatrix2Buffer, MPI.INT, 0, TAG_MASTER);

        Matrix subMatrix1 = MatrixHelper.createMatrixFromBuffer(subMatrix1Buffer, endRowIndex[0] - startRowIndex[0] + 1, countColumn);
        Matrix matrix2 = MatrixHelper.createMatrixFromBuffer(matrix2Buffer, countRows, countColumn);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuffer = resultMatrix.toIntBuffer();

        MPI.COMM_WORLD.Send(startRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(endRowIndex, 0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Send(resultMatrixBuffer, 0, resultMatrixBuffer.length, MPI.INT, 0, TAG_WORKER);

        System.out.println("Worker process finished");
    }

    static void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int countWorkers) {
        int rowsForOneWorker = countRows / countWorkers;
        int extraRows = countRows % countWorkers;

        sendAssignmentsToWorkers(matrix1, matrix2, countWorkers, rowsForOneWorker, extraRows);
        System.out.println("Assignments sent");

        receiveResultsFromWorkers(resultMatrix, countWorkers);
        System.out.println("Results received");
    }

    private static void receiveResultsFromWorkers(Matrix resultMatrix, int countWorkers) {
        for (int i = 1; i <= countWorkers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];
            MPI.COMM_WORLD.Recv(startRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);
            MPI.COMM_WORLD.Recv(endRowIndex, 0, 1, MPI.INT, i, TAG_WORKER);

            int countElemsResultBuffer = (endRowIndex[0] - startRowIndex[0] + 1) * countColumn * Integer.BYTES;
            int[] resultMatrixBuffer = new int[countElemsResultBuffer];
            MPI.COMM_WORLD.Recv(resultMatrixBuffer, 0,
                    countElemsResultBuffer, MPI.INT, i, TAG_WORKER);
            Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(resultMatrixBuffer, endRowIndex[0] - startRowIndex[0] + 1, countColumn);

            resultMatrix.updateMatrixSlice(subMatrix, startRowIndex[0], endRowIndex[0], countColumn);
        }
    }

    private static void sendAssignmentsToWorkers(Matrix matrix1, Matrix matrix2, int countWorkers, int rowsForOneWorker, int extraRows) {
        for (int i = 1; i <= countWorkers; i++) {
            int startRowIndex = (i - 1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if (i == countWorkers) {
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, countColumn);
            int[] subMatrix1Buffer = subMatrix1.toIntBuffer();
            int[] matrix2Buffer = matrix2.toIntBuffer();

            sendAssignmentToWorker(i, startRowIndex, endRowIndex, subMatrix1Buffer, matrix2Buffer);
        }
    }

    private static void sendAssignmentToWorker(int i, int startRowIndex, int endRowIndex, int[] subMatrix1Buffer, int[] matrix2Buffer) {
        MPI.COMM_WORLD.Send(new int[]{startRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Send(new int[]{endRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Send(subMatrix1Buffer, 0, subMatrix1Buffer.length, MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Send(matrix2Buffer, 0, matrix2Buffer.length, MPI.INT, i, TAG_MASTER);
    }
}