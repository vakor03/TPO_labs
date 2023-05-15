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
        ArrayList<WaitAndAddThread> callables = new ArrayList<>();
        Future<Integer>[] futures = new Future[matrixA.length * matrixA.length];
        ArrayList<Future<?>> futures2 = new ArrayList<>();

        int iterationsCount = matrixA.length;
        for (int i = 0; i < iterationsCount; i++) { // TODO:check var in for
            for (int j = 0; j < matrixA.length; j++) {
                int rowIndex = j;
                int colIndex = (j + i) % matrixA.length; //TODO: same here
                int curIndex = rowIndex * matrixA.length + colIndex;
                Future<Integer> previousRowUsage = curIndex % matrixA.length == rowIndex ? null : curIndex % matrixA.length - 1 < 0 ? futures[curIndex + matrixA.length - 1] : futures[curIndex - 1];
                Future<Integer> previousColumnUsage = curIndex / matrixA.length == colIndex ? null : curIndex + matrixA.length < matrixA.length * matrixA.length ? futures[curIndex + matrixA.length] : futures[curIndex - matrixA.length * (matrixA.length - 1)];

                callables.add(new WaitAndAddThread(previousRowUsage, previousColumnUsage, executor, new StripeThread(matrixA[rowIndex], transposedMatrixB[colIndex]), futures, curIndex));
            }

            for (WaitAndAddThread callable : callables) {
                futures2.add(executor.submit(callable));
            }
            callables.clear();
            try {
                for (Future<?> future : futures2) {
                    future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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

class WaitAndAddThread implements Runnable {

    private final Future<Integer> previousRowUsage;
    private final Future<Integer> previousColumnUsage;
    private final ExecutorService executorService;
    private final StripeThread currentStripeThread;
    private final Future<Integer>[] futures;
    private final int i;

    public WaitAndAddThread(Future<Integer> previousRowUsage, Future<Integer> previousColumnUsage, ExecutorService executorService, StripeThread currentStripeThread, Future<Integer>[] futures, int i) {
        this.previousRowUsage = previousRowUsage;
        this.previousColumnUsage = previousColumnUsage;
        this.executorService = executorService;
        this.currentStripeThread = currentStripeThread;
        this.futures = futures;
        this.i = i;
    }

    @Override
    public void run() {
        try {
            if (previousRowUsage != null) {
                previousRowUsage.get();
            }
            if (previousColumnUsage != null) {
                previousColumnUsage.get();
            }
//            futures.add(executorService.submit(currentStripeThread));
            futures[i] = executorService.submit(currentStripeThread);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException();
        }
    }
}

