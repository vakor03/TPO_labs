import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderSearchTask extends RecursiveTask<List<Integer>> {
    private final Folder folder;

    FolderSearchTask(Folder folder) {
        this.folder = folder;
    }

    @Override
    protected List<Integer> compute() {
        ArrayList<Integer> wordLengths = new ArrayList<>();
        List<RecursiveTask<List<Integer>>> tasks = new LinkedList<>();

        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder);
            tasks.add(task);
            task.fork();
        }

        for (TextFile textFile : folder.getDocuments()) {
            TextFileSearchTask task = new TextFileSearchTask(textFile);
            tasks.add(task);
            task.fork();
        }

        for (RecursiveTask<List<Integer>> task : tasks) {
            wordLengths.addAll(task.join());
        }
        return wordLengths;
    }
}
