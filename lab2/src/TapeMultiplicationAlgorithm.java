import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TapeMultiplicationAlgorithm {
    public int[][] Multiply(int[][] matrixA, int[][] matrixB, int numberOfThreads) {
        int[][] result = new int[matrixA.length][matrixB[0].length];
        HashSet<Thread> threads = new HashSet<>();
        ArrayList<int[][]> arrayParts = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            int startRow = i * matrixA.length / numberOfThreads;
            int endRow = i == numberOfThreads - 1 ? matrixA.length : (i + 1) * matrixA.length / numberOfThreads;
            int[][] slicedMatrixA = sliceMatrix(matrixA, startRow, endRow);
            int[][] resultPart = new int[slicedMatrixA.length][matrixB[0].length];
            Thread thread = new Thread(new TapeMultiplicationWorker(slicedMatrixA, matrixB, resultPart));
            arrayParts.add(resultPart);
            thread.start();
            threads.add(thread);
        }

        try {
            for (Thread thread : threads) {

                thread.join();

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < arrayParts.size(); i++) {
            int startRow = i * matrixA.length / numberOfThreads;
            int endRow = i == numberOfThreads - 1 ? matrixA.length : (i + 1) * matrixA.length / numberOfThreads;
            int[][] arrayPart = arrayParts.get(i);
            for(int j = startRow; j < endRow; j++){
                for(int k = 0; k < matrixB[0].length; k++){
                    result[j][k] = arrayPart[j - startRow][k];
                }
            }
        }

        return result;
    }

    private int[][] sliceMatrix(int[][] matrix, int startRow, int endRow){
        int[][] result = new int[endRow - startRow][matrix[0].length];
        for(int i = startRow; i < endRow; i++){
            for(int j = 0; j < matrix[0].length; j++){
                result[i - startRow][j] = matrix[i][j];
            }
        }
        return result;
    }
}

class TapeMultiplicationWorker implements Runnable{

    private int[][] matrixA;
    private int[][] matrixB;
    private int[][] result;

    public TapeMultiplicationWorker(int[][] matrixA, int[][] matrixB, int[][] result) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.result = result;
    }

    @Override
    public void run() {
        MatrixMultiplication.PerformSequentialAlgorithm(matrixA, matrixB, result);
    }
}
