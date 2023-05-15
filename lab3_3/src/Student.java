import java.util.UUID;

public class Student {
    private final String name;
    private final String surname;
    private final UUID id;

    public Student(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
