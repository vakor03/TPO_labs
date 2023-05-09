import java.util.Random;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        int[][] matrixA = GenerateRandomMatrix(2000, 2000);
        int[][] matrixB = GenerateRandomMatrix(2000, 2000);

//        long startTime = System.currentTimeMillis();
        int[][] result = MatrixMultiplication.PerformSequentialAlgorithm(matrixA, matrixB);
//        long endTime = System.currentTimeMillis();
//        System.out.println("Sequential algorithm took " + (endTime - startTime) + " milliseconds");
//        System.out.println();
//        PrintMatrix(result);
        for (int i = 20; i>=2; i--){
            CallBlockStripedDecompositionAlgorithm(matrixA, matrixB, i);
            CallTapeAlgorithm(matrixA, matrixB, i);
            System.out.println();
        }

//        PrintMatrix(result2);

//        System.out.println(MatricesAreIdentical(result, result2) ? "Matrices are identical" : "Matrices are not identical");
    }

    private static void PrintMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    private static boolean MatricesAreIdentical(int[][] matrixA, int[][] matrixB) {
        if (matrixA.length != matrixB.length) {
            return false;
        }
        if (matrixA[0].length != matrixB[0].length) {
            return false;
        }
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[][] GenerateRandomMatrix(int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        Random random = new Random();
        for (int[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                row[i] = random.nextInt(100);
            }
        }
        return matrix;
    }

    private static void CallBlockStripedDecompositionAlgorithm(int[][] matrixA, int[][] matrixB, int numberOfThreads) {
        long startTime = System.currentTimeMillis();
        int[][] result2 = new BlockStripedDecompositionAlgorithmOld().Multiply(matrixA, matrixB, 10);
        long endTime = System.currentTimeMillis();
        System.out.println("Block-striped => " + (endTime - startTime) + " ms with " + numberOfThreads + " threads");
//        System.out.println();
    }

    private static void CallTapeAlgorithm(int[][] matrixA, int[][] matrixB, int numberOfThreads) {
        long startTime = System.currentTimeMillis();
        int[][] result2 = new TapeMultiplicationAlgorithm().Multiply(matrixA, matrixB, 10);
        long endTime = System.currentTimeMillis();
        System.out.println("Tape => " + (endTime - startTime) + " ms with " + numberOfThreads + " threads");
    }
}

