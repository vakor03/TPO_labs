import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.RecursiveTask;

class SearchTask extends RecursiveTask<HashMap<String, List<String>>> {
    private final File file;
    private final List<String> requiredWords;

    SearchTask(File file, List<String> requiredWords) {
        this.file = file;
        this.requiredWords = requiredWords;
    }

    @Override
    protected HashMap<String, List<String>> compute() {
        if (!file.isDirectory()) {
            return checkRequiredWords(file, requiredWords);
        }
        HashMap<String, List<String>> filesWithRequiredWords = new HashMap<>();
        List<RecursiveTask<HashMap<String, List<String>>>> tasks = new ArrayList<>();

        for (File entry : Objects.requireNonNull(file.listFiles())) {
            SearchTask task = new SearchTask(entry, requiredWords);
            tasks.add(task);
            task.fork();
        }

        for (RecursiveTask<HashMap<String, List<String>>> task : tasks) {
            filesWithRequiredWords.putAll(task.join());
        }

        return filesWithRequiredWords;
    }

    private static HashMap<String, List<String>> checkRequiredWords(File file, List<String> requiredWords) {
        HashSet<String> uniqueWords = new HashSet<>();
        String fileName = file.getPath() + file.getName();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                for (String word : getWordsInLine(line)) {
                    uniqueWords.add(word.toLowerCase());
                }
                line = reader.readLine();
            }
        } catch (Exception ignored) {
        }

        List<String> matchedWords = new ArrayList<>(requiredWords);
        matchedWords.retainAll(uniqueWords);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put(fileName, matchedWords);

        return map;
    }

    private static String[] getWordsInLine(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
}
