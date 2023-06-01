import java.util.concurrent.ForkJoinPool;

public class FoxAlgorithmForkJoin implements IMatrixMultiplicationAlgorithm {
    private final ForkJoinPool forkJoinPool;

    public FoxAlgorithmForkJoin(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }
    @Override
    public Result multiply(Matrix matrixA, Matrix matrixB) {
        long startTime = System.currentTimeMillis();

        return new Result(forkJoinPool.invoke(new FoxAlgorithmTask(matrixA, matrixB)),
                System.currentTimeMillis() - startTime);
    }
}
