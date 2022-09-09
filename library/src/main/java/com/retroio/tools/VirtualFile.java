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

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Represents a single file (or directory).
 *
 * @author Marcel Schoen
 */
public class VirtualFile implements Comparable<VirtualFile> {

    /**
     * The filename.
     */
    private String name;

    /**
     * The parent directory (there must always be one).
     */
    private VirtualDirectory parent;

    /**
     * The file content (can be empty buffer).
     */
    private ByteBuffer content = ByteBuffer.allocate(0);

    /**
     * The unique identifier for this file.
     */
    private String uuid;

    /**
     * Creates a file.
     *
     * @param name The name of this file.
     */
    public VirtualFile(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     * Creates a file.
     *
     * @param parent The parent directory of this file.
     * @param name   The name of this file.
     */
    public VirtualFile(VirtualDirectory parent, String name) {
        this(name);
        this.parent = parent;
        parent.addFile(this);
    }

    /**
     * Creates a file.
     *
     * @param parent  The parent directory of this file.
     * @param name    The name of this file.
     * @param content The data contents of this file.
     */
    public VirtualFile(VirtualDirectory parent, String name, ByteBuffer content) {
        this(parent, name);
        setContent(content);
    }

    /**
     * UUID that uniquely identifies this file. This is generated randomly
     * every time the file is read from a floppy disk image.
     *
     * @return The UUID of this file.
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Returns the filename suffix, if there is one. Which is the part
     * after the last "." in the simple filename, e.g. ".prg" for
     * "something.prg".
     *
     * @return The suffix, or null.
     */
    public String getSuffix() {
        String suffix = null;
        if (this.name.contains(".") && this.name.lastIndexOf(".") < name.length()) {
            suffix = this.name.substring(this.name.lastIndexOf(".") + 1);
        }
        return suffix;
    }

    /**
     * Returns the full path filename of this file, i.e.
     * the name of the file preceeded by all parent directories.
     *
     * @return The full file path.
     */
    public String getFullName() {
        if (parent == null) {
            // no parent - must be root directory
            return "";
        }
        return parent.getFullName() + "/" + this.name;
    }

    @Override
    public int compareTo(VirtualFile other) {
        return this.getName().compareTo(other.getName());
    }

    /**
     * Determines if this is a file (rather than a directory).
     *
     * @return True if this is a file.
     */
    public boolean isFile() {
        return true;
    }

    /**
     * Determines if this is a directory (rather than a file).
     *
     * @return True if this is a directory.
     */
    public boolean isDirectory() {
        return false;
    }

    /**
     * Returns the name of this file.
     *
     * @return The simple file or directory name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name for this file.
     *
     * @param name The name of this file or directory.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the parent directory of this file,
     * if there is one.
     *
     * @return The parent directory (null for the root directory).
     */
    public VirtualDirectory getParent() {
        return this.parent;
    }

    /**
     * Sets the parent directory for this file or directory.
     *
     * @param parent The parent directory.
     */
    public void setParent(VirtualDirectory parent) {
        this.parent = parent;
    }

    /**
     * Copies this file or diretory to another directory.
     *
     * @param target The target (aka new parent) directory.
     */
    public void copyTo(VirtualDirectory target) {
        target.addFile(this);
    }

    /**
     * Moves this file or directory to another directory.
     *
     * @param target The target (aka new parent) directory.
     */
    public void moveTo(VirtualDirectory target) {
        target.addFile(this);
        parent.remove(this);
    }

    /**
     * Gets this file's content data.
     *
     * @return The new content data.
     * @throws IllegalStateException If the method was called for a directory.
     */
    public ByteBuffer getContent() {
        if (this.isFile()) {
            return this.content;
        } else {
            throw new IllegalStateException("Cannot get content of a directory entry: " + getName());
        }
    }

    /**
     * Sets this file's contents to the given content data.
     *
     * @param content The new content data.
     * @throws IllegalStateException If the method was called for a directory.
     */
    public void setContent(ByteBuffer content) {
        if (this.isFile()) {
            this.content = content;
        } else {
            throw new IllegalStateException("Cannot set content for a directory entry: " + getName());
        }
    }

    /**
     * Sets this file's contents to the given content data.
     *
     * @param content The new content data.
     * @throws IllegalStateException If the method was called for a directory.
     */
    public void setContent(byte[] content) {
        if (this.isFile()) {
            this.content = ByteBuffer.wrap(content);
        } else {
            throw new IllegalStateException("Cannot set content for a directory entry: " + getName());
        }
    }

    /**
     * Clears this file's contents.
     */
    public void clear() {
        this.content = ByteBuffer.allocate(0);
    }

    /**
     * Returns the size of this file in bytes.
     *
     * @return The content size.
     */
    public long getLength() {
        return this.content.capacity();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof VirtualFile)) {
            return false;
        }
        VirtualFile other = (VirtualFile) obj;
        if (this.getName().equals(other.getName())) {
            if (this.getParent() != null && other.getParent() != null) {
                if (this.getParent().equals(other.getParent())) {
                    return true;
                }
            } else if (this.getParent() == null && other.getParent() == null) {
                return true;
            }
        }
        return false;
    }
}
