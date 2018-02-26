package org.retro.common;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a single file entry.
 *
 * @author Marcel Schoen
 */
public class VirtualFile implements Comparable<String> {

    /** The filename. */
    private String name;

    /** The parent directory (there must always be one). */
    private VirtualDirectory parent;

    /** The file content (can be empty buffer). */
    private ByteBuffer content = ByteBuffer.allocate(0);

    /** The last modification date. */
    private long lastModified;

    public VirtualFile(String name) {
        this.name = name;
    }

    public VirtualFile(String name, long lastModified) {
        this(name);
        this.lastModified = lastModified;
    }

    public VirtualFile(VirtualDirectory parent, String name) {
        this(name);
        this.parent = parent;
        parent.addFile(this);
    }

    public VirtualFile(VirtualDirectory parent, String name, long lastModified) {
        this(parent, name);
        this.lastModified = lastModified;
    }

    public VirtualFile(VirtualDirectory parent, String name, ByteBuffer content) {
        this(parent, name);
        setContent(content);
    }

    public VirtualFile(VirtualDirectory parent, String name, ByteBuffer content, long lastModified) {
        this(parent, name);
        setContent(content);
        this.lastModified = lastModified;
    }

    @Override
    public int compareTo(String other) {
        return this.getName().compareTo(other);
    }

    public boolean isFile() {
        return !isDirectory();
    }

    public boolean isDirectory() {
        return false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VirtualDirectory getParent() {
        return this.parent;
    }

    public void setParent(VirtualDirectory parent) {
        this.parent = parent;
    }

    public void copyTo(VirtualDirectory target) throws IOException {
        target.addFile(this);
    }

    public void moveTo(VirtualDirectory target) throws IOException {
        target.addFile(this);
        parent.remove(this);
    }

    public ByteBuffer getContent() {
        return this.content;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
    }

    public void clear() {
        this.content = ByteBuffer.allocate(0);
    }

    public long getLength() {
        return this.content.capacity();
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof VirtualFile)) {
            return false;
        }
        VirtualFile other = (VirtualFile)obj;
        if(this.getName().equals(other.getName())) {
            if(this.getParent() != null && other.getParent() != null) {
                if(this.getParent().equals(other.getParent())) {
                    return true;
                }
            } else if(this.getParent() == null && other.getParent() == null) {
                return true;
            }
        }
        return false;
    }
}
