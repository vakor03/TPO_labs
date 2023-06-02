
import mpi.MPI;

public class CollectiveMPI implements IMatrixMultiplicationAlgorithm {
    private static final int MASTER_ID = 0;
    private static final int INT_32_BYTE_SIZE = 4;
    private final String[] args;

    public CollectiveMPI(String[] args) {
        this.args = args;
    }

    @Override
    public Result multiply(Matrix matrixA, Matrix matrixB) {
        try{
            long startTime = System.currentTimeMillis();

            MPI.Init(args);

            int tasksCount = MPI.COMM_WORLD.Size();
            int taskID = MPI.COMM_WORLD.Rank();

            int[] bytes = calculateBytes(matrixA, matrixB, tasksCount);
            int[] offsets = calculateOffsets(tasksCount, bytes);

            byte[] matrixAByteBuffer = matrixA.toByteBuffer();
            byte[] matrixBByteBuffer = matrixB.toByteBuffer();

            int taskBytes = bytes[taskID];
            byte[] subMatrixBytes = new byte[taskBytes];
            byte[] resBytes = new byte[matrixA.getRowsCount() * matrixB.getColumnsCount() * INT_32_BYTE_SIZE];

            MPI.COMM_WORLD.Scatterv(matrixAByteBuffer, 0, bytes, offsets, MPI.BYTE,
                    subMatrixBytes, 0, taskBytes, MPI.BYTE, 0);

            MPI.COMM_WORLD.Bcast(matrixBByteBuffer, 0, matrixBByteBuffer.length, MPI.BYTE, 0);

            byte[] multiplicationResultBuffer = performMatrixMultiplication(matrixA.getRowsCount(),
                    matrixB.getColumnsCount(), matrixBByteBuffer, taskBytes, subMatrixBytes)
                    .toByteBuffer();

            MPI.COMM_WORLD.Gatherv(multiplicationResultBuffer, 0, multiplicationResultBuffer.length,
                    MPI.BYTE, resBytes, 0, bytes, offsets, MPI.BYTE, 0);

            if (taskID == MASTER_ID) {
                Matrix resultMatrix = MatrixHelper.createMatrixFromBuffer(resBytes, matrixA.getRowsCount(),
                        matrixB.getColumnsCount());

                return new Result(resultMatrix, System.currentTimeMillis() - startTime);
            }

            return null;
        }finally {
            MPI.Finalize();
        }
    }

    private Matrix performMatrixMultiplication(int matrix1RowsCount, int matrix2ColumnsCount, byte[] secondMatrixBuffer, int taskBytes, byte[] subMatrixBytes) {
        Matrix subMatrix = MatrixHelper.createMatrixFromBuffer(subMatrixBytes, taskBytes / (INT_32_BYTE_SIZE * matrix2ColumnsCount), matrix1RowsCount);
        Matrix secondMatrix = MatrixHelper.createMatrixFromBuffer(secondMatrixBuffer, matrix2ColumnsCount, matrix1RowsCount);

        return subMatrix.multiply(secondMatrix);
    }

    private int[] calculateBytes(Matrix matrix1, Matrix matrix2, int tasksCount) {
        var rowsForOneWorker = matrix1.getRowsCount() / tasksCount;
        var extraRows = matrix1.getRowsCount() % tasksCount;

        int[] bytes = new int[tasksCount];
        for (var i = 0; i < tasksCount; i++) {
            if (i != tasksCount - 1) {
                bytes[i] = rowsForOneWorker * matrix2.getColumnsCount() * INT_32_BYTE_SIZE;
            } else {
                bytes[i] = (rowsForOneWorker + extraRows) * matrix2.getColumnsCount() * INT_32_BYTE_SIZE;
            }
        }
        return bytes;
    }

    private int[] calculateOffsets(int tasksCount, int[] bytes) {
        int[] offsets = new int[tasksCount];
        for (var i = 0; i < offsets.length; i++) {
            if (i == 0) continue;

            offsets[i] = bytes[i - 1] + offsets[i - 1];
        }
        return offsets;
    }
}