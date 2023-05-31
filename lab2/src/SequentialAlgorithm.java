public class SequentialAlgorithm implements IMatrixMultiplicationAlgorithm {
    @Override
    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
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
        return new Matrix(result);
    }
}

