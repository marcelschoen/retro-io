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
package org.retro.common.impl.amiga.adf;

/**
 * Item with additional block information.
 *
 * @author Marcel Schoen
 */
public class AdfBlockItem {

    private int size;
    private String name;
    private int linkedSector;
    private int parent;
    private int dataBlockExtention;
    private int type;

    /**
     * Returns the item size.
     *
     * @return The item size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the item size.
     *
     * @param size The item size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * NOT USED
     *
     * @param comment
     */
    public void setComment(String comment) {
    }

    /**
     * NOT USED
     *
     * @param lastChangeDays
     */
    public void setLastChangeDays(int lastChangeDays) {
    }

    /**
     * NOT USED
     *
     * @param lastChangeMinutes
     */
    public void setLastChangeMinutes(int lastChangeMinutes) {
    }

    /**
     * NOT USED
     *
     * @param lastChangeTicks
     */
    public void setLastChangeTicks(int lastChangeTicks) {
    }

    /**
     * Returns the item name.
     *
     * @return The item's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item's name.
     *
     * @param name The item name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the linked sector number.
     *
     * @return The linked sector.
     */
    public int getLinkedSector() {
        return linkedSector;
    }

    /**
     * Sets the linked sector number.
     *
     * @param linkedSector The linked sector.
     */
    public void setLinkedSector(int linkedSector) {
        this.linkedSector = linkedSector;
    }

    /**
     * Returns the parent directory sector.
     *
     * @return The parent directory sector.
     */
    public int getParent() {
        return parent;
    }

    /**
     * Sets the parent directory sector.
     *
     * @param parent The parent directory sector.
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    /**
     * Returns the data block extention sector.
     *
     * @return The data block extension sector.
     */
    public int getDataBlockExtention() {
        return dataBlockExtention;
    }

    /**
     * Sets the data block extention sector.
     *
     * @param dataBlockExtention The data block extension sector.
     */
    public void setDataBlockExtention(int dataBlockExtention) {
        this.dataBlockExtention = dataBlockExtention;
    }

    /**
     * Returns the item type.
     *
     * @return The item type.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the item type.
     *
     * @param type The item type.
     */
    public void setType(int type) {
        this.type = type;
    }
}
