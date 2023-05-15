public class MatrixHelper {
    public static int[] getMatrixRow(int[][] matrix, int rowIndex) {
        return matrix[rowIndex];
    }

    public static int[] getMatrixColumn(int[][] matrix, int columnIndex) {
        int[] column = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][columnIndex];
        }
        return column;
    }

    public static int[][] transposeMatrix(int[][] matrix){
        int[][] result = new int[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length;j++){
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    public static int[][] clone(int[][] matrix){
        int[][] result = new int[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            System.arraycopy(matrix[i], 0, result[i], 0, matrix[0].length);
        }
        return result;
    }
}
