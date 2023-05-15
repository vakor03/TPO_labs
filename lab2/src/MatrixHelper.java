public class MatrixHelper {
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

    public static int[][] generateRandomMatrix(int width, int height, int minValue, int maxValue){
        int[][] result = new int[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width;j++){
                result[i][j] = (int)(Math.random() * (maxValue - minValue)) + minValue;
            }
        }
        return result;
    }

    public static int[][] generateRandomMatrix(int width, int height){
        return generateRandomMatrix(width, height, 0, 100);
    }

    public static boolean compareMatrices(int[][] matrixA, int[][] matrixB){
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length){
            return false;
        }
        for(int i = 0; i < matrixA.length; i++){
            for(int j = 0; j < matrixA[0].length;j++){
                if (matrixA[i][j] != matrixB[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public static void printMatrix(int[][] matrix){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length;j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
