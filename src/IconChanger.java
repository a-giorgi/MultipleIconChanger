import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Stack;

import static java.lang.System.currentTimeMillis;

public abstract class IconChanger implements Runnable {
    protected File rootDir;
    protected int rootRecursions;
    protected String filename;
    protected boolean useFirst;
    protected boolean isPrefix;
    protected File savingDir;
    protected boolean squareImage;
    protected DefaultListModel<String> listModel;
    private JButton iconsButton;
    public void setRootRecursions(int rootRecursions) {
        this.rootRecursions = rootRecursions;
    }
    public abstract void setIcon(File directoryTarget, File icon) throws IOException,InterruptedException;
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setPrefix(boolean prefix) {
        isPrefix = prefix;
    }
    public void setSavingDir(File savingDir) {
        this.savingDir = savingDir;
    }
    public void setUseFirst(boolean useFirst) {
        this.useFirst = useFirst;
    }
    public void setSquareImage(boolean squareImage) {
        this.squareImage = squareImage;
    }
    public void setListModel(DefaultListModel<String> listModel) {
        this.listModel = listModel;
    }
    public void setIconsButton(JButton iconsButton) {
        this.iconsButton = iconsButton;
    }

    public String getFileExtension(File file){
        int i = file.getAbsolutePath().lastIndexOf('.');
        if (i > 0) {
           return file.getAbsolutePath().substring(i+1);
        }else{
            return "";
        }
    }

    public void squareAndSave(File image, String saveTarget) throws IOException{
        BufferedImage loadedImage = ImageIO.read(image);
        int width = loadedImage.getWidth();
        int height = loadedImage.getHeight();
        int crop = Math.min(width,height);
        loadedImage = loadedImage.getSubimage(0,0,crop,crop);
        File output = new File(saveTarget+".png");
        ImageIO.write(loadedImage, "png", output);
    }

    public void setIcons(File directory, String filename, boolean isPrefix){
        Path imagePath = null;
        if(isPrefix) {
            File[] content = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return !(new File(current, name).isDirectory());
                }
            });
            if (content == null) {
                return;
            }
            for (File image : content) {
                if (image.getName().startsWith(filename) && (
                        image.getName().endsWith(".jpg") ||
                        image.getName().endsWith(".png") ||
                        image.getName().endsWith(".bmp") ||
                        image.getName().endsWith(".gif"))) {
                    imagePath = Paths.get(image.getAbsolutePath());
                    break;
                }
            }
        }else{
            imagePath = Paths.get(directory.getAbsolutePath() + "/" + filename);
            if(!imagePath.toFile().exists()){
                imagePath = null;
            }
        }
        if(imagePath == null && useFirst){
            File[] targets = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif"));
                }
            });
            if(targets == null || targets.length<1){
                return;
            }
            imagePath = Paths.get(directory.getAbsolutePath()+"/"+targets[0].getName());
        }else if(imagePath == null){
            return;
        }
        if(imagePath.toFile().exists()){
            File image = imagePath.toFile();
            String copiedImageName = "icon_at_" + currentTimeMillis();// + "." + getFileExtension(image);
            Path copiedPath = Paths.get(savingDir + "/" + copiedImageName + "." + getFileExtension(image));
            try {
                if(squareImage){
                    squareAndSave(image,savingDir + "/" + copiedImageName);
                    copiedPath = Paths.get(savingDir + "/" + copiedImageName + ".png");
                }else {
                    Files.copy(imagePath, copiedPath, StandardCopyOption.REPLACE_EXISTING);
                }
                setIcon(directory, copiedPath.toFile());
            } catch (IOException e) {
                listModel.addElement("[IO Error] " + directory.getAbsolutePath());
            } catch (InterruptedException e) {
                listModel.addElement("[EXCEPTION] " + directory.getAbsolutePath());
            }
        }
    }

    public String joSetIcon(){
        Path imagePath = Paths.get(filename);
        if(imagePath.toFile().exists()){
            File image = imagePath.toFile();
            String copiedImageName = "icon_at_" + currentTimeMillis();// + "." + getFileExtension(image);
            Path copiedPath = Paths.get(savingDir + "/" + copiedImageName + "." + getFileExtension(image));
            try {
                if(squareImage){
                    squareAndSave(image,savingDir + "/" + copiedImageName);
                    copiedPath = Paths.get(savingDir + "/" + copiedImageName + ".png");
                }else {
                    Files.copy(imagePath, copiedPath, StandardCopyOption.REPLACE_EXISTING);
                }
                setIcon(rootDir, copiedPath.toFile());
                return "Icon set successfully!";
            } catch (IOException e) {
                return "[IO Error] " + rootDir.getAbsolutePath();
            } catch (InterruptedException e) {
                return "Exception occurred!";
            }
        }
        return "The selected image does not exists!";
    }

    public void run() {
        Stack<RecursiveDirectory> stack = new Stack<>();
        stack.push(new RecursiveDirectory(rootDir, rootRecursions));
        if(listModel!=null){
            listModel.removeAllElements();
            listModel.addElement("*********** PROCESS STARTED ***********");
        }
        if(iconsButton!=null){
            iconsButton.setText("Please wait...");
        }
        while(!stack.isEmpty()){
            RecursiveDirectory popped = stack.pop();
            if(listModel!=null){
                listModel.addElement(popped.getDirectory().getAbsolutePath());
            }
            //System.out.println(popped.getDirectory().getAbsolutePath());
            //System.out.println(currentTimeMillis());
            setIcons(popped.getDirectory(),filename,isPrefix);
            if(popped.getRecursion()!=0) {
                File[] directories = popped.getDirectory().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        return new File(current, name).isDirectory();
                    }
                });
                if (directories == null) {
                    continue;
                }
                for (File f : directories) {
                    int nextRecursions = -1;
                    if(popped.getRecursion()>0){
                        nextRecursions = popped.getRecursion()-1;
                    }
                    stack.push(new RecursiveDirectory(f, nextRecursions));
                }
            }
        }
        if(listModel!=null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            listModel.addElement("DONE!");
        }
        if(iconsButton!=null){
            iconsButton.setEnabled(true);
            iconsButton.setText("Apply Icons");
        }

    }

}
