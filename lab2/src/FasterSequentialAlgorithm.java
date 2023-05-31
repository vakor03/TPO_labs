public class FasterSequentialAlgorithm implements IMatrixMultiplicationAlgorithm {
    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        int[][] result = new int[matrixA.getRowsCount()][matrixB.getColumnsCount()];
        Matrix transposedMatrixB = matrixB.clone().transpose();

        for (int i = 0; i < matrixA.getColumnsCount(); i++) {
            for (int j = 0; j < matrixA.getRowsCount(); j++) {
                int rowIndex = j;
                int colIndex = (j + i) % result[0].length;

                result[rowIndex][colIndex] = multiply(matrixA.getRow(rowIndex), transposedMatrixB.getRow(colIndex));
            }
        }

        return new Matrix(result);
    }

    private int multiply(int[] row, int[] column) {
        int result = 0;
        for (int i = 0; i < row.length; i++) {
            result += row[i] * column[i];
        }
        return result;
    }
}
