import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

public class CommonWordCounter {
    private final ForkJoinPool forkJoinPool;

    public CommonWordCounter(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public static String[] getWordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    public HashSet<String> findCommonWordsForkJoin(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

    public static HashSet<String> getUniqueWordsInTextFile(TextFile textFile) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (String line : textFile.getLines()) {
            for (String word : getWordsIn(line)) {
                uniqueWords.add(word.toLowerCase());
            }
        }
        return uniqueWords;
    }

}