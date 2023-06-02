//import java.util.ArrayList;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//public final class FoxMultiplier implements IMatricesMultiplier {
//    private int poolCapacity;
//
//    public FoxMultiplier(int poolCapacity) {
//        this.poolCapacity = poolCapacity;
//    }
//
//    @Override
//    public Result multiply(Matrix matrixA, Matrix matrixB) {
//        int splitSize = (int) Math.sqrt(poolCapacity - 1) + 1;
//        Matrix[][] matricesA = IMatricesMultiplier.getSplitMatrices(matrixA, splitSize);
//        Matrix[][] matricesB = IMatricesMultiplier.getSplitMatrices(matrixB, splitSize);
//        Matrix[][] resultMatrices = new Matrix[splitSize][splitSize];
//        for (int blockI = 0; blockI < splitSize; blockI++) {
//            for (int blockJ = 0; blockJ < splitSize; blockJ++) {
//                resultMatrices[blockI][blockJ] = new Matrix(matricesA[blockI][blockJ].getRowsNumber(), matricesB[blockI][blockJ].getColumnsNumber(), false);
//            }
//        }
//
//        ExecutorService pool = Executors.newFixedThreadPool(poolCapacity);
//        ArrayList<Future<Matrix>> futureResults = new ArrayList<>();
//
//        for (int s = 0; s < splitSize; s++) {
//            for (int i = 0; i < splitSize; i++) {
//                for (int j = 0; j < splitSize; j++) {
//                    futureResults.add(pool.submit(new FoxMultiplierTask(matricesA[i][s], matricesB[s][j])));
//                }
//            }
//            for (int i = 0; i < splitSize; i++) {
//                for (int j = 0; j < splitSize; j++) {
//                    resultMatrices[i][j].addMatrix(futureResults.get(i * splitSize + j).get());
//                }
//            }
//            futureResults.clear();
//        }
//        pool.shutdown();
//        return new Result(IMatricesMultiplier.combineMatrices(resultMatrices));
//    }
//
//    @Override
//    public String getName() {
//        return "Fox";
//    }
//
//    @Override
//    public int getPoolCapacity() {
//        return poolCapacity;
//    }
//
//    @Override
//    public void setPoolCapacity(int capacity) {
//        this.poolCapacity = capacity;
//    }
//
//    @Override
//    public boolean isParallelAlgorithm() {
//        return true;
//    }
//
//    private record FoxMultiplierTask(Matrix matrixA, Matrix matrixB) implements Callable<Matrix> {
//        @Override
//        public Matrix call() {
//         return null;
////            return new StandardMultiplier().multiply(matrixA, matrixB).getMatrix();
//        }
//
//    }