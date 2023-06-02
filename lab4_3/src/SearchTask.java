import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RecursiveTask;

class SearchTask extends RecursiveTask<HashSet<String>> {
    private final File file;

    SearchTask(File file) {
        this.file = file;
    }

    @Override
    protected HashSet<String> compute() {
        if (!file.isDirectory()) {
            return getUniqueWordsInFile(file);
        }
        HashSet<String> commonWords;
        List<RecursiveTask<HashSet<String>>> tasks = new ArrayList<>();

        for (File entry : Objects.requireNonNull(file.listFiles())) {
            SearchTask task = new SearchTask(entry);
            tasks.add(task);
            task.fork();
        }

        commonWords = tasks.get(0).join();
        for (RecursiveTask<HashSet<String>> task : tasks) {
            commonWords.retainAll(task.join());
        }

        return commonWords;
    }

    private static HashSet<String> getUniqueWordsInFile(File file) {
        HashSet<String> uniqueWords = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                for (String word : getWordsIn(line)) {
                    uniqueWords.add(word.toLowerCase());
                }
                line = reader.readLine();
            }
        } catch (Exception ignored) {
        }

        return uniqueWords;
    }

    private static String[] getWordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
}
