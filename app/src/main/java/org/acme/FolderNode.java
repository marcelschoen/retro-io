package org.acme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderNode {

    private List<FolderNode> childNodes = new ArrayList<>();
    private File folder;
    private String title = null;
    private String relativePath = "";
    private String basePath = "";

    public FolderNode(String basePath, File folder, boolean isRoot) {
        this(basePath, folder, "");
        if(isRoot) {
            this.title = "";
        }
    }

    public FolderNode(String basePath, File folder, String parentPath) {
        this.folder = folder;
        this.title = this.folder.getName();
        this.relativePath = parentPath + "/" + folder.getName();
        this.basePath = basePath;
        File[] entries = this.folder.listFiles();
        for (File entry : entries) {
            if (entry.isDirectory()) {
                this.childNodes.add(new FolderNode(this.basePath, entry, this.relativePath));
            }
        }
    }

    public String getFolderDivTag() {
        String divTag = "<div>";
        divTag += "<br /><b>" + (this.title.isEmpty() ? "" : "/") + this.title + "/</b>";
        divTag += "<div style=\"margin-left: 20px\">";
        File[] files = this.folder.listFiles();
        divTag += "<table>";
        for (File entry : files) {
            if (entry.isFile()) {
                divTag += "<tr>";
                divTag += "<td>+</td>";
                divTag += "<td><a href=\"/download/file?path=" + this.basePath + "&relativePath=" + this.relativePath + "/" + entry.getName() + "\">" + entry.getName() + "</a></td>";
                divTag += "<td>size: " + entry.length() + "</td>";
                divTag += "</tr>";
            }
        }
        divTag += "</table>";
        for (FolderNode child : childNodes) {
            divTag += child.getFolderDivTag();
        }
        return divTag + "</div></div>";
    }
}
