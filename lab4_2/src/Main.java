public class Main {
    public static void main(String[] args) {
        int sizeMatrix = 2000;
        int countThread = 16;
        boolean printedMatrices = false;

        System.out.print("Size matrix: ");
        System.out.println(sizeMatrix);
        System.out.print("count Thread: ");
        System.out.println(countThread);

        Matrix matrix1 = MatrixHelper.generateRandomMatrix(sizeMatrix);

        Matrix matrix2 = MatrixHelper.generateRandomMatrix(sizeMatrix);
        if(printedMatrices){
            matrix1.print();
            matrix2.print();
        }

        // Not parallel algorithm multiply matrices
        long startTime = System.currentTimeMillis();

//        Matrix resultMatrix1 = new SequentialAlgorithm().multiply(matrix1, matrix2);

        long endTime = System.currentTimeMillis();
        System.out.print("Time working standard algo: ");
        System.out.println(endTime - startTime);

        if(printedMatrices) {
//            resultMatrix1.print();
        }

        long totalTime = 0;
        int countTest = 5;

        // Fox algorithm multiply matrices
        totalTime = 0;
        Matrix resultMatrix2 = null;
        for (int i = 0; i < countTest; i++) {
            startTime = System.currentTimeMillis();
            FoxAlgorithm foxAlgorithm = new FoxAlgorithm(countThread);
            resultMatrix2 = foxAlgorithm.multiply(matrix1, matrix2);
            endTime = System.currentTimeMillis();
            totalTime += endTime - startTime;
        }


        System.out.print("Time working fox algo (FixedThreadPool): ");
        System.out.println(totalTime / countTest);

        if(printedMatrices) {
            resultMatrix2.print();
        }

        // Fox algorithm multiply matrices forkjoin
        totalTime = 0;
        Matrix resultMatrix3 = null;
        for (int i = 0; i < countTest; i++) {
            startTime = System.currentTimeMillis();
            FoxAlgorithmForkJoin foxAlgorithmForkJoin = new FoxAlgorithmForkJoin(countThread);
            resultMatrix3 = foxAlgorithmForkJoin.multiply(matrix1, matrix2);
            endTime = System.currentTimeMillis();
            totalTime += endTime - startTime;
        }

        System.out.print("Time working fox algo (ForkJoinPool): ");
        System.out.println(totalTime / countTest);

        if(printedMatrices) {
            resultMatrix3.print();
        }



//        System.out.println(resultMatrix1.equals(resultMatrix2));
//        System.out.println(resultMatrix1.equals(resultMatrix3));
    }
}