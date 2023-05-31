import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

class DocumentSearchTask extends RecursiveTask<HashSet<String>> {
    private final Document document;

    DocumentSearchTask(Document document) {
        this.document = document;
    }

    @Override
    protected HashSet<String> compute() {
        return CommonWordCounter.getUniqueWordsInDocument(document);
    }
}
