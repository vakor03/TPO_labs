import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Projects\\ParrallelComputing\\TPO_labs\\lab4_1/TestFolder1");
        Folder folder = Folder.loadFromDirectory(file);
        RequiredWordsChecker requiredWordsChecker = new RequiredWordsChecker(2);

        List<String> words = new ArrayList<>();
        words.add("Algorithm");
        words.add("Java");
        words.add("Networking");
        words.add("Parallel");
        words.add("Computer");

        long startTime = System.currentTimeMillis();
        HashMap<String, List<String>> fileAndExistWords = requiredWordsChecker.findCommonWordsForkJoin(folder, words);
        long time = System.currentTimeMillis() - startTime;

        int dontHaveRequiredWords = 0;
        int haveNotAllRequiredWords = 0;
        int haveAllRequiredWords = 0;

        for (var item: fileAndExistWords.entrySet()){
            System.out.println(item);
            if (item.getValue().size() == 0){
                dontHaveRequiredWords++;
            }
            else if (item.getValue().size() < words.size()){
                haveNotAllRequiredWords++;
            }
            else{
                haveAllRequiredWords++;
            }
        }

        System.out.println("\nDon't have required words: " + dontHaveRequiredWords);
        System.out.println("Have not all required words: " + haveNotAllRequiredWords);
        System.out.println("Have all required words: " + haveAllRequiredWords);
        System.out.println("\nTime: " + time + " ms");
    }
}