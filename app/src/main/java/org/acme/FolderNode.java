package org.acme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderNode {

    private List<FolderNode> childNodes = new ArrayList<>();
    private File folder;

    public FolderNode(File folder) {
        this.folder = folder;
        File[] entries = this.folder.listFiles();
        for (File entry : entries) {
            if (entry.isDirectory()) {
                this.childNodes.add(new FolderNode(entry));
            }
        }
    }

    public String getFolderDivTag() {
        String divTag = "<div style=\"margin-left: 20px\">";
        divTag += "<br><b>" + this.folder.getName() + "</b>";
        File[] files = this.folder.listFiles();
        for (File entry : files) {
            if (entry.isFile()) {
                divTag += "<br>+&nbsp;" + entry.getName() + " / size: " + entry.length();
            }
        }
        for (FolderNode child : childNodes) {
            divTag += child.getFolderDivTag();
        }
        return divTag + "</div>";
    }
}
