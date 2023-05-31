import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderSearchTask extends RecursiveTask<HashMap<String, List<String>>> {
    private final Folder folder;
    private final List<String> wordsMustExist;

    FolderSearchTask(Folder folder, List<String> wordsMustExist) {
        this.folder = folder;
        this.wordsMustExist = wordsMustExist;
    }


    @Override
    protected HashMap<String, List<String>> compute() {
        HashMap<String, List<String>> fileExistWords = new HashMap<>();
        List<RecursiveTask<HashMap<String, List<String>>>> tasks = new LinkedList<>();

        for (Folder subFolder : folder.getSubFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder, wordsMustExist);
            tasks.add(task);
            task.fork();
        }

        for (Document document : folder.getDocuments()) {
            DocumentSearchTask task = new DocumentSearchTask(document, wordsMustExist);
            tasks.add(task);
            task.fork();
        }


        for (RecursiveTask<HashMap<String, List<String>>> task : tasks) {
            fileExistWords.putAll(task.join());
        }

        return fileExistWords;
    }
}
