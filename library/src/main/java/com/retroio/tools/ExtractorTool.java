/*
 * (C) Copyright ${year} retro-io (https://github.com/marcelschoen/retro-io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.retroio.tools;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;

/**
 * Simple GUI- or commandline tool for extracting contents
 * of a floppy disk image. This is really more meant as a
 * proof-of-concept, and to make this library potentially
 * immediately at least somewhat useful.
 *
 * @author Marcel Schoen
 */
public class ExtractorTool {

    private final static String UI_ACTION_LOAD = "Load image";
    private final static String UI_ACTION_EXTRACT = "Extract files to...";
    private final static String UI_ACTION_ZIP = "Create Zip file...";

    private File currentImageDirectory = new File(".");
    private File currentTargetDirectory = new File(".");

    private VirtualDisk currentDisk;

    private JPanel mainPanel;

    private JTree tree;

    public static void main(String... args) {
        boolean showUsage = true;
        if (args != null && args.length > 0) {
            ExtractorTool tool = new ExtractorTool();
            if (args[0].equalsIgnoreCase("-e")) {
                // TODO - extract disk image into directory
            } else if (args[0].equalsIgnoreCase("-l")) {
                // TODO - list disk image contents
            } else if (args[0].equalsIgnoreCase("-z")) {
                // TODO - create zip archive of disk image contents
            }
        } else {
            new ExtractorTool().runUI();
        }
        if (showUsage) {
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
     * The Swing-based GUI tool.
     */
    class MainWindow extends JFrame implements WindowListener, ActionListener {

        JButton extractFilesButton;
        JButton extractToZipButton;

        public MainWindow() {
            setTitle("retro-io");
            addWindowListener(this);
            setSize(new Dimension(500, 600));
            mainPanel = new JPanel();

            add(mainPanel);
            getContentPane().add(mainPanel);

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("/");
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            treeModel.addTreeModelListener(new MyTreeModelListener());
            tree = new JTree(treeModel);

            JScrollPane scrollPane = new JScrollPane(tree);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setBackground(Color.CYAN);

            LayoutManager mainLayout = new BorderLayout();
            mainPanel.setLayout(mainLayout);
            mainPanel.add(new Label("Retro Image Extractor Tool"), BorderLayout.NORTH);

            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel loadPanel = new JPanel();
            mainPanel.add(loadPanel, BorderLayout.SOUTH);

            JButton loadButton = new JButton(UI_ACTION_LOAD);
            loadButton.addActionListener(this);
            this.extractFilesButton = new JButton(UI_ACTION_EXTRACT);
            this.extractFilesButton.addActionListener(this);
            this.extractFilesButton.setEnabled(false);
            loadPanel.add(loadButton);
            loadPanel.add(this.extractFilesButton);

            this.extractToZipButton = new JButton(UI_ACTION_ZIP);
            this.extractToZipButton.addActionListener(this);
            this.extractToZipButton.setEnabled(false);
            loadPanel.add(this.extractToZipButton);
        }

        // The user has requested to open a file.
        // Show a FileDialog in LOAD mode.
        protected void selectFile() {
            String[] suffixes = Arrays.stream(ImageType.values()).map(e -> e.getFileSuffix()).toArray(String[]::new);
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Load disk image", suffixes);
            chooser.setCurrentDirectory(currentImageDirectory);
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getName();
                // Load the file.
                ImageType type = ImageType.getTypeFromFileSuffix(filename.substring(filename.indexOf(".") + 1));
                System.out.println("Attempt to load image of type: " + type.name());
                if (type != ImageType.unknown) {
                    ImageHandler handler = ImageHandlerFactory.get(type);
                    try {
                        VirtualDisk disk = handler.loadImage(chooser.getSelectedFile());
                        VirtualDirectory root = disk.getRootContents();

                        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root.getName());
                        createTree(rootNode, root);
                        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
                        treeModel.addTreeModelListener(new MyTreeModelListener());
                        tree.setModel(treeModel);

                        mainPanel.revalidate();
                        mainPanel.repaint();
                        extractFilesButton.setEnabled(true);
                        extractToZipButton.setEnabled(true);
                        currentDisk = disk;
                        currentImageDirectory = chooser.getCurrentDirectory();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Opens the file dialog to choose the target directory
         * and extracts the image into it, either as files and
         * subdirectories, or in a ZIP archive.
         *
         * @param toZip True if the contents should be extracted into a ZIP archive.
         */
        protected void extractToFile(boolean toZip) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(currentTargetDirectory);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Load the file.
                ImageType type = currentDisk.getType();
                if (type != null) {
                    ImageHandler handler = ImageHandlerFactory.get(type);
                    try {
                        currentTargetDirectory = chooser.getSelectedFile();
                        if (toZip) {
                            currentDisk.exportAsZip(currentTargetDirectory);
                        } else {
                            currentDisk.exportToDirectory(currentTargetDirectory);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void createTree(DefaultMutableTreeNode parentNode, VirtualDirectory parentDirectory) {
            for (VirtualFile file : parentDirectory.getContents()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getName());
                parentNode.add(newNode);
                if (file.isDirectory()) {
                    createTree(newNode, (VirtualDirectory) file);
                }
            }
        }

        /**
         * Builds the directory contents text list.
         *
         * @param indent    The current indentation spaces.
         * @param directory The directory to process.
         * @return The resulting text for that directory.
         */
        private String getDirectoryContents(String indent, VirtualDirectory directory) {
            String txt = indent + directory.getName() + "/\n";
            indent += "    ";
            for (VirtualFile entry : directory.getContents()) {
                if (entry.isDirectory()) {
                    txt += getDirectoryContents(indent, (VirtualDirectory) entry);
                } else {
                    txt += indent + entry.getName() + "\n";
                }
            }
            return txt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(UI_ACTION_LOAD)) {
                selectFile();
            }
            if (e.getActionCommand().equals(UI_ACTION_EXTRACT)) {
                extractToFile(false);
            }
            if (e.getActionCommand().equals(UI_ACTION_ZIP)) {
                extractToFile(true);
            }
        }

        @Override
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }
    }


    class MyTreeModelListener implements TreeModelListener {

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getTreePath()
                    .getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));
            System.out.println("New value NodesChanged: " + node.getUserObject());
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getTreePath()
                    .getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));
            System.out.println("New value NodesInserted : " + node.getUserObject());
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getTreePath()
                    .getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));
            System.out.println("New value NodesRemoved : " + node.getUserObject());
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (e.getTreePath()
                    .getLastPathComponent());
            int index = e.getChildIndices()[0];
            node = (DefaultMutableTreeNode) (node.getChildAt(index));
            System.out.println("New value StructureChanged : " + node.getUserObject());
        }
    }
}
