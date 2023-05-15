public class Result {
    long time;
    int[][] matrix;

    public Result(long time, int[][] matrix) {
        this.time = time;
        this.matrix = matrix;
    }

    public long getTime() {
        return time;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
