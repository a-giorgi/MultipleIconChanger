import net.sf.image4j.codec.ico.ICOEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WindowsIconChanger extends IconChanger{

    public void setIcon(File directoryTarget, File icon) throws IOException{
        BufferedImage loadedImage = ImageIO.read(icon);
        int width = loadedImage.getWidth();
        int height = loadedImage.getHeight();

        int icoMaxSize = 256;
        int resizedWidth;
        int resizedHeight;
        if(height>width){
            resizedHeight = icoMaxSize;
            resizedWidth = (icoMaxSize * width) / height;
        }else{
            resizedWidth = icoMaxSize;
            resizedHeight = (icoMaxSize * height) / width;
        }

        Image scaled = loadedImage.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
        BufferedImage scaledBImage = new BufferedImage(resizedWidth, resizedHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = scaledBImage.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();

        ICOEncoder.write(scaledBImage, new File(icon.getAbsolutePath()+".ico"));

        String ini = "[.ShellClassInfo]\n" +
                     "IconResource="+icon.getAbsolutePath()+".ico,0";
        BufferedWriter out = new BufferedWriter(
                new FileWriter(directoryTarget.getAbsolutePath()+"/desktop.ini"));
        out.write(ini);
        out.close();
        String[] command = {"attrib", "+h", "+s", directoryTarget.getAbsolutePath()+"/desktop.ini"};
        Process processOne =  Runtime.getRuntime().exec(command);
        command = new String[]{"attrib", "-h", "+s", directoryTarget.getAbsolutePath()};
        Process processTwo =  Runtime.getRuntime().exec(command);
    }

    public WindowsIconChanger(File file){
        this.rootDir = file;
    }
}
