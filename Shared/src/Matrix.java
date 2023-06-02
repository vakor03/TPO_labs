import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Matrix {
    private int[][] matrixData;
    public static final int INT_32_BYTE_SIZE = 4;

    public Matrix(int[][] matrix) {
        this.matrixData = matrix;
    }

    public Matrix(int height, int width){
        this.matrixData = new int[height][width];
    }
    public int[] getRow(int rowIndex){
        return matrixData[rowIndex];
    }
    public int[][] getMatrix() {
        return matrixData;
    }

    public int getRowsCount() {
        return matrixData.length;
    }

    public int getColumnsCount() {
        return matrixData[0].length;
    }

    public int get(int row, int column) {
        return matrixData[row][column];
    }

    public void set(int row, int column, int value) {
        matrixData[row][column] = value;
    }

    public void add(Matrix matrixB) {
        for (int i = 0; i < matrixB.getRowsCount(); i++) {
            for (int j = 0; j < matrixB.getColumnsCount(); j++) {
                matrixData[i][j] += matrixB.get(i, j);
            }
        }
    }

    public boolean equals(Matrix matrix1){
        if (matrixData.length != matrix1.getRowsCount() || matrixData[0].length != matrix1.getColumnsCount()) {
            return false;
        }
        for (int i = 0; i < matrixData.length; i++) {
            for (int j = 0; j < matrix1.getColumnsCount(); j++) {
                if (matrixData[i][j] != matrix1.get(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Matrix sliceMatrix(int startRowIndex, int endRowIndex, int columnsCount)
    {
        Matrix subMatrix = new Matrix(endRowIndex - startRowIndex + 1, columnsCount);
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            for (int j = 0; j < columnsCount; j++) {
                subMatrix.set(i - startRowIndex, j, matrixData[i][j]);
            }
        }
        return subMatrix;
    }

    public void updateMatrixSlice(Matrix matrix, int indexStartRow, int indexEndRow, int countColumns)
    {
        for (int i = indexStartRow; i <= indexEndRow; i++) {
            for (int j = 0; j < countColumns; j++) {
                matrixData[i][j] = matrix.get(i - indexStartRow, j);
            }
        }
    }

    public int[] toIntBuffer()
    {
        int [] array = new int[getRowsCount() * getColumnsCount()];
        int index = 0;
        for (int i = 0; i < getRowsCount(); i++) {
            for (int j = 0; j < getColumnsCount(); j++) {
                array[index] = matrixData[i][j];
                index++;
            }
        }
        return array;
    }

    public byte[] toByteBuffer() {
        var buffer = ByteBuffer.allocate(getRowsCount() * getColumnsCount() * INT_32_BYTE_SIZE);
        buffer.order(ByteOrder.nativeOrder());
        var intBuffer = buffer.asIntBuffer();
        for (var ints : matrixData) {
            intBuffer.put(ints);
        }

        return buffer.array();
    }

    public Matrix transpose() {
        int[][] result = new int[matrixData[0].length][matrixData.length];
        for (int i = 0; i < matrixData.length; i++) {
            for (int j = 0; j < matrixData[0].length; j++) {
                result[j][i] = matrixData[i][j];
            }
        }
        return new Matrix(result);
    }

    public Matrix clone() {
        int[][] result = new int[matrixData.length][matrixData[0].length];
        for (int i = 0; i < matrixData.length; i++) {
            System.arraycopy(matrixData[i], 0, result[i], 0, matrixData[0].length);
        }
        return new Matrix(result);
    }

    public void print() {
        for (int i = 0; i < matrixData.length; i++) {
            for (int j = 0; j < matrixData[0].length; j++) {
                System.out.print(matrixData[i][j] + " ");
            }
            System.out.println();
        }
    }

    public Matrix multiply(Matrix matrix2) {
        int[][] result = new int[matrixData.length][matrix2.getColumnsCount()];
        for (int i = 0; i < matrixData.length; i++) {
            for (int j = 0; j < matrix2.getColumnsCount(); j++) {
                for (int k = 0; k < matrixData[0].length; k++) {
                    result[i][j] += matrixData[i][k] * matrix2.get(k, j);
                }
            }
        }
        return new Matrix(result);
    }
}
