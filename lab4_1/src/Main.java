import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        int[] threadsCount = {2, 4, 6, 8, 9};
        for (var count : threadsCount) {
            System.out.println("-------------------------");
            System.out.println("Threads count: " + count);
                        testResult(count);
        }
    }

    private static void testResult(int threadsCount) throws IOException {
        File file = new File("C:\\Projects\\ParrallelComputing\\TPO_labs\\lab4_1/TestFolder1");
        System.out.println(file.getAbsolutePath());
        Folder folder = Folder.loadFromDirectory(file);
        WordCounter wordCounter = new WordCounter(threadsCount);

        long startTime = System.currentTimeMillis();
        List<Integer> wordLengths = wordCounter.getAllWordLenghtsForkJoin(folder);
        long totalTime = System.currentTimeMillis() - startTime;

        int totalLength = 0;
        int count = 0;
        for (var length : wordLengths) {
            totalLength += length;
            count++;
        }
        double meanLength = (double) totalLength / count;

        int totalSquaredLength = 0;
        for (var length : wordLengths) {
            totalSquaredLength += Math.pow(length, 2);
        }

        double D = ((double) totalSquaredLength / count) - Math.pow(meanLength, 2);
        double G = Math.sqrt(D);

        System.out.println("Count: " + count);
        System.out.println("Mean length: " + meanLength);
        System.out.println("Dispersion: " + D);
        System.out.println("Mean square deviation: " + G);
        System.out.println("\nTime: " + totalTime + " ms");
    }
}