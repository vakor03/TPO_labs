import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class DocumentSearchTask extends RecursiveTask<HashMap<String, List<String>>> {
    private final Document document;
    private final List<String> wordsMustExist;

    DocumentSearchTask(Document document, List<String> wordsMustExist) {
        this.document = document;
        this.wordsMustExist = wordsMustExist;
    }

    @Override
    protected HashMap<String, List<String>> compute() {
        return ExistWordsChecker.checkExistWords(document, wordsMustExist);
    }
}
