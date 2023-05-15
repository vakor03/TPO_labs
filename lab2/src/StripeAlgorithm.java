import java.util.concurrent.*;

public class StripeAlgorithm implements IMatrixMultiplicationAlgorithm {
    private final int countThread;

    public StripeAlgorithm(int countThread) {
        this.countThread = countThread;
    }

    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        int[][] transposedMatrixB = MatrixHelper.transposeMatrix(MatrixHelper.clone(matrixB));

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        Future<Integer>[] futures = new Future[result.length * result[0].length];

        for (int i = 0; i < matrixA[0].length; i++) {
            for (int j = 0; j < matrixA.length; j++) {
                int rowIndex = j;
                int colIndex = (j + i) % result[0].length;
                int curIndex = rowIndex * result.length + colIndex;

                futures[curIndex] = executor.submit(new StripeWorker(matrixA[rowIndex], transposedMatrixB[colIndex]));
            }
        }

        executor.shutdown();
        try {
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[0].length; j++) {
                    var future = futures[i * result.length + j].get();
                    result[i][j] = future;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

class StripeWorker implements Callable<Integer> {
    private final int[] row;
    private final int[] column;

    public StripeWorker(int[] row, int[] column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public Integer call() {
        int result = 0;
        for (int i = 0; i < row.length; i++) {
            result += row[i] * column[i];
        }
        return result;
    }
}