import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FoxAlgorithm implements IMatrixMultiplicationAlgorithm {
    final int countThread;
    final IMatrixMultiplicationAlgorithm algorithmForSmallMatrices = new SequentialAlgorithm();
//    final int sizeMatrixM;

    public FoxAlgorithm(int countThread/*, int sizeMatrixM*/) {
        this.countThread = countThread;
//        this.sizeMatrixM = sizeMatrixM;
    }

    public Matrix multiply(Matrix matrixA, Matrix matrixB) {
        int sizeMatrixM = (int) Math.sqrt(countThread - 1) + 1;
//        int blocksCount = matrixA.getRowsCount() / sizeMatrixM;
        Matrix[][] matrixM1 = splitMatrixIntoSmallerMatrices(matrixA, sizeMatrixM);
        Matrix[][] matrixM2 = splitMatrixIntoSmallerMatrices(matrixB, sizeMatrixM);

        int sizeInternalM = matrixM1[0][0].getColumnsCount();
        Matrix[][] resultMatrixM = new Matrix[sizeMatrixM][sizeMatrixM];
        for (int i = 0; i < sizeMatrixM; i++) {
            for (int j = 0; j < sizeMatrixM; j++) {
                resultMatrixM[i][j] = new Matrix(sizeInternalM, sizeInternalM);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        for (int k = 0; k < sizeMatrixM; k++) {
            ArrayList<Future<Matrix>> futures = new ArrayList<>();
            for (int i = 0; i < sizeMatrixM; i++) {
                for (int j = 0; j < sizeMatrixM; j++) {
                    TaskFoxAlgorithm task = new TaskFoxAlgorithm(
                            matrixM1[i][(i + k) % sizeMatrixM],
                            matrixM2[(i + k) % sizeMatrixM][j],
                            resultMatrixM[i][j],
                            algorithmForSmallMatrices);
                    futures.add(executor.submit(task));
                }
            }

            for (int i = 0; i < sizeMatrixM; i++) {
                for (int j = 0; j < sizeMatrixM; j++) {
                    try {
                        resultMatrixM[i][j] = futures.get(i * sizeMatrixM + j).get();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        executor.shutdown();

        return combineMatrixMatricesToMatrix(resultMatrixM, matrixA.getRowsCount(), matrixB.getColumnsCount());
    }

    public static Matrix[][] splitMatrixIntoSmallerMatrices(Matrix matrix, int sizeMatrixM) {
        Matrix[][] matrixMatrices = new Matrix[sizeMatrixM][sizeMatrixM];
        int sizeInternal = (int) ((matrix.getColumnsCount() - 1) / sizeMatrixM) + 1;

        for (int i = 0; i < sizeMatrixM; i++) {
            for (int j = 0; j < sizeMatrixM; j++) {
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

class TaskFoxAlgorithm implements Callable<Matrix> {
    private Matrix matrix1;
    private Matrix matrix2;
    private Matrix resMatrix;

    private final IMatrixMultiplicationAlgorithm multiplier;


    public TaskFoxAlgorithm(Matrix matrix1, Matrix matrix2, Matrix resMatrix, IMatrixMultiplicationAlgorithm multiplier) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.resMatrix = resMatrix;
        this.multiplier = multiplier;
    }

    @Override
    public Matrix call() {
        resMatrix.add(multiplier.multiply(matrix1, matrix2));
        return resMatrix;
    }
}