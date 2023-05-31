import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<HashSet<String>> {
    private final TextFile textFile;

    TextFileSearchTask(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    protected HashSet<String> compute() {
        return CommonWordCounter.getUniqueWordsInTextFile(textFile);
    }
}
