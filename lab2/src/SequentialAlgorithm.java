public class SequentialAlgorithm implements IMatrixMultiplicationAlgorithm {
    @Override
    public Result multiply(Matrix matrixA, Matrix matrixB) {
        long startTime = System.currentTimeMillis();

        int[][] result = new int[matrixA.getRowsCount()][matrixB.getColumnsCount()];
        for (int i = 0; i < matrixA.getRowsCount(); i++) {
            for (int j = 0; j < matrixB.getColumnsCount(); j++) {
                int value = 0;
                for (int k = 0; k < matrixA.getColumnsCount(); k++) {
                    value += matrixA.get(i,k) * matrixB.get(k,j);
                }
                result[i][j] = value;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        return new Result(new Matrix(result), duration);
    }
}

