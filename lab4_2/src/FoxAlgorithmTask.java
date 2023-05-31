import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FoxAlgorithmTask extends RecursiveTask<Matrix> {

    private Matrix matrix1;
    private Matrix matrix2;

    private final int limitSizeMatrices = 100;
    private final int matrixMSize = 2;

    IMatrixMultiplicationAlgorithm standardMultiplier = new SequentialAlgorithm();

    public FoxAlgorithmTask(Matrix matrix1, Matrix matrix2) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

    @Override
    public Matrix compute() {
        if (matrix1.getColumnsCount() <= limitSizeMatrices) {
            return standardMultiplier.multiply(matrix1, matrix2);
        }

        Matrix[][] matrixM1 = FoxAlgorithm.splitMatrixIntoSmallerMatrices(matrix1, matrixMSize);
        Matrix[][] matrixM2 = FoxAlgorithm.splitMatrixIntoSmallerMatrices(matrix2, matrixMSize);

        int sizeInternalM = matrixM1[0][0].getColumnsCount();
        Matrix[][] resultMatrixM = new Matrix[matrixMSize][matrixMSize];

        for (int i = 0; i < matrixMSize; i++) {
            for (int j = 0; j < matrixMSize; j++) {
                resultMatrixM[i][j] = new Matrix(sizeInternalM, sizeInternalM);
            }
        }

        for (int k = 0; k < matrixMSize; k++) {
            List<FoxAlgorithmTask> tasks = new ArrayList<>();
            List<Matrix> calculatedSubBlocks = new ArrayList<>();

            for (int i = 0; i < matrixMSize; i++) {
                for (int j = 0; j < matrixMSize; j++) {
                    var task = new FoxAlgorithmTask(matrixM1[i][(i + k) % matrixMSize], matrixM2[(i + k) % matrixMSize][j]);
                    tasks.add(task);
                    task.fork();
                }
            }

            for (var task : tasks) {
                var subMatrix = task.join();
                calculatedSubBlocks.add(subMatrix);
            }

            for (int i = 0; i < matrixMSize; i++) {
                for (int j = 0; j < matrixMSize; j++) {
                    resultMatrixM[i][j].add(calculatedSubBlocks.get(i * matrixMSize + j));
                }
            }
        }

        return FoxAlgorithm.combineMatrixMatricesToMatrix(
                resultMatrixM, matrix1.getRowsCount(), matrix2.getColumnsCount());
    }
}