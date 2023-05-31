import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ExistWordsChecker {
    private final ForkJoinPool forkJoinPool;

    public ExistWordsChecker(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public static String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    public HashMap<String, List<String>> findCommonWordsParallel(Folder folder, List<String> wordsMustExist) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, wordsMustExist));
    }

    public static HashMap<String, List<String>> checkExistWords(Document document, List<String> wordsMustExist) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                uniqueWords.add(word.toLowerCase());
            }
        }

        List<String> matchedWords = new ArrayList<>();
        matchedWords.addAll(wordsMustExist);
        matchedWords.retainAll(uniqueWords);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put(document.getName(), matchedWords);

        return map;
    }

}