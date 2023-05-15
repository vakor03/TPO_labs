public class SequentialAlgorithm implements IMatrixMultiplicationAlgorithm {
    @Override
    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                int value = 0;
                for (int k = 0; k < matrixA[0].length; k++) {
                    value += matrixA[i][k] * matrixB[k][j];
                }
                result[i][j] = value;
            }
        }
        return result;
    }
}

