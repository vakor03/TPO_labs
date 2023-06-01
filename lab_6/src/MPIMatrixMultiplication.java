import mpi.MPI;

public class MPIMatrixMultiplication {
    public static void main(String[] args) {
        int size = 3000;
        boolean blocking = true;
        Matrix matrix1 = MatrixHelper.generateRandomMatrix(size);
        Matrix matrix2 = MatrixHelper.generateRandomMatrix(size);

        if (blocking){
            BlockingMPI blockingMPI = new BlockingMPI(args);
            Result blockingMPIResult = blockingMPI.multiply(matrix1, matrix2);
            if (blockingMPIResult == null) {
                return;
            }
            System.out.println("Blocking MPI: ");
            System.out.println("Matrix size: " + size);
            System.out.println("Processors count: " + MPI.COMM_WORLD.Size());
//            System.out.println(blockingMPIResult.getResultMatrix().equals(matrix1.multiply(matrix2)) ?
//                    "Result is Correct" : "Result is Incorrect");
            System.out.println("Total time: " + blockingMPIResult.getTotalTime());
        } else {
            NonBlockingMPI nonBlockingMPI = new NonBlockingMPI(args);
            Result nonBlockingMPIResult = nonBlockingMPI.multiply(matrix1, matrix2);
            if (nonBlockingMPIResult == null) {
                return;
            }

            System.out.println("Non-Blocking MPI: ");
            System.out.println("Matrix size: " + size);
            System.out.println("Processors count: " + MPI.COMM_WORLD.Size());
//            System.out.println(nonBlockingMPIResult.getResultMatrix().equals(matrix1.multiply(matrix2)) ?
//                    "Result is Correct" : "Result is Incorrect");
            System.out.println("Total time: " + nonBlockingMPIResult.getTotalTime());
        }

    }
}
