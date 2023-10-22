import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LinuxIconChanger extends IconChanger{
    @Override
    public void setIcon(File directoryTarget, File icon) throws IOException,InterruptedException{
        Runtime runtime = Runtime.getRuntime();
        String[] command = {"gio", "set", "-t", "string", directoryTarget.getAbsolutePath(),
                "metadata::custom-icon", "file://"+icon.getAbsolutePath()};
        Process process = runtime.exec(command);
        int response = process.waitFor();
        //System.out.println(response);
    }

    public LinuxIconChanger(File file){
        this.rootDir = file;
    }
}
