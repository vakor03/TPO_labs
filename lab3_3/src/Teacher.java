import java.util.ArrayList;
import java.util.Random;

public class Teacher implements Runnable {
    private final Journal journal;
    private final ArrayList<Group> availableGroups;

    public Teacher(Journal journal, ArrayList<Group> availableGroups) {
        this.journal = journal;
        this.availableGroups = availableGroups;
    }

    public void run() {
        Random random = new Random();
        for (var group : availableGroups) {
            for (var student : group.getStudents()) {
                    journal.addMark(student.getId(), random.nextInt(41) + 60);
            }
        }
    }
}
