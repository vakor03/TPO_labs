import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("TestText\\Texts");
        Folder folder = Folder.fromDirectory(file);
        ExistWordsChecker existWordsChecker = new ExistWordsChecker(2);

        ArrayList<String> words = new ArrayList<>();
        words.add("olexandr");
        words.add("lorem");
        words.add("unique11");
        words.add("unique22");
        words.add("unique33");
        words.add("unique44");

        long startTime = System.currentTimeMillis();
        HashMap<String, List<String>> fileAndExistWords = existWordsChecker.findCommonWordsParallel(folder, words);
        long time = System.currentTimeMillis() - startTime;

        for (var item: fileAndExistWords.entrySet()){
            System.out.println(item);
        }
        System.out.println(fileAndExistWords.size());
        System.out.println("\nTime: " + time);
    }
}