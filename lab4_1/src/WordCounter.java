import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class WordCounter {
    private final ForkJoinPool forkJoinPool;

    public WordCounter(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public static String[] getAllWordsInLine(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    public static List<Integer> getAllWordLengths(TextFile textFile) {
        List<Integer> wordLengths = new ArrayList<>();
        for (String line : textFile.getLines()) {
            for (String word : getAllWordsInLine(line)) {
                wordLengths.add(word.length());
            }
        }
        return wordLengths;
    }

    public List<Integer> getAllWordLenghtsForkJoin(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

}