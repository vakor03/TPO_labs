import java.io.File;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class WordCounter {
    private final ForkJoinPool forkJoinPool;

    public WordCounter(int threadsCount) {
        forkJoinPool = new ForkJoinPool(threadsCount);
    }

    public List<Integer> getAllWordLenghtsForkJoin(File file) {
        return forkJoinPool.invoke(new SearchTask(file));
    }
}