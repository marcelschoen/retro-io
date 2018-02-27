package org.retro.common;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;

/**
 * GUI- or commandline tool for extracting contents
 * of a floppy disk image.
 */
public class ExtractorTool {

    private TextArea fileList = new TextArea();

    private final static String UI_ACTION_LOAD = "Load image";
    private final static String UI_ACTION_EXTRACT = "Extract to...";

    private File currentImageDirectory = new File(".");
    private File currentTargetDirectory = new File(".");

    private VirtualDisk currentDisk;

    public static void main(String ... args) {
        boolean showUsage = true;
        if(args != null && args.length > 0) {
            ExtractorTool tool = new ExtractorTool();
            if(args[0].equalsIgnoreCase("-ui")) {
                showUsage = false;
                tool.runUI();
            } else if(args[0].equalsIgnoreCase("-e")) {
                // extract disk image
            } else if(args[0].equalsIgnoreCase("-l")) {
                // list disk image contents
            }
        }
        if(showUsage) {
            System.out.println("To run in GUI mode:");
            System.out.println("");
            System.out.println("$ java -jar retro-io.jar -ui");
            System.out.println("");
            System.out.println("To list the contents of a disk image file:");
            System.out.println("");
            System.out.println("$ java -jar retro-io.jar -l <disk image file>");
            System.out.println("");
            System.out.println("To extract a disk image file:");
            System.out.println("");
            System.out.println("$ java -jar retro-io.jar -e <disk image file>");
            System.out.println("");
            System.out.println("NOTE: The image type will be determined from the filename suffix:");
            System.out.println("");
            System.out.println(".adf - Amiga disk image");
            System.out.println(".d64 - Commodore 64 disk image");
            System.out.println(".img - DOS disk image");
            System.out.println(".st - Atari ST disk image");
        }
    }

    /**
     * Runs the UI tool.
     */
    public void runUI() {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    /**
     * The AWT-based GUI tool.
     */
    class MainWindow extends Frame implements WindowListener, ActionListener {

        Button extractButton;

        public MainWindow() {
            addWindowListener(this);

            setSize(new Dimension(500,600));
            Panel mainPanel = new Panel();
            add(mainPanel);
            LayoutManager mainLayout = new BorderLayout();
            mainPanel.setLayout(mainLayout);
            mainPanel.add(new Label("Retro Image Extractor Tool"), BorderLayout.NORTH);
            mainPanel.add(fileList, BorderLayout.CENTER);
            fileList.setEditable(false);

            Panel loadPanel = new Panel();
            mainPanel.add(loadPanel, BorderLayout.SOUTH);
            Button loadButton=new Button(UI_ACTION_LOAD);
            loadButton.addActionListener(this);
            this.extractButton=new Button(UI_ACTION_EXTRACT);
            this.extractButton.addActionListener(this);
            this.extractButton.setEnabled(false);
            loadPanel.add(loadButton);
            loadPanel.add(this.extractButton);
        }

        // The user has requested to open a file.
        // Show a FileDialog in LOAD mode.
        protected void selectFile(){
            String[] suffixes = Arrays.stream(ImageType.values()).map(e -> e.getFileSuffix()).toArray(String[]::new);
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Load disk image", suffixes);
            chooser.setCurrentDirectory(currentImageDirectory);
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getName();
                System.out.println("You chose to open this file: " + filename);
                // Load the file.
                ImageType type = ImageType.getTypeFromFileSuffix(filename.substring(filename.indexOf(".") + 1));
                System.out.println("Attempt to load image of type: " + type.name());
                if(type != null) {
                    ImageHandler handler = ImageHandlerFactory.get(type);
                    try {
                        VirtualDisk disk = handler.loadImage(chooser.getSelectedFile());
                        VirtualDirectory root = disk.getRootContents();
                        String txt = getDirectoryContents("", root);
                        fileList.setText(txt.toString());
                        extractButton.setEnabled(true);
                        currentDisk = disk;
                        currentImageDirectory = chooser.getCurrentDirectory();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Opens the file dialog to choose the target directory
         * and extracts the image into it.
         */
        protected void extractContents(){
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(currentTargetDirectory);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to extract to this directory: " + chooser.getCurrentDirectory().getAbsolutePath());
                // Load the file.
                ImageType type = currentDisk.getType();
                System.out.println("Attempt to extract image of type: " + type.name());
                if(type != null) {
                    ImageHandler handler = ImageHandlerFactory.get(type);
                    try {
                        currentTargetDirectory = chooser.getCurrentDirectory();
                        handler.extractVirtualDisk(currentDisk, currentTargetDirectory);
                        System.out.println("Files extracted.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Builds the directory contents text list.
         *
         * @param indent The current indentation spaces.
         * @param directory The directory to process.
         * @return The resulting text for that directory.
         */
        private String getDirectoryContents(String indent, VirtualDirectory directory) {
            String txt = indent + directory.getName() + "/\n";
            indent += "    ";
            for(VirtualFile entry : directory.getContents()) {
                if(entry.isDirectory()) {
                    txt += getDirectoryContents(indent, (VirtualDirectory)entry);
                } else {
                    txt += indent + entry.getName() + "\n";
                }
            }
            return txt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(UI_ACTION_LOAD)) {
                selectFile();
            } if(e.getActionCommand().equals(UI_ACTION_EXTRACT)) {
                extractContents();
            }
        }

        @Override
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }

        @Override
        public void windowOpened(WindowEvent e) {}
        @Override
        public void windowActivated(WindowEvent e) {}
        @Override
        public void windowIconified(WindowEvent e) {}
        @Override
        public void windowDeiconified(WindowEvent e) {}
        @Override
        public void windowDeactivated(WindowEvent e) {}
        @Override
        public void windowClosed(WindowEvent e) {}    }
}
