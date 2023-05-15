public class TapeAlgorithm {
    public static Result multiply(int[][] matrixA, int[][] matrixB){
        long startTime = System.currentTimeMillis();
//        for ()
        return null;
    }
    private static int[][] getColumns(int[][] matrix){
        int[][] result = new int[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length;j++){
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
}
