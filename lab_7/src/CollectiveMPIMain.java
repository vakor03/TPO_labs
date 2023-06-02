//import mpi.MPI;
//
//public class CollectiveMPIMain {
//
//    public static void main(String[] args){
//        int size = 3000;
//        Matrix matrix1 = MatrixHelper.generateRandomMatrix(size);
//        Matrix matrix2 = MatrixHelper.generateRandomMatrix(size);
//
//        CollectiveMPI collectiveMPI = new CollectiveMPI(args);
//        Result collectiveMPIResult = collectiveMPI.multiply(matrix1, matrix2);
//        if (collectiveMPIResult == null) {
//            return;
//        }
//        System.out.println("Collective MPI: ");
//        System.out.println("Matrix size: " + size);
//        System.out.println("Processors count: " + MPI.COMM_WORLD.Size());
////            System.out.println(collectiveMPIResult.getResultMatrix().equals(matrix1.multiply(matrix2)) ?
////                    "Result is Correct" : "Result is Incorrect");
//        System.out.println("Total time: " + collectiveMPIResult.getTotalTime());
//    }
//}
