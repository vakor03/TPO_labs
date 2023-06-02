public class Main {
    public static void main(String[] args) {
//        testAlgorithmsAccuracy();

        int[] matrixSizes = {500, 1000, 1500, 2000, 2500, 3000, 3500};
        int[] threadsCounts = {2, 4, 8, 9};

        testAlgorithmsSpeed(matrixSizes, threadsCounts);
    }

    private static void testAlgorithmsSpeed(int[] matrixSizes, int[] threadsCounts) {
        for (int matrixSize : matrixSizes) {
            Matrix matrixA = MatrixHelper.generateRandomMatrix(matrixSize);
            Matrix matrixB = MatrixHelper.generateRandomMatrix(matrixSize);
            System.out.println("-------------------------");
            System.out.println("Matrix size: " + matrixSize);

            long sequentialTime = checkAlgorithmSpeed(matrixA, matrixB, new SequentialAlgorithm(), 5);
            System.out.println("\nSequential algorithm: " + sequentialTime + " ms");

            for (int threads : threadsCounts) {
//                System.out.println("\nThreads count: " + threads);
//                long stripeTime = checkAlgorithmSpeed(matrixA, matrixB, new StripeAlgorithm(threads), 5);
//                long foxTime = checkAlgorithmSpeed(matrixA, matrixB, new FoxAlgorithm(threads), 5);

//                System.out.println("\tStripe algorithm with " + threads + " threads: " + stripeTime + " ms");
//                System.out.println("\tFox algorithm with " + threads + " threads: " + foxTime + " ms");
            }
        }
    }

    static void testAlgorithmsAccuracy() {
        Matrix matrixA = MatrixHelper.generateRandomMatrix(4);
        Matrix matrixB = MatrixHelper.generateRandomMatrix(4);

        Matrix resultSequential = new SequentialAlgorithm().multiply(matrixA, matrixB).getResultMatrix();
        Matrix resultStripe = new StripeAlgorithm(2).multiply(matrixA, matrixB).getResultMatrix();
        Matrix resultFox = new FoxAlgorithm(2).multiply(matrixA, matrixB).getResultMatrix();

        System.out.println("Matrix A:");
        matrixA.print();

        System.out.println("\nMatrix B:");
        matrixB.print();

        System.out.println("\nSequential result:");
        resultSequential.print();

        System.out.println("\nStripe result:");
        resultStripe.print();

        System.out.println("\nFox result:");
        resultFox.print();
    }

    static long checkAlgorithmSpeed(Matrix matrixA, Matrix matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm, int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += multiplicationAlgorithm.multiply(matrixA, matrixB).getTotalTime();
        }
        return sum / iterations;
    }

}

