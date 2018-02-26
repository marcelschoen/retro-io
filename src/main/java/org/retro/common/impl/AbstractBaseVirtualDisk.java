package org.retro.common.impl;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;

public abstract class AbstractBaseVirtualDisk implements VirtualDisk {

    private long capacity;
    private long usedSpace;
    private long freeSpace;
    private long usableSpace;

    private VirtualDirectory rootContent;

    private int numbersOfTracks;
    private int clusterSize;
    private int sectorsPerCluster;
    private int sectorSize;

    @Override
    public long getCapacity() throws VirtualDiskException {
        return this.capacity;
    }

    @Override
    public long getUsedSpace() throws VirtualDiskException {
        return this.usedSpace;
    }

    @Override
    public long getFreeSpace() throws VirtualDiskException {
        return this.freeSpace;
    }

    @Override
    public long getUsableSpace() throws VirtualDiskException {
        return this.usableSpace;
    }

    @Override
    public VirtualDirectory getRootContents() throws VirtualDiskException {
        return this.rootContent;
    }

    @Override
    public int getNumberOfTracks() throws VirtualDiskException {
        return this.numbersOfTracks;
    }

    @Override
    public int getSectorsPerCluster() throws VirtualDiskException {
        return this.sectorsPerCluster;
    }

    @Override
    public int getSectorSize() throws VirtualDiskException {
        return this.sectorSize;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public void setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public void setUsableSpace(long usableSpace) {
        this.usableSpace = usableSpace;
    }

    public void setRootContent(VirtualDirectory rootContent) {
        this.rootContent = rootContent;
    }

    public void setNumbersOfTracks(int numbersOfTracks) {
        this.numbersOfTracks = numbersOfTracks;
    }

    public void setClusterSize(int clusterSize) {
        this.clusterSize = clusterSize;
    }

    public void setSectorsPerCluster(int sectorsPerCluster) {
        this.sectorsPerCluster = sectorsPerCluster;
    }

    public void setSectorSize(int sectorSize) {
        this.sectorSize = sectorSize;
    }
}
