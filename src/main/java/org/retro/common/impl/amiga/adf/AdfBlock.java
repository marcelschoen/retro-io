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

import java.util.List;

/**
 * Stores basic information of one block. Which
 * fields are used depends on which type of
 * block is being processed.
 *
 * @author Marcel Schoen
 */
public class AdfBlock {

    /** The block type. */
    private int type;

    /** The header block starting sector. */
    private int headerSector;

    /** The data block count. */
    private int dataBlockCount;

    /** The data size. */
    private int dataSize;

    /** The sector of the first data block. */
    private int firstDataBlock;

    /** The sector of the next data block. */
    private int nextDataBlock;

    /** The checksum. */
    private int checksum;

    /** Item with additional information. */
    private AdfBlockItem item = new AdfBlockItem();

    /** Actual block data. */
    private byte[] content;

    /** List of block pointers. */
    private List<Integer> pointers;

    /**
     * NOT USED.
     *
     * @param number
     */
    public void setNumber(int number) {
    }

    public int getNextDataBlock() {
        return nextDataBlock;
    }

    public void setNextDataBlock(int nextDataBlock) {
        this.nextDataBlock = nextDataBlock;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public AdfBlockItem getItem() {
        return item;
    }

    public void setItem(AdfBlockItem item) {
        this.item = item;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHeaderSector() {
        return headerSector;
    }

    public void setHeaderSector(int headerSector) {
        this.headerSector = headerSector;
    }

    public int getDataBlockCount() {
        return dataBlockCount;
    }

    public void setDataBlockCount(int dataBlockCount) {
        this.dataBlockCount = dataBlockCount;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getFirstDataBlock() {
        return firstDataBlock;
    }

    public void setFirstDataBlock(int firstDataBlock) {
        this.firstDataBlock = firstDataBlock;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public List<Integer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Integer> pointers) {
        this.pointers = pointers;
    }
}
