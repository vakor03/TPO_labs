import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class RequiredWordsChecker {
    private final ForkJoinPool forkJoinPool;

    public RequiredWordsChecker(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public HashMap<String, List<String>> findCommonWordsForkJoin(Folder folder, List<String> requiredWords) {
        List<String> wordsToLowerCase = new ArrayList<>();
        for (String word : requiredWords) {
            wordsToLowerCase.add(word.toLowerCase());
        }
        return forkJoinPool.invoke(new FolderSearchTask(folder, wordsToLowerCase));
    }
}