import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("TestText\\Texts");
        Folder folder = Folder.fromDirectory(file);
        CommonWordCounter commonWordSearcher = new CommonWordCounter(2);

        long startTime = System.currentTimeMillis();
        HashSet<String> commonWords = commonWordSearcher.findCommonWordsParallel(folder);
        long time = System.currentTimeMillis() - startTime;

        System.out.println(commonWords);
        System.out.println(commonWords.size());
        System.out.println("\nTime: " + time);
    }
}