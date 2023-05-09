import java.util.HashSet;

public class BlockStripedDecompositionAlgorithm {
    public int[][] Multiply(int[][] matrixA, int[][] matrixB, int numberOfThreads) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        HashSet<Thread> threads = new HashSet<>();
        for (int i = 0; i < numberOfThreads; i++) {
            int startRow = i * matrixA.length / numberOfThreads;
            int endRow = i == numberOfThreads - 1 ? matrixA.length : (i + 1) * matrixA.length / numberOfThreads;
            Thread thread = new Thread(new BlockStripedDecompositionAlgorithmWorker(matrixA, matrixB, result, startRow, endRow));
            thread.start();
            threads.add(thread);
        }

        try {
            for (Thread thread : threads) {

                thread.join();

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}

class BlockStripedDecompositionAlgorithmWorker implements Runnable {
    private int[][] matrixA;
    private int[][] matrixB;
    private int[][] result;
    private int startRow;
    private int endRow;

    public BlockStripedDecompositionAlgorithmWorker(int[][] matrixA,
                                                    int[][] matrixB,
                                                    int[][] result,
                                                    int startRow,
                                                    int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.result = result;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                result[i][j] = 0;
                for (int k = 0; k < matrixA[0].length; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
}


