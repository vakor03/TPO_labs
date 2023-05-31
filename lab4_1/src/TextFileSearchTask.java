import java.util.List;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<List<Integer>> {
    private final TextFile textFile;

    TextFileSearchTask(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    protected List<Integer> compute() {
        return WordCounter.getAllWordLengths(textFile);
    }
}
