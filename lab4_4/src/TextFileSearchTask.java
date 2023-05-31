import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<HashMap<String, List<String>>> {
    private final TextFile textFile;
    private final List<String> wordsMustExist;

    TextFileSearchTask(TextFile textFile, List<String> wordsMustExist) {
        this.textFile = textFile;
        this.wordsMustExist = wordsMustExist;
    }

    @Override
    protected HashMap<String, List<String>> compute() {
        return RequiredWordsChecker.checkExistWords(textFile, wordsMustExist);
    }
}
