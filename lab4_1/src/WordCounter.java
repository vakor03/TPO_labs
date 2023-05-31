import java.util.HashMap;
import java.util.concurrent.*;

public class WordCounter {
    private final ForkJoinPool forkJoinPool;

    public WordCounter(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public static String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    public static HashMap<Integer, Integer> occurrencesCount(Document document) {
        HashMap<Integer, Integer> countLengthsMap = new HashMap<>();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                int length = word.length();
                if (countLengthsMap.containsKey(length)) {
                    countLengthsMap.put(length, countLengthsMap.get(length) + 1);
                }
                else {
                    countLengthsMap.put(length, 1);
                }
            }
        }
        return countLengthsMap;
    }

    public HashMap<Integer, Integer> occurrencesCountParallel(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

}