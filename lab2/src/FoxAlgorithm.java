import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FoxAlgorithm implements IMatrixMultiplicationAlgorithm {
    final int countThread;
    final IMatrixMultiplicationAlgorithm algorithmForSmallMatrices = new FasterSequentialAlgorithm();
    final int sizeMatrixM;

    public FoxAlgorithm(int countThread, int sizeMatrixM) {
        this.countThread = countThread;
        this.sizeMatrixM = sizeMatrixM;
    }

    public int[][] multiply(int[][] matrixA, int[][] matrixB) {
//        int sizeMatrixM = (int) Math.sqrt(countThread - 1) + 1;
        int blocksCount = matrixA.length / sizeMatrixM;
        int[][][][] matrixM1 = splitMatrixIntoSmallerMatrices(matrixA, blocksCount);
        int[][][][] matrixM2 = splitMatrixIntoSmallerMatrices(matrixB, blocksCount);

        int sizeInternalM = matrixM1[0][0][0].length;
        int[][][][] resultMatrixM = new int[blocksCount][blocksCount][][];
        for (int i = 0; i < blocksCount; i++) {
            for (int j = 0; j < blocksCount; j++) {
                resultMatrixM[i][j] = new int[sizeInternalM][sizeInternalM];
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(countThread);
        for (int k = 0; k < blocksCount; k++) {
            ArrayList<Future<int[][]>> futures = new ArrayList<>();
            for (int i = 0; i < blocksCount; i++) {
                for (int j = 0; j < blocksCount; j++) {
                    TaskFoxAlgorithm task = new TaskFoxAlgorithm(
                            matrixM1[i][(i + k) % blocksCount],
                            matrixM2[(i + k) % blocksCount][j],
                            resultMatrixM[i][j],
                            algorithmForSmallMatrices);
                    futures.add(executor.submit(task));
                }
            }

            for (int i = 0; i < blocksCount; i++) {
                for (int j = 0; j < blocksCount; j++) {
                    try {
                        resultMatrixM[i][j] = futures.get(i * blocksCount + j).get();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        executor.shutdown();

        return MatrixMatricesToMatrix(resultMatrixM, matrixA.length, matrixB[0].length);
    }

    private int[][][][] splitMatrixIntoSmallerMatrices(int[][] matrix, int sizeMatrixM) {
        int[][][][] matrixMatrices = new int[sizeMatrixM][sizeMatrixM][][];
        int sizeInternal = (int) ((matrix[0].length - 1) / sizeMatrixM) + 1;

        for (int i = 0; i < sizeMatrixM; i++) {
            for (int j = 0; j < sizeMatrixM; j++) {
                matrixMatrices[i][j] = new int[sizeInternal][sizeInternal];

                for (int k = 0; k < sizeInternal; k++) {
                    for (int l = 0; l < sizeInternal; l++) {
                        if (i * sizeInternal + k >= matrix.length
                                || j * sizeInternal + l >= matrix[0].length) {
                            matrixMatrices[i][j][k][l] = 0;
                        } else {
                            int element = matrix[i * sizeInternal + k][j * sizeInternal + l];
                            matrixMatrices[i][j][k][l] = element;
                        }
                    }
                }
            }
        }
        return matrixMatrices;
    }

    private int[][] MatrixMatricesToMatrix(int[][][][] matrixM, int heightMatrix, int widthMatrix) {
        int[][] resultMatrix = new int[heightMatrix][widthMatrix];

        for (int i = 0; i < matrixM.length; i++) {
            for (int j = 0; j < matrixM[i].length; j++) {

                for (int k = 0; k < matrixM[i][j].length; k++) {
                    for (int l = 0; l < matrixM[i][j][0].length; l++) {
                        if (i * matrixM[i][j].length + k < heightMatrix
                                && j * matrixM[i][j][0].length + l < widthMatrix) {
                            resultMatrix[i * matrixM[i][j].length + k][j * matrixM[i][j][0].length + l] =
                                    matrixM[i][j][k][l];

                        }
                    }
                }
            }
        }

        return resultMatrix;
    }
}

class TaskFoxAlgorithm implements Callable<int[][]> {
    private int[][] matrix1;
    private int[][] matrix2;
    private int[][] resMatrix;

    private final IMatrixMultiplicationAlgorithm multiplier;


    public TaskFoxAlgorithm(int[][] matrix1, int[][] matrix2, int[][] resMatrix, IMatrixMultiplicationAlgorithm multiplier) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.resMatrix = resMatrix;
        this.multiplier = multiplier;
    }

    @Override
    public int[][] call() {
        return MatrixHelper.addMatrices(resMatrix, multiplier.multiply(matrix1, matrix2));
    }
}