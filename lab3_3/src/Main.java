import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Group group = new Group("Group 1");
        Group group2 = new Group("Group 2");
        Group group3 = new Group("Group 3");
        group.randomFill(3);
        group2.randomFill(4);
        group3.randomFill(3);
        Journal journal = new Journal(new ArrayList<>(Arrays.asList(group, group2, group3)), "Journal");

        var lecturer = new Teacher(journal, new ArrayList<>(Arrays.asList(group, group2, group3)));
        var teacher1 = new Teacher(journal, new ArrayList<>(Arrays.asList(group, group2)));
        var teacher2 = new Teacher(journal, new ArrayList<>(Arrays.asList(group2, group3)));
        var teacher3 = new Teacher(journal, new ArrayList<>(Arrays.asList(group, group3)));

        int weeksCount = 3;
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < weeksCount; i++) {
            threads.add(new Thread(lecturer));
            threads.add(new Thread(teacher1));
            threads.add(new Thread(teacher2));
            threads.add(new Thread(teacher3));

            for (Thread thread : threads) {
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException ignored) {
            }

            System.out.println("Week " + (i + 1));
            journal.test();
            Thread.sleep(10000);
            threads.clear();
        }
    }
}

