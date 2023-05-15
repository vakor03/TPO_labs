// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        int[][] matrixA = MatrixHelper.generateRandomMatrix(2000, 2000);
        int[][] matrixB = MatrixHelper.generateRandomMatrix(2000, 2000);

        checkAlgorithmAccuracy(matrixA, matrixB, new StripeAlgorithm(2));
        checkAlgorithmAccuracy(matrixA, matrixB, new FoxAlgorithm(2));
        checkAlgorithmAccuracy(matrixA, matrixB, new FasterSequentialAlgorithm());
        for (int i = 0; i < 10; i++) {
            IMatrixMultiplicationAlgorithm multiplicationAlgorithm = new StripeAlgorithm(i + 1);
            System.out.print("Stripe algorithm with " + (i + 1) + " threads: => ");
            checkAlgorithmSpeed(matrixA, matrixB, multiplicationAlgorithm, 10);
        }

        System.out.println();

        for (int i = 1; i < 50; i++) {
            int threadsCount = i+1;
            IMatrixMultiplicationAlgorithm multiplicationAlgorithm2 = new FoxAlgorithm(threadsCount, 500);
            System.out.print("Fox algorithm with " + threadsCount + " threads: => ");
            checkAlgorithmSpeed(matrixA, matrixB, multiplicationAlgorithm2, 1);
        }

        System.out.println();
        System.out.print("Sequential algorithm: => ");
        checkAlgorithmSpeed(matrixA, matrixB, new SequentialAlgorithm(), 1);

        System.out.println();
        System.out.print("Faster sequential algorithm: => ");
        checkAlgorithmSpeed(matrixA, matrixB, new FasterSequentialAlgorithm(), 1);


    }

    static void checkAlgorithmAccuracy(int[][] matrixA, int[][] matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm) {
        int[][] result = multiplicationAlgorithm.multiply(matrixA, matrixB);
        int[][] result2 = new SequentialAlgorithm().multiply(matrixA, matrixB);
        System.out.println(MatrixHelper.compareMatrices(result, result2) ? "Matrices are identical" : "Matrices are not identical");
    }

    static void checkAlgorithmSpeed(int[][] matrixA, int[][] matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm, int iterations) {
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

