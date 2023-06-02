public class Result {
    private final Matrix matrix;

    public Result(int rows, int columns) {
        matrix = new Matrix(rows, columns, false);
    }

    public Result(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setValue(int row, int col, int value) {
        matrix.setValue(row, col, value);
    }

    public Matrix getMatrix() {
        return matrix.getMatrixCopy();
    }

    public void print() {
        matrix.print();
    }
}