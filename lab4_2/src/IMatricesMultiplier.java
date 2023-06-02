import java.util.concurrent.ExecutionException;

public interface IMatricesMultiplier {
    Result multiply(Matrix matrixA, Matrix matrixB) throws ExecutionException, InterruptedException;
    int getPoolCapacity();
    void setPoolCapacity(int capacity);
    boolean isParallelAlgorithm();

    String getName();

    static Matrix combineMatrices(Matrix[][] resultMatrices) {
        int splitSize = resultMatrices.length;
        int fullSizeI = resultMatrices[0][0].getRowsNumber();
        int fullSizeJ = resultMatrices[0][0].getColumnsNumber();
        Matrix resultMatrix = new Matrix(splitSize * fullSizeI, splitSize * fullSizeJ, false);

        for (int matrixI = 0; matrixI < splitSize; matrixI++) {
            for (int matrixJ = 0; matrixJ < splitSize; matrixJ++) {
                for (int i = 0; i < fullSizeI; i++) {
                    for (int j = 0; j < fullSizeJ; j++) {
                        resultMatrix.setValue(matrixI * fullSizeI + i, matrixJ * fullSizeJ + j, resultMatrices[matrixI][matrixJ].getValue(i, j));
                    }
                }
            }
        }
        return resultMatrix;
    }

    static Matrix[][] getSplitMatrices(Matrix matrix, int splitNumber) {
        int splitSize = (matrix.getColumnsNumber() - 1) / splitNumber + 1;
        Matrix[][] splitMatrices = new Matrix[splitNumber][splitNumber];

        for (int matrixI = 0; matrixI < splitNumber; matrixI++) {
            for (int matrixJ = 0; matrixJ < splitNumber; matrixJ++) {
                splitMatrices[matrixI][matrixJ] = new Matrix(splitSize, splitSize, false);
                for (int i = 0; i < splitSize; i++) {
                    for (int j = 0; j < splitSize; j++) {
                        if (matrixI * splitSize + i >= matrix.getRowsNumber() || matrixJ * splitSize + j >= matrix.getColumnsNumber()) {
                            splitMatrices[matrixI][matrixJ].setValue(i, j, 0);
                            continue;
                        }
                        splitMatrices[matrixI][matrixJ].setValue(i, j, matrix.getValue(matrixI * splitSize + i, matrixJ * splitSize + j));
                    }
                }
            }
        }
        return splitMatrices;
    }

    static void printMatrices(Matrix[][] resultMatrices) {
        Matrix combined = combineMatrices(resultMatrices);
        for (int i = 0; i < combined.getRowsNumber(); i++) {
            for (int j = 0; j < combined.getColumnsNumber(); j++) {
                System.out.print(combined.getValue(i, j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}