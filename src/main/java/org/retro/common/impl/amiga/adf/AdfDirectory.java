package org.retro.common.impl.amiga.adf;

import java.util.List;

public class AdfDirectory {
    private int sector;
    private List<AdfDirectory> subDirectories;
    private List<AdfFile> fileEntries;

    public int getSector() {
        return sector;
    }

    public void setSector(int sector) {
        this.sector = sector;
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
