// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        int[][] matrixA = MatrixHelper.generateRandomMatrix(1000, 1000);
        int[][] matrixB = MatrixHelper.generateRandomMatrix(1000, 1000);

        for (int i = 0; i < 5; i++) {
            IMatrixMultiplicationAlgorithm multiplicationAlgorithm = new StripeAlgorithm(i+1);
            System.out.print("Stripe algorithm with " + (i+1) + " threads: => ");
            checkAlgorithmSpeed(matrixA, matrixB, multiplicationAlgorithm, 5);
        }
    }
    static void checkAlgorithmAccuracy(int[][] matrixA, int[][] matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm){
        int[][] result = multiplicationAlgorithm.multiply(matrixA, matrixB);
        int[][] result2 = new MatrixMultiplication().multiply(matrixA, matrixB);
        System.out.println(MatrixHelper.compareMatrices(result, result2) ? "Matrices are identical" : "Matrices are not identical");
    }

    static void checkAlgorithmSpeed(int[][] matrixA, int[][] matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm, int iterations){
        long[] times = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            multiplicationAlgorithm.multiply(matrixA, matrixB);
            long endTime = System.currentTimeMillis();
            times[i] = endTime - startTime;
        }
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += times[i];
        }
        System.out.println("Average time: " + (sum / iterations) + " milliseconds");
    }

}

