import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    public static Matrix createMatrixFromBuffer(byte[] bytes, int rows, int cols) {
        var buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.nativeOrder());
        var array = new int[rows][cols];
        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                array[i][j] = buffer.getInt();
            }
        }
        return new Matrix(array);
    }
}