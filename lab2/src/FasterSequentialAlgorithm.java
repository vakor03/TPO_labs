public class FasterSequentialAlgorithm implements IMatrixMultiplicationAlgorithm {
    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        int[][] transposedMatrixB = MatrixHelper.transposeMatrix(MatrixHelper.clone(matrixB));

        for (int i = 0; i < matrixA[0].length; i++) {
            for (int j = 0; j < matrixA.length; j++) {
                int rowIndex = j;
                int colIndex = (j + i) % result[0].length;

                result[rowIndex][colIndex] = multiply(matrixA[rowIndex], transposedMatrixB[colIndex]);
            }
        }

        return result;
    }

    private int multiply(int[] row, int[] column) {
        int result = 0;
        for (int i = 0; i < row.length; i++) {
            result += row[i] * column[i];
        }
        return result;
    }
}
