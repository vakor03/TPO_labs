import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class RequiredWordsChecker {
    private final ForkJoinPool forkJoinPool;

    public RequiredWordsChecker(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public static String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    public HashMap<String, List<String>> findCommonWordsForkJoin(Folder folder, List<String> requiredWords) {
        List<String> wordsToLowerCase = new ArrayList<>();
        for (String word : requiredWords) {
            wordsToLowerCase.add(word.toLowerCase());
        }
        return forkJoinPool.invoke(new FolderSearchTask(folder, wordsToLowerCase));
    }

    public static HashMap<String, List<String>> checkExistWords(TextFile textFile, List<String> requiredWords) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (String line : textFile.getLines()) {
            for (String word : wordsIn(line)) {
                uniqueWords.add(word.toLowerCase());
            }
        }

        List<String> matchedWords = new ArrayList<>(requiredWords);
        matchedWords.retainAll(uniqueWords);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put(textFile.getName(), matchedWords);

        return map;
    }

}