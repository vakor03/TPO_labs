import java.util.ArrayList;
import java.util.concurrent.*;

public class StripeAlgorithm {
    private final int countThread;

    public StripeAlgorithm(int countThread) {
        this.countThread = countThread;
    }

    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        int[][] transposedMatrixB = MatrixHelper.transposeMatrix(MatrixHelper.clone(matrixB));

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        ArrayList<StripeThread> callables = new ArrayList<>();
        ArrayList<Future<Integer>> futures = new ArrayList<>();

        int iterationsCount = matrixA.length;
        for (int i = 0; i < iterationsCount; i++) { // TODO:check var in for
            for (int j = 0; j < matrixA.length; j++) {
                int rowIndex = j;
                int colIndex = (j + i) % matrixA.length; //TODO: same here
                callables.add(
                        new StripeThread(matrixA[rowIndex],
                                transposedMatrixB[colIndex]));
            }

            try {
                futures.addAll(executor.invokeAll(callables));
                callables.clear();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
        try {
//            for (int i = 0; i < result.length; i++) {
//                for (int j = 0; j < result[0].length; j++) {
//                    int index = ((j - i) >= 0 ? (j - i) * iterationsCount : (j - i + matrixA.length) * iterationsCount) + i;
//                    result[i][j] = futures.get(index).get();
//                }
//            }
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[0].length; j++) {
                    var future = futures.get(i * result.length + j);
                    int idxRow = (j + i) % result.length;
                    result[idxRow][j] = future.get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

class StripeThread implements Callable<Integer> {
    private final int[] row;
    private final int[] column;

    public StripeThread(int[] row, int[] column) {
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

