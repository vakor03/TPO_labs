public class Matrix {
    private int[][] matrix;

    public Matrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public Matrix(int height, int width){
        this.matrix = new int[height][width];
    }
    public int[] getRow(int rowIndex){
        return matrix[rowIndex];
    }
    public int[][] getMatrix() {
        return matrix;
    }

    public int getRowsCount() {
        return matrix.length;
    }

    public int getColumnsCount() {
        return matrix[0].length;
    }

    public int get(int row, int column) {
        return matrix[row][column];
    }

    public void set(int row, int column, int value) {
        matrix[row][column] = value;
    }

    public void add(Matrix matrixB) {
        for (int i = 0; i < matrixB.getRowsCount(); i++) {
            for (int j = 0; j < matrixB.getColumnsCount(); j++) {
                matrix[i][j] += matrixB.get(i, j);
            }
        }
    }

    public boolean equals(Matrix matrix1){
        if (matrix.length != matrix1.getRowsCount() || matrix[0].length != matrix1.getColumnsCount()) {
            return false;
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix1.getColumnsCount(); j++) {
                if (matrix[i][j] != matrix1.get(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Matrix transpose() {
        int[][] result = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return new Matrix(result);
    }

    public Matrix clone() {
        int[][] result = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, result[i], 0, matrix[0].length);
        }
        return new Matrix(result);
    }

    public void print() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
