import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainForm extends JFrame{
    public JPanel mainPanel;
    private JTextField textFieldIcon;
    private JCheckBox useAsPrefixCheckBox;
    private JTextField recursionTextField;
    private JCheckBox setIconForEveryCheckBox;
    private JCheckBox squareTheImageCheckBox;
    private JButton chooseDirectoryButton;
    private JTextField textFieldDirectory;
    private JButton applyIconsButton;
    private JLabel progressLabel;
    private JButton chooseDirectoryWhereToButton;
    private JTextField savingIconsTextField;
    private JLabel recursiveLabel;
    private JCheckBox useTheFirstImageCheckBox;
    private JScrollPane scrolling;
    private JList<String> directoryList;
    private DefaultListModel<String> listModel;
    private File targetDirectory;
    private IconChanger changer;

    public MainForm() {
        //LISTENERS INSTALLATION
        chooseDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File target = chooseDirectory();
                if (target!=null){
                    textFieldDirectory.setText(target.getAbsolutePath());
                    targetDirectory = target;
                }
            }
        });

        chooseDirectoryWhereToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File target = chooseDirectory();
                if (target!=null){
                    savingIconsTextField.setText(target.getAbsolutePath());
                }
            }
        });

        setIconForEveryCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enableRecursiveFields = !setIconForEveryCheckBox.isSelected();
                recursionTextField.setEnabled(enableRecursiveFields);
            }
        });

        recursionTextField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent k) {
                int keyCode = k.getKeyCode();
                if(keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE){
                    return;
                }
                recursionTextField.setEditable(k.getKeyChar() >= '0' && k.getKeyChar() <= '9');
            }

            @Override
            public void keyReleased(KeyEvent k) {
                recursionTextField.setEditable(true);
            }
        });

        applyIconsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!validateForm()){
                    return;
                }

                String operatingSystem = System.getProperty("os.name").toLowerCase();

                if(changer == null) {
                    if (operatingSystem.contains("win")) {
                        changer = new WindowsIconChanger(targetDirectory);
                    } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") ||
                            operatingSystem.contains("aix")) {
                        changer = new LinuxIconChanger(targetDirectory);
                    } else {
                        return;
                    }
                }

                //ICON CHANGER INITIALIZATION
                changer.setSavingDir(new File(savingIconsTextField.getText()));
                changer.setFilename(textFieldIcon.getText());
                changer.setPrefix(useAsPrefixCheckBox.isSelected());
                changer.setSquareImage(squareTheImageCheckBox.isSelected());
                changer.setUseFirst(useTheFirstImageCheckBox.isSelected());
                changer.setRootRecursions(Integer.parseInt(recursionTextField.getText()));
                changer.setListModel(listModel);
                changer.setIconsButton(applyIconsButton);

                Thread t = new Thread(changer);
                applyIconsButton.setEnabled(false);
                t.start();
            }
        });
    }

    private File chooseDirectory(){
        final JFileChooser jf = new JFileChooser();
        jf.setCurrentDirectory(new java.io.File("."));
        jf.setDialogTitle("Select target directory...");
        jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jf.setAcceptAllFileFilterUsed(false);
        int returnVal = jf.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return jf.getSelectedFile();
        }
        return null;

    }

    private boolean validateForm(){
        if(savingIconsTextField.getText().isEmpty() ||
                textFieldDirectory.getText().isEmpty() ||
                textFieldIcon.getText().isEmpty()){
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,"Fill every field!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        listModel = new DefaultListModel<String>();
        directoryList = new JList<String>(listModel);
    }
}
