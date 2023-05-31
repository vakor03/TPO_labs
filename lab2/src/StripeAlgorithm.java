import java.util.concurrent.*;

public class StripeAlgorithm implements IMatrixMultiplicationAlgorithm {
    private final int countThread;

    public StripeAlgorithm(int countThread) {
        this.countThread = countThread;
    }

    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        Matrix result =new Matrix(matrixA.getRowsCount(),matrixB.getColumnsCount());
        Matrix transposedMatrixB = matrixB.clone().transpose();

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        Future<Integer>[] futures = new Future[result.getRowsCount() * result.getColumnsCount()];

        for (int i = 0; i < matrixA.getColumnsCount(); i++) {
            for (int j = 0; j < matrixA.getRowsCount(); j++) {
                int rowIndex = j;
                int colIndex = (j + i) % result.getColumnsCount();
                int curIndex = rowIndex * result.getRowsCount() + colIndex;

                futures[curIndex] = executor.submit(new StripeWorker(matrixA.getRow(rowIndex), transposedMatrixB.getRow(colIndex)));
            }
        }

        executor.shutdown();
        try {
            for (int i = 0; i < result.getRowsCount(); i++) {
                for (int j = 0; j < result.getColumnsCount(); j++) {
                    var future = futures[i * result.getRowsCount() + j].get();
                    result.set(i,j,future);
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

