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
}