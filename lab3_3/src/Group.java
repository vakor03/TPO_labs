import java.util.ArrayList;

public class Group {
    private ArrayList<Student> students = new ArrayList<>();
    private String name;

    public Group(ArrayList<Student> students, String name) {
        this.students = students;
        this.name = name;
    }

    public Group(String name) {
        this.name = name;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public String getName() {
        return name;
    }
    public void randomFill(int count) {
        for (int i = 0; i < count; i++) {
            students.add(new Student("Name" + i, "Surname" + i));
        }
    }
}

