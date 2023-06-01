import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FoxAlgorithm implements IMatrixMultiplicationAlgorithm {
    final int countThread;

    public FoxAlgorithm(int countThread) {
        this.countThread = countThread;
    }

    public Result multiply(Matrix matrixA, Matrix matrixB) {
        long startTime = System.currentTimeMillis();

        int splitFactor = (int) Math.sqrt(countThread - 1) + 1;
        Matrix[][] matrixM1 = splitMatrixIntoSmallerMatrices(matrixA, splitFactor);
        Matrix[][] matrixM2 = splitMatrixIntoSmallerMatrices(matrixB, splitFactor);

        int internalMatrixSize = matrixM1[0][0].getColumnsCount();
        Matrix[][] resultMatrixM = new Matrix[splitFactor][splitFactor];
        for (int i = 0; i < splitFactor; i++) {
            for (int j = 0; j < splitFactor; j++) {
                resultMatrixM[i][j] = new Matrix(internalMatrixSize, internalMatrixSize);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        for (int k = 0; k < splitFactor; k++) {
            ArrayList<Future<Matrix>> futures = new ArrayList<>();
            for (int i = 0; i < splitFactor; i++) {
                for (int j = 0; j < splitFactor; j++) {
                    FoxAlgorithmWorker task = new FoxAlgorithmWorker(
                            matrixM1[i][(i + k) % splitFactor],
                            matrixM2[(i + k) % splitFactor][j],
                            resultMatrixM[i][j]);

                    futures.add(executor.submit(task));
                }
            }

            for (int i = 0; i < splitFactor; i++) {
                for (int j = 0; j < splitFactor; j++) {
                    try {
                        resultMatrixM[i][j] = futures.get(i * splitFactor + j).get();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        executor.shutdown();
        Matrix resultMatrix = combineMatrixMatricesToMatrix(resultMatrixM, matrixA.getRowsCount(),
                matrixB.getColumnsCount());
        return new Result(resultMatrix, System.currentTimeMillis() - startTime);
    }

    public static Matrix[][] splitMatrixIntoSmallerMatrices(Matrix matrix, int splitFactor) {
        Matrix[][] matrixMatrices = new Matrix[splitFactor][splitFactor];
        int sizeInternal = (int) ((matrix.getColumnsCount() - 1) / splitFactor) + 1;

        for (int i = 0; i < splitFactor; i++) {
            for (int j = 0; j < splitFactor; j++) {
                matrixMatrices[i][j] = new Matrix(sizeInternal, sizeInternal);

                for (int k = 0; k < sizeInternal; k++) {
                    for (int l = 0; l < sizeInternal; l++) {
                        if (i * sizeInternal + k >= matrix.getRowsCount()
                                || j * sizeInternal + l >= matrix.getColumnsCount()) {
                            matrixMatrices[i][j].set(k, l, 0);
                        } else {
                            int element = matrix.get(i * sizeInternal + k, j * sizeInternal + l);
                            matrixMatrices[i][j].set(k, l, element);
                        }
                    }
                }
            }
        }
        return matrixMatrices;
    }

    public static Matrix combineMatrixMatricesToMatrix(Matrix[][] matrixM, int rowsCount, int columnsCount) {
        Matrix resultMatrix = new Matrix(rowsCount, columnsCount);

        for (int i = 0; i < matrixM.length; i++) {
            for (int j = 0; j < matrixM[i].length; j++) {

                for (int k = 0; k < matrixM[i][j].getRowsCount(); k++) {
                    for (int l = 0; l < matrixM[i][j].getColumnsCount(); l++) {

                        if (i * matrixM[i][j].getRowsCount() + k < rowsCount && j * matrixM[i][j].getColumnsCount() + l < columnsCount) {

                            resultMatrix.set(i * matrixM[i][j].getRowsCount() + k, j * matrixM[i][j].getColumnsCount() + l,
                                    matrixM[i][j].get(k, l));

                        }
                    }
                }
            }
        }

        return resultMatrix;
    }
}

class FoxAlgorithmWorker implements Callable<Matrix> {
    private Matrix matrix1;
    private Matrix matrix2;
    private Matrix resMatrix;
    private IMatrixMultiplicationAlgorithm algorithm = new FasterSequentialAlgorithm();

    public FoxAlgorithmWorker(Matrix matrix1, Matrix matrix2, Matrix resMatrix) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.resMatrix = resMatrix;
    }
    @Override
    public Matrix call() {
//        resMatrix.add(algorithm.multiply(matrix1, matrix2).getResultMatrix());
        resMatrix.add(matrix1.multiply(matrix2));
        return resMatrix;
    }
}