public class MatrixHelper {
    public static Matrix generateRandomMatrix(int width, int height, int minValue, int maxValue) {
        int[][] result = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = (int) (Math.random() * (maxValue - minValue)) + minValue;
            }
        }
        return new Matrix(result);
    }

    public static Matrix generateRandomMatrix(int size) {
        return generateRandomMatrix(size, size, 0, 100);
    }

    public static Matrix createMatrixFromBuffer(int[] array, int rowsCount, int columnsCount) {
        int[][] matrixData = new int[rowsCount][columnsCount];

        int arrayIndex = 0;
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                matrixData[i][j] = array[arrayIndex];
                arrayIndex++;
            }
        }
        return new Matrix(matrixData);
    }
}