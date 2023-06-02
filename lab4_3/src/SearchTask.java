import java.util.ArrayList;
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
        HashSet<String> commonWords;
        List<RecursiveTask<HashSet<String>>> tasks = new ArrayList<>();

        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder);
            tasks.add(task);
            task.fork();
        }

        for (TextFile textFile : folder.getTextFiles()) {
            TextFileSearchTask task = new TextFileSearchTask(textFile);
            tasks.add(task);
            task.fork();
        }

        commonWords = tasks.get(0).join();
        for (RecursiveTask<HashSet<String>> task : tasks) {
            commonWords.retainAll(task.join());
        }

        return commonWords;
    }
}
