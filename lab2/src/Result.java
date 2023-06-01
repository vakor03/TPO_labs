public class Result {
    private final long totalTime;
    private final Matrix resultMatrix;

    public Result(Matrix resultMatrix, long totalTime) {
        this.totalTime = totalTime;
        this.resultMatrix = resultMatrix;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public Matrix getResultMatrix() {
        return resultMatrix;
    }
}
