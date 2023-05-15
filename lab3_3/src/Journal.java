import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Journal {
    private ArrayList<Group> groups = new ArrayList<>();
    private final HashMap<UUID, ArrayList<Integer>> marks = new HashMap<>();
    private String name;

    public Journal(ArrayList<Group> groups, String name) {
        this.groups = groups;
        this.name = name;
    }

    public Journal(String name) {
        this.name = name;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public String getName() {
        return name;
    }

    public synchronized void addMark(UUID studentId, int mark) {
        if (marks.containsKey(studentId)) {
            marks.get(studentId).add(mark);
        } else {
            ArrayList<Integer> marksList = new ArrayList<>();
            marksList.add(mark);
            marks.put(studentId, marksList);
        }
    }

    public void test() {
        for (var group : groups) {
            System.out.println("----------------");
            System.out.println(group.getName());
            for (var student : group.getStudents()) {
                System.out.print("Name: " + student.getName());
                System.out.print("  Marks: " + marks.get(student.getId()));
                System.out.println("  Marks count: "+marks.get(student.getId()).size());
            }
        }
    }
}
