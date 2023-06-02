public class Main {
    public static void main(String[] args) {
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

            //long sequentialTime = checkAlgorithmSpeed(matrixA, matrixB, new SequentialAlgorithm(), 1);
           // System.out.println("\nSequential algorithm: " + sequentialTime + " ms");

            for (int threads : threadsCounts) {
                System.out.println("\nThreads count: " + threads);

                long foxForkJoinTime = checkAlgorithmSpeed(matrixA, matrixB, new FoxAlgorithmForkJoin(threads), 5);
//                long foxTime = checkAlgorithmSpeed(matrixA, matrixB, new FoxAlgorithm(threads), 5);

//                System.out.println("\tFox algorithm with " + threads + " threads: " + foxTime + " ms");
                System.out.println("\tFox algorithm with " + threads + " threads and ForkJoin: " + foxForkJoinTime + " ms");
            }
        }
    }
    static long checkAlgorithmSpeed(Matrix matrixA, Matrix matrixB, IMatrixMultiplicationAlgorithm multiplicationAlgorithm, int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
                FoxAlgorithmForkJoin foxAlgorithmForkJoin = new FoxAlgorithmForkJoin(4);
                Result result = foxAlgorithmForkJoin.multiply(matrixA, matrixB);
            long endTime = System.currentTimeMillis();
            System.out.println(checkMatrixEquals(result.getMatrix(), matrixA.multiply(matrixB)));
            sum += endTime - startTime;
        }
        return sum / iterations;
    }

    static boolean checkMatrixEquals(Matrix matrixA, Matrix matrixB){
        if(matrixA.getRowsNumber() != matrixB.getRowsNumber() || matrixA.getColumnsNumber() != matrixB.getColumnsNumber()){
            return false;
        }
        for(int i = 0; i < matrixA.getRowsNumber(); i++){
            for(int j = 0; j < matrixA.getColumnsNumber(); j++){
                if(matrixA.getValue(i, j) != matrixB.getValue(i, j)){
                    return false;
                }
            }
        }
        return true;
    }
}