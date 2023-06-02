import java.io.File;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

public class CommonWordCounter {
    private final ForkJoinPool forkJoinPool;

    public CommonWordCounter(int countThreads) {
        forkJoinPool = new ForkJoinPool(countThreads);
    }

    public HashSet<String> findCommonWordsForkJoin(File file) {
        return forkJoinPool.invoke(new SearchTask(file));
    }
}
