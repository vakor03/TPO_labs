import mpi.MPI;

import static java.lang.System.exit;

public class NonBlockingMPI {
    private static final int countRows = 1000;
    private static final int countColumn = 1000;

    private static final int TAG_MASTER = 1;
    private static final int TAG_WORKER = 2;

    public static void main(String[] args) {
        Matrix matrix1 = MatrixHelper.generateRandomMatrix(countRows, countColumn, 0, 10);
        Matrix matrix2 = MatrixHelper.generateRandomMatrix(countRows, countColumn, 0, 10);
        Matrix resultMatrix = new Matrix(countRows, countColumn);

        var startTime = System.currentTimeMillis();

        MPI.Init(args);

        int countTasks = MPI.COMM_WORLD.Size();
        int taskID = MPI.COMM_WORLD.Rank();
        //System.out.println("Tasks: "+ countTasks);

        int masterID = 0;
        int countWorkers = countTasks - 1;

        if(countTasks < 2){
            MPI.COMM_WORLD.Abort(1);
            exit(1);
        }

        if(taskID == masterID){
            masterProcess(matrix1, matrix2, resultMatrix, countWorkers);
            //Matrix matrix = matrix1.multiply(matrix2);
            //System.out.println(matrix.Equal(resultMatrix));
            //resultMatrix.print();
            var endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime);
        }
        else {
            workerProcess();
        }

        MPI.Finalize();
    }

    private static void workerProcess() {
        System.out.println("Worker process started");

        int[] startRowIndex = new int[1];
        int[] endRowIndex = new int[1];
        var recStartIndex = MPI.COMM_WORLD.Irecv(startRowIndex,0,1, MPI.INT, 0, TAG_MASTER);
        var recEndIndex = MPI.COMM_WORLD.Irecv(endRowIndex,0,1, MPI.INT, 0, TAG_MASTER);
        recStartIndex.Wait();
        recEndIndex.Wait();

        int sizeSubMatrix1Buffer = (endRowIndex[0] - startRowIndex[0] + 1) * countColumn;
        int sizeMatrix2Buffer = countRows * countColumn;
        int[] subMatrix1Buffer = new int[sizeSubMatrix1Buffer];
        int[] matrix2Buffer = new int[sizeMatrix2Buffer];
        var recSubMatrix1 = MPI.COMM_WORLD.Irecv(subMatrix1Buffer,0, sizeSubMatrix1Buffer, MPI.INT,0, TAG_MASTER);
        var recMatrix2 = MPI.COMM_WORLD.Irecv(matrix2Buffer,0,sizeMatrix2Buffer, MPI.INT,0, TAG_MASTER);
        recSubMatrix1.Wait();
        recMatrix2.Wait();

        Matrix subMatrix1 = MatrixHelper.createMatrixFromBuffer(subMatrix1Buffer, endRowIndex[0] - startRowIndex[0] + 1, countColumn);
        Matrix matrix2 = MatrixHelper.createMatrixFromBuffer(matrix2Buffer, countRows, countColumn);
        Matrix resultMatrix = subMatrix1.multiply(matrix2);

        int[] resultMatrixBuff = resultMatrix.toIntBuffer();

        MPI.COMM_WORLD.Isend(startRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(endRowIndex,0, 1, MPI.INT, 0, TAG_WORKER);
        MPI.COMM_WORLD.Isend(resultMatrixBuff,0, resultMatrixBuff.length, MPI.INT, 0, TAG_WORKER);

        System.out.println("Worker process finished");
    }

    private static void masterProcess(Matrix matrix1, Matrix matrix2, Matrix resultMatrix, int countWorkers) {
        int rowsForOneWorker = countRows / countWorkers;
        int extraRows = countRows % countWorkers;

        sendAssignmentsToWorkers(matrix1, matrix2, countWorkers, rowsForOneWorker, extraRows);
        System.out.println("Assignments sent");

        receiveResultsFromWorkers(resultMatrix, countWorkers);
        System.out.println("Results received");
    }

    private static void sendAssignmentsToWorkers(Matrix matrix1, Matrix matrix2, int countWorkers, int rowsForOneWorker, int extraRows) {
        for (int i = 1; i <= countWorkers; i++) {
            int startRowIndex = (i-1) * rowsForOneWorker;
            int endRowIndex = startRowIndex + rowsForOneWorker - 1;
            if(i == countWorkers){
                endRowIndex += extraRows;
            }

            Matrix subMatrix1 = matrix1.sliceMatrix(startRowIndex, endRowIndex, countColumn);
            int[] subMatrix1Buff = subMatrix1.toIntBuffer();
            int[] matrix2Buff = matrix2.toIntBuffer();

            sendAssignmentToWorker(i, startRowIndex, endRowIndex, subMatrix1Buff, matrix2Buff);
        }
    }

    private static void receiveResultsFromWorkers(Matrix resultMatrix, int countWorkers) {
        for (int i = 1; i <= countWorkers; i++) {
            int[] startRowIndex = new int[1];
            int[] endRowIndex = new int[1];

            var recStartIndex =  MPI.COMM_WORLD.Irecv(startRowIndex,0,1, MPI.INT, i, TAG_WORKER);
            var recEndIndex =MPI.COMM_WORLD.Irecv(endRowIndex,0,1, MPI.INT, i, TAG_WORKER);
            recStartIndex.Wait();
            recEndIndex.Wait();

            int resultBufferElementsCount = (endRowIndex[0] - startRowIndex[0] + 1) * countColumn;
            int[] resultMatrixBuff = new int[resultBufferElementsCount];

            var recRes = MPI.COMM_WORLD.Irecv(resultMatrixBuff,0, resultBufferElementsCount , MPI.INT, i, TAG_WORKER);
            recRes.Wait();

            Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(resultMatrixBuff, endRowIndex[0] - startRowIndex[0] + 1, countColumn);
            resultMatrix.updateMatrixSlice(subMatrix, startRowIndex[0], endRowIndex[0], countColumn);
        }
    }

    private static void sendAssignmentToWorker(int i, int startRowIndex, int endRowIndex, int[] subMatrix1Buff, int[] matrix2Buff) {
        MPI.COMM_WORLD.Isend(new int[]{startRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Isend(new int[]{endRowIndex}, 0, 1, MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Isend(subMatrix1Buff, 0, subMatrix1Buff.length , MPI.INT, i, TAG_MASTER);
        MPI.COMM_WORLD.Isend(matrix2Buff, 0, matrix2Buff.length, MPI.INT, i, TAG_MASTER);
    }
}