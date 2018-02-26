package org.retro.common;

public interface VirtualDisk {

    long getCapacity() throws VirtualDiskException;

    long getUsedSpace() throws VirtualDiskException;

    long getFreeSpace() throws VirtualDiskException;

    /**
     * The actually usable amount of space. This may differ
     * from the "free space" due to things like bad sectors.
     *
     * @return
     * @throws VirtualDiskException
     */
    long getUsableSpace() throws VirtualDiskException;

    VirtualDirectory getRootContents() throws VirtualDiskException;

    int getNumberOfTracks() throws VirtualDiskException;

    int getSectorsPerCluster() throws VirtualDiskException;

    int getSectorSize() throws VirtualDiskException;
}
