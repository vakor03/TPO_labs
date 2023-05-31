import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderSearchTask extends RecursiveTask<HashSet<String>> {
    private final Folder folder;

    FolderSearchTask(Folder folder) {
        this.folder = folder;
    }


    @Override
    protected HashSet<String> compute() {
        HashSet<String> commonWord;
        List<RecursiveTask<HashSet<String>>> tasks = new LinkedList<>();

        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder);
            tasks.add(task);
            task.fork();
        }

        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(document);
            tasks.add(task);
            task.fork();
        }

        commonWord = tasks.get(0).join();
        for (RecursiveTask<HashSet<String>> task : tasks) {
            commonWord.retainAll(task.join());
        }

        return commonWord;
    }
}
