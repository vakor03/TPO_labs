import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<List<Integer>> {
    private final TextFile textFile;

    TextFileSearchTask(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    protected List<Integer> compute() {
        return getAllWordLengths(textFile);
    }

    private static List<Integer> getAllWordLengths(TextFile textFile) {
        List<Integer> wordLengths = new ArrayList<>();
        for (String line : textFile.getLines()) {
            for (String word : getAllWordsInLine(line)) {
                wordLengths.add(word.length());
            }
        }
        return wordLengths;
    }
    private static String[] getAllWordsInLine(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
}
