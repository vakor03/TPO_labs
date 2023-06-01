import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class TextFileSearchTask extends RecursiveTask<HashMap<String, List<String>>> {
    private final TextFile textFile;
    private final List<String> wordsMustExist;

    TextFileSearchTask(TextFile textFile, List<String> wordsMustExist) {
        this.textFile = textFile;
        this.wordsMustExist = wordsMustExist;
    }

    @Override
    protected HashMap<String, List<String>> compute() {
        return checkExistWords(textFile, wordsMustExist);
    }

    private static HashMap<String, List<String>> checkExistWords(TextFile textFile, List<String> requiredWords) {
        HashSet<String> uniqueWords = new HashSet<>();
        for (String line : textFile.getLines()) {
            for (String word : getWordsInLine(line)) {
                uniqueWords.add(word.toLowerCase());
            }
        }

        List<String> matchedWords = new ArrayList<>(/*requiredWords*/);
        matchedWords.addAll(requiredWords);
        System.out.println("matchedWords: " + matchedWords);
        matchedWords.retainAll(uniqueWords);
        System.out.println(uniqueWords);

        HashMap<String, List<String>> map = new HashMap<>();
        map.put(textFile.getName(), matchedWords);

        return map;
    }

    private static String[] getWordsInLine(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
}
