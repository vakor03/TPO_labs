import java.util.concurrent.ForkJoinPool;

public class FoxAlgorithmForkJoin implements IMatrixMultiplicationAlgorithm {

    private final ForkJoinPool forkJoinPool;

    public FoxAlgorithmForkJoin(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }
    @Override
    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        return forkJoinPool.invoke(new FoxAlgorithmTask(matrixA, matrixB));
    }
}
