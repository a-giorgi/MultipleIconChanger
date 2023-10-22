import java.io.File;

public class RecursiveDirectory {
    private final int recursion;
    private final File directory;

    public RecursiveDirectory(File directory, int recursion) {
        this.recursion = recursion;
        this.directory = directory;
    }

    public int getRecursion() {
        return recursion;
    }

    public File getDirectory() {
        return directory;
    }




}
