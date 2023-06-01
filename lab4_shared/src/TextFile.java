import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFile {
    private final List<String> lines;
    private final String name;

    public TextFile(List<String> lines, String name) {
        this.lines = lines;
        this.name = name;
    }

    public List<String> getLines() {
        return this.lines;
    }
    public String getName() {
        return this.name;
    }

    public static TextFile createFromFile(File file) throws IOException {
        String path = file.getPath() + file.getName();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        return new TextFile(lines, path);
    }
}


