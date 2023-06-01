import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<HashSet<String>> {
    private final TextFile textFile;

    TextFileSearchTask(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    protected HashSet<String> compute() {
        return getUniqueWordsInTextFile(textFile);
    }

    private static HashSet<String> getUniqueWordsInTextFile(TextFile textFile) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (String line : textFile.getLines()) {
            for (String word : getWordsIn(line)) {
                uniqueWords.add(word.toLowerCase());
            }
        }
        return uniqueWords;
    }

    private static String[] getWordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

}
