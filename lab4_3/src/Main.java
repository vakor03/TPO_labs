import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        File file = new File("C:\\Projects\\ParrallelComputing\\TPO_labs\\lab4_1/TestFolder2");
        CommonWordCounter commonWordSearcher = new CommonWordCounter(2);

        long startTime = System.currentTimeMillis();
        HashSet<String> commonWords = commonWordSearcher.findCommonWordsForkJoin(file);
        long time = System.currentTimeMillis() - startTime;

        System.out.println("Common words: " + commonWords);
        System.out.println("Count: " + commonWords.size());
        System.out.println("\nTime: " + time + " ms");
    }
}