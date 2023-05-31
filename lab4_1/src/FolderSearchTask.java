import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class FolderSearchTask extends RecursiveTask<HashMap<Integer, Integer>> {
    private final Folder folder;

    FolderSearchTask(Folder folder) {
        this.folder = folder;
    }

    HashMap<Integer, Integer> mergeMaps(HashMap<Integer,Integer> map1, HashMap<Integer,Integer> map2){
        for (Integer key : map2.keySet()) {
            map1.merge(key, map2.get(key), Integer::sum);
        }
        return map1;
    }

    @Override
    protected HashMap<Integer, Integer> compute() {
        HashMap<Integer, Integer> countLengthsMap = new HashMap<>();
        List<RecursiveTask<HashMap<Integer, Integer>>> tasks = new LinkedList<>();

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

        for (RecursiveTask<HashMap<Integer, Integer>> task : tasks) {
            countLengthsMap = mergeMaps(countLengthsMap, task.join());
        }
        return countLengthsMap;
    }
}
