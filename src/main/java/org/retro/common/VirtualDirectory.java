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
package org.retro.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

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

    @Override
    public boolean isFile() {
        return false;
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
     * Returns a new instance of a set with all the entries in this directory,
     * sorted by file / directory names (alphabetically).
     *
     * @return The set with all entries.
     */
    public List<VirtualFile> getContents() {
        List<VirtualFile> contents = new ArrayList();
        // First add all directory entries, sorted alphabetically
        this.virtualFiles.stream()
                .filter(e -> e.isDirectory())
                .sorted()
                .forEach(e -> contents.add(e));

        // Then add all file entries, sorted alphabetically
        this.virtualFiles.stream()
                .filter(e -> e.isFile())
                .sorted()
                .forEach(e -> contents.add(e));
        return contents;
    }

    /**
     * Gets the entry with the given name.
     *
     * @param name the name of the entry to get
     * @return the entry, if it existed
     */
    public VirtualFile getEntry(String name) {
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
     */
    public VirtualDirectory addDirectory(String name) {
        return new VirtualDirectory(this, name);
    }

    /**
     * Remove the given entry from this directory.
     *
     * @param virtualFile The entry toremove..
     */
    public void remove(VirtualFile virtualFile) {
        this.virtualFiles.remove(virtualFile);
    }

    /**
     * Remove the entry with the given name from this directory.
     *
     * @param name name of the entry to remove
     */
    public void remove(String name) {
        this.virtualFiles.remove(getEntry(name));
    }
}
