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
package com.retroio.tools.impl.amiga.adf;

/**
 * Stores contents of one file entry in the ADF image.
 *
 * @author Marcel Schoen
 */
public class AdfFile {

    /**
     * The sector where the file is stored.
     */
    private int sector;

    /**
     * Holds the file's actual contents.
     */
    private byte[] content = new byte[0];

    /**
     * The file's size in bytes.
     */
    private int size;

    /**
     * The name of the file.
     */
    private String name;

    /**
     * Additional information.
     */
    private AdfBlockItem item = new AdfBlockItem();

    /**
     * Creates an empty file holder.
     */
    public AdfFile() {
    }

    /**
     * Creates a file which starts at the given sector.
     *
     * @param sector The sector with the file data.
     */
    public AdfFile(int sector) {
        this.sector = sector;
    }

    /**
     * Sets the additional information iteml
     *
     * @param item The item with additional information.
     */
    public void setItem(AdfBlockItem item) {
        this.item = item;
    }

    /**
     * Returns the sector where this file is located.
     *
     * @return The sector of this file.
     */
    public int getSector() {
        return sector;
    }

    /**
     * Sets the sector where this file is located.
     *
     * @param sector The sector of this file.
     */
    public void setSector(int sector) {
        this.sector = sector;
    }

    /**
     * Returns this file's contents.
     *
     * @return The contents of this file (may be empty, but not null).
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets this file's contents.
     *
     * @param content The contents for this file.
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Returns this file's size.
     *
     * @return The size of this file in bytes.
     */
    public int getSize() {
        return item.getSize();
    }

    /**
     * Sets this file's size.
     *
     * @param size The size of this file in bytes.
     */
    public void setSize(int size) {
        item.setSize(size);
    }

    /**
     * Returns this file's name.
     *
     * @return The name of this file.
     */
    public String getName() {
        return item.getName();
    }

    /**
     * Sets this file's name.
     *
     * @param name The name of this file.
     */
    public void setName(String name) {
        item.setName(name);
    }

    /**
     * Returns the next linked sector (if there is any).
     *
     * @return The next linked sector number.
     */
    public int getLinkedSector() {
        return item.getLinkedSector();
    }

    /**
     * Sets the next linked sector number.
     *
     * @param linkedSector The linked sector number.
     */
    public void setLinkedSector(int linkedSector) {
        item.setLinkedSector(linkedSector);
    }

    /**
     * Returns the parent directory.
     *
     * @return Parent directory sector.
     */
    public int getParent() {
        return item.getParent();
    }

    /**
     * Sets the parent directory.
     *
     * @param parent Parent directory sector.
     */
    public void setParent(int parent) {
        item.setParent(parent);
    }

    /**
     * Returns the item type.
     *
     * @return The item type.
     */
    public int getType() {
        return item.getType();
    }

    /**
     * Sets the item type.
     *
     * @param type The item type.
     */
    public void setType(int type) {
        item.setType(type);
    }
}
