
import mpi.MPI;

public class CollectiveMPI {

    public static void main(String[] args) {
        Matrix matrix1 = MatrixHelper.generateRandomMatrix(1000, 1000, 0, 10);
        Matrix matrix2 = MatrixHelper.generateRandomMatrix(1000, 1000, 0, 10);

        MPI.Init(args);

        var startTime = System.currentTimeMillis();
        var tasksCount = MPI.COMM_WORLD.Size();
        var taskID = MPI.COMM_WORLD.Rank();

        int[] bytes = calculateBytes(matrix1, matrix2, tasksCount);

        int[] offsets = calculateOffsets(tasksCount, bytes);

        byte[] firstMatrixBuffer = matrix1.toByteBuffer();
        byte[] secondMatrixBuffer = matrix2.toByteBuffer();

        int taskBytes = bytes[taskID];
        byte[] subMatrixBytes = new byte[taskBytes];
        byte[] resBytes = new byte[matrix1.getRowsCount() * matrix2.getColumnsCount() * Matrix.INT32_BYTE_SIZE];

        MPI.COMM_WORLD.Scatterv(firstMatrixBuffer, 0, bytes, offsets, MPI.BYTE, subMatrixBytes, 0, taskBytes, MPI.BYTE, 0);

        MPI.COMM_WORLD.Bcast(secondMatrixBuffer, 0, secondMatrixBuffer.length, MPI.BYTE, 0);

        byte[] multiplicationResultBuffer = performMatrixMultiplication(matrix1.getRowsCount(), matrix2.getColumnsCount(), secondMatrixBuffer, taskBytes, subMatrixBytes);
        //MPI.COMM_WORLD.Allgatherv(multiplicationResultBuffer, 0, multiplicationResultBuffer.length, MPI.BYTE, resBytes, 0, bytes, offsets, MPI.BYTE);

        MPI.COMM_WORLD.Gatherv(multiplicationResultBuffer, 0, multiplicationResultBuffer.length, MPI.BYTE, resBytes, 0, bytes, offsets, MPI.BYTE, 0);

        if (taskID == 0) {
            var resultMatrix = MatrixHelper.createMatrixFromBuffer(resBytes, matrix1.getRowsCount(), matrix2.getColumnsCount());
            var endTime = System.currentTimeMillis();
            System.out.println(matrix1.multiply(matrix2).equals(resultMatrix));
        }

        MPI.Finalize();
    }

    private static byte[] performMatrixMultiplication(int matrix1RowsCount, int matrix2ColumnsCount, byte[] secondMatrixBuffer, int taskBytes, byte[] subMatrixBytes) {
        Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(subMatrixBytes, taskBytes / (Matrix.INT32_BYTE_SIZE * matrix2ColumnsCount), matrix1RowsCount);
        Matrix secondMatrix = MatrixHelper.createMatrixFromBuffer(secondMatrixBuffer, matrix2ColumnsCount, matrix1RowsCount);

        Matrix result = subMatrix.multiply(secondMatrix);

        return result.toByteBuffer();
    }

    private static int[] calculateBytes(Matrix matrix1, Matrix matrix2, int tasksCount) {
        var rowsForOneWorker = matrix1.getRowsCount() / tasksCount;
        var extraRows = matrix1.getRowsCount() % tasksCount;

        int[] bytes = new int[tasksCount];
        for (var i = 0; i < tasksCount; i++) {
            if (i != tasksCount - 1) {
                bytes[i] = rowsForOneWorker * matrix2.getColumnsCount() * Matrix.INT32_BYTE_SIZE;
            } else {
                bytes[i] = (rowsForOneWorker + extraRows) * matrix2.getColumnsCount() * Matrix.INT32_BYTE_SIZE;
            }
        }
        return bytes;
    }

    private static int[] calculateOffsets(int tasksCount, int[] bytes) {
        int[] offsets = new int[tasksCount];
        for (var i = 0; i < offsets.length; i++) {
            if (i == 0) continue;

            offsets[i] = bytes[i - 1] + offsets[i - 1];
        }
        return offsets;
    }
}