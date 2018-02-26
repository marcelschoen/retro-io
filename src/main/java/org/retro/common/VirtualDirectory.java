package org.retro.common;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Stores information about one directory of a virtual disk,
 * and the contents of that directory (subdirectories and files).
 *
 * @author Marcel Schoen
 */
public class VirtualDirectory extends VirtualFile {

    /** The list of directory entries. */
    private LinkedHashSet<VirtualFile> virtualFiles = new LinkedHashSet<>();

    /**
     * Creates a virtual directory.
     *
     * @param name The name of the virtual directory.
     */
    public VirtualDirectory(String name) {
        super(name);
    }

    /**
     * Creates a virtual directory which has a parent directory.
     *
     * @param parent The parent directory.
     * @param name The name of this directory.
     */
    public VirtualDirectory(VirtualDirectory parent, String name) {
        super(parent, name);
    }

    /**
     * Creates a virtual directory which has a parent directory
     * and a certain modification date.
     *
     * @param parent The parent directory.
     * @param name The name of this directory.
     * @param lastModified The date of last modification.
     */
    public VirtualDirectory(VirtualDirectory parent, String name, long lastModified) {
        super(parent, name, lastModified);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    /**
     * Gets an iterator to iterate over the entries of this directory.
     *
     * @return the directory iterator
     */
    public Iterator<VirtualFile> iterator() {
        return this.virtualFiles.iterator();
    }

    /**
     * Returns a set with all the entries in this directory.
     *
     * @return The set with all entries.
     */
    public Set<VirtualFile> getContents() { return this.virtualFiles; }

    /**
     * Gets the entry with the given name.
     *
     * @param name the name of the entry to get
     * @return the entry, if it existed
     * @throws IOException on error retrieving the entry
     */
    public VirtualFile getEntry(String name) throws IOException {
        return this.virtualFiles.stream()
                .filter(f -> f.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add a new file with a given name to this directory.
     *
     * @param name the name of the file to add
     * @return the entry pointing to the new file
     * @throws IOException on error creating the file
     */
    public VirtualFile addFile(String name) {
        VirtualFile newVirtualFile = new VirtualFile(name);
        this.addFile(newVirtualFile);
        return newVirtualFile;
    }

    /**
     * Add a new file to this directory.
     *
     * @param newVirtualFile The new file to add to this directory.
     * @return the entry pointing to the new file
     */
    public void addFile(VirtualFile newVirtualFile) {
        this.virtualFiles.add(newVirtualFile);
    }

    /**
     * Add a new (sub-)directory with a given name to this directory.
     *
     * @param name the name of the sub-directory to add
     * @return the entry pointing to the new directory
     * @throws IOException on error creating the directory
     */
    public VirtualDirectory addDirectory(String name) throws IOException {
        return new VirtualDirectory(this, name);
    }

    /**
     * Remove the given entry from this directory.
     *
     * @param virtualFile The entry toremove..
     * @throws IOException on error deleting the entry
     */
    public void remove(VirtualFile virtualFile) throws IOException {
        this.virtualFiles.remove(virtualFile);
    }

    /**
     * Remove the entry with the given name from this directory.
     *
     * @param name name of the entry to remove
     * @throws IOException on error deleting the entry
     */
    public void remove(String name) throws IOException {
        this.virtualFiles.remove(getEntry(name));
    }
}
