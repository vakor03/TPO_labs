import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Folder {
    private final List<Folder> subFolders;
    private final List<TextFile> documents;

    public Folder(List<Folder> subFolders, List<TextFile> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
    }
    List<TextFile> getTextFiles() {
        return this.documents;
    }

    List<Folder> getSubFolders() {
        return this.subFolders;
    }

    static Folder loadFromDirectory(File dir) throws IOException {
        List<TextFile> documents = new LinkedList<>();
        List<Folder> subFolders = new LinkedList<>();

        for (File entry : Objects.requireNonNull(dir.listFiles())) {
            if (entry.isDirectory()) {
                subFolders.add(Folder.loadFromDirectory(entry));
            } else {
                documents.add(TextFile.createFromFile(entry));
            }
        }
        return new Folder(subFolders, documents);
    }
}
