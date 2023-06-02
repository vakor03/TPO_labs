import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FoxAlgorithmTask extends RecursiveTask<Matrix> {

    private Matrix matrixA;
    private Matrix matrixB;
    private final int matrixSizeLimit = 100;
    private final int splitSize = 2;

    public FoxAlgorithmTask(Matrix matrixA, Matrix matrixB) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
    }

    @Override
    public Matrix compute() {
        if (matrixA.getColumnsNumber() <= matrixSizeLimit) {
            return matrixA.multiply(matrixB);
        }
        Matrix[][] matricesA = IMatricesMultiplier.getSplitMatrices(matrixA, splitSize);
        Matrix[][] matricesB = IMatricesMultiplier.getSplitMatrices(matrixB, splitSize);

        Matrix[][] resultMatrices = new Matrix[splitSize][splitSize];
        for (int blockI = 0; blockI < splitSize; blockI++) {
            for (int blockJ = 0; blockJ < splitSize; blockJ++) {
                resultMatrices[blockI][blockJ] = new Matrix(matricesA[blockI][blockJ].getRowsNumber(), matricesB[blockI][blockJ].getColumnsNumber(), false);
            }
        }


        for (int s = 0; s < splitSize; s++) {
            List<FoxAlgorithmTask> tasks = new ArrayList<>();
            List<Matrix> calculatedSubBlocks = new ArrayList<>();

            for (int i = 0; i < this.splitSize; i++) {
                for (int j = 0; j < this.splitSize; j++) {
                    var task = new FoxAlgorithmTask(
                            matricesA[i][s],
                            matricesB[s][j]);

                    tasks.add(task);
                    task.fork();
                }
            }

            for (var task : tasks) {
                var subMatrix = task.join();
                calculatedSubBlocks.add(subMatrix);
            }

            for (int i = 0; i < this.splitSize; i++) {
                for (int j = 0; j < this.splitSize; j++) {
                    resultMatrices[i][j].addMatrix(calculatedSubBlocks.get(i * splitSize + j));
                }
            }
        }

        return IMatricesMultiplier.combineMatrices(resultMatrices);
    }
}

