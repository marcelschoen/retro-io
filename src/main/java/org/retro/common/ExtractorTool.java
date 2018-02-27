package org.retro.common;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 * GUI- or commandline tool for extracting contents
 * of a floppy disk image.
 */
public class ExtractorTool {

    private TextArea fileList = new TextArea();

    private final static String UI_ACTION_LOAD = "Load image";
    private final static String UI_ACTION_EXTRACT = "Extract to...";

    private String currentDirectory = "";

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


    public void runUI() {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    class MainWindow extends Frame implements WindowListener, ActionListener {

        public MainWindow() {
            addWindowListener(this);

            setSize(new Dimension(400,300));
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
            Button extractButton=new Button(UI_ACTION_EXTRACT);
            extractButton.addActionListener(this);
            extractButton.setEnabled(false);
            loadPanel.add(loadButton);
            loadPanel.add(extractButton);
        }

        // The user has requested to open a file.
        // Show a FileDialog in LOAD mode.
        protected void selectFile(){
            FileDialog dialog = new FileDialog(this,"Open File",FileDialog.LOAD);
            dialog.setDirectory(currentDirectory);
            dialog.setFile("");
            dialog.show();

            // Wait for the dialog to complete.
            String filename = dialog.getFile();
            if((filename != null) && (filename.length() != 0)){
                // Prefix the directory name, if any.
                String directory = dialog.getDirectory();
                if((directory != null) && (directory.length() != 0)){
                    currentDirectory = directory;
                    filename = directory+filename;
                }
                try {
                    if(filename.contains(".")) {
                        // Load the file.
                        ImageType type = ImageType.getTypeFromFileSuffix(filename.substring(filename.indexOf(".") + 1));
                        System.out.println("Attempt to load image of type: " + type.name());
                        if(type != null) {
                            ImageHandler handler = ImageHandlerFactory.get(type);
                            VirtualDisk disk = handler.loadImage(new File(dialog.getFile()));
                            VirtualDirectory root = disk.getRootContents();
                            StringBuffer txt = new StringBuffer();
                            root.getContents().forEach(e -> txt.append(e.getName() + "\n\r"));
                            fileList.setText(txt.toString());
                        }
                    }
                } catch(Exception e){
                    System.err.println(e);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(UI_ACTION_LOAD)) {
                System.out.println("Load image...");
                selectFile();
            } if(e.getActionCommand().equals(UI_ACTION_EXTRACT)) {
                System.out.println("Extract to...");
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
