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
    private String ICONS_PATH = "/icons/";

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
        String divTag = "<div style=\"padding-top: 20px; padding-bottom=20px;\">\n";
        divTag += "<span style=\"font-weight: bold;\"><img style=\"margin-right: 3px;\" src=\"" + ICONS_PATH + "folder.png\">" + this.title + "/</span>\n";
        divTag += "<div style=\"margin-left: 20px\">\n";
        File[] files = this.folder.listFiles();
        divTag += "<table>\n";
        String tightStyle = "margin: 0px; inset: 0px; padding: 0px; border: none;";
        for (File entry : files) {
            if (entry.isFile()) {
                divTag += "<tr style=\"" + tightStyle + "\">\n";
                divTag += "<td style=\"" + tightStyle + "\"><img src=\"" + getIconUri(entry) + "\"></td>";
                divTag += "<td style=\"" + tightStyle + "\"><a href=\"/download/file?path=" + this.basePath + "&relativePath=" + this.relativePath + "/" + entry.getName() + "\">" + entry.getName() + "</a></td>";
                divTag += "<td style=\"" + tightStyle + "\">size: " + entry.length() + "</td>";
                divTag += "</tr>\n";
            }
        }
        divTag += "</table>\n";
        for (FolderNode child : childNodes) {
            divTag += child.getFolderDivTag();
        }
        return divTag + "</div>\n</div>\n";
    }

    private String getIconUri(File file) {
        String name = file.getName().toLowerCase();
        String suffix = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "";
        switch (suffix) {
            case "prg":
            case "app":
            case "tos":
                return ICONS_PATH + "application-x-executable.png";
            case "zip":
            case "bz":
            case "lzb":
            case "lzh":
                return ICONS_PATH + "package-x-generic.png";
            case "mod":
            case "wav":
                return ICONS_PATH + "audio-x-generic.png";
            case "pi1":
            case "pi2":
            case "pi3":
            case "neo":
            case "gif":
            case "jpg":
            case "jpeg":
            case "png":
            case "iff":
            case "tiff":
                return ICONS_PATH + "image-x-generic.png";
            case "avi":
            case "mp4":
                return ICONS_PATH + "applications-multimedia-4.png";
            default:
        }
        return ICONS_PATH + "text-x-generic-template.png";
    }
}
