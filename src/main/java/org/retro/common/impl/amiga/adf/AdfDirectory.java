package org.retro.common.impl.amiga.adf;

import java.util.ArrayList;
import java.util.List;

public class AdfDirectory extends AdfFile {

    private List<AdfDirectory> subDirectories = new ArrayList<>();
    private List<AdfFile> fileEntries = new ArrayList<>();

    public AdfDirectory(int sector) {
        super(sector);
    }

    public List<AdfDirectory> getSubDirectories() {
        return subDirectories;
    }

    public void setSubDirectories(List<AdfDirectory> subDirectories) {
        this.subDirectories = subDirectories;
    }

    public List<AdfFile> getFileEntries() {
        return fileEntries;
    }

    public void setFileEntries(List<AdfFile> fileEntries) {
        this.fileEntries = fileEntries;
    }
}
