import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FoxAlgorithmTask extends RecursiveTask<Matrix> {

    private Matrix matrix1;
    private Matrix matrix2;
    private final int matrixSizeLimit = 100;
    private final int splitFactor = 2;

    public FoxAlgorithmTask(Matrix matrix1, Matrix matrix2) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
    }

    @Override
    public Matrix compute() {
        if (matrix1.getColumnsCount() <= matrixSizeLimit) {
            return matrix1.multiply(matrix2);
        }

        Matrix[][] matrixM1 = FoxAlgorithm.splitMatrixIntoSmallerMatrices(matrix1, splitFactor);
        Matrix[][] matrixM2 = FoxAlgorithm.splitMatrixIntoSmallerMatrices(matrix2, splitFactor);

        int internalMatrixSize = matrixM1[0][0].getColumnsCount();
        Matrix[][] resultMatrixM = new Matrix[splitFactor][splitFactor];

        for (int i = 0; i < splitFactor; i++) {
            for (int j = 0; j < splitFactor; j++) {
                resultMatrixM[i][j] = new Matrix(internalMatrixSize, internalMatrixSize);
            }
        }

        for (int k = 0; k < splitFactor; k++) {
            List<FoxAlgorithmTask> tasks = new ArrayList<>();
            List<Matrix> calculatedSubBlocks = new ArrayList<>();

            for (int i = 0; i < splitFactor; i++) {
                for (int j = 0; j < splitFactor; j++) {
                    var task = new FoxAlgorithmTask(
                            matrixM1[i][(i + k) % splitFactor],
                            matrixM2[(i + k) % splitFactor][j]);

                    tasks.add(task);
                    task.fork();
                }
            }

            for (var task : tasks) {
                var subMatrix = task.join();
                calculatedSubBlocks.add(subMatrix);
            }

            for (int i = 0; i < splitFactor; i++) {
                for (int j = 0; j < splitFactor; j++) {
                    resultMatrixM[i][j].add(calculatedSubBlocks.get(i * splitFactor + j));
                }
            }
        }

        return FoxAlgorithm.combineMatrixMatricesToMatrix(
                resultMatrixM, matrix1.getRowsCount(), matrix2.getColumnsCount());
    }
}