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
package org.retro.common.impl.atarist;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

import java.io.IOException;
import java.nio.ByteBuffer;

import static de.waldheinz.fs.util.FileDisk.BYTES_PER_SECTOR;

/**
 * Abstract base class for a reader for a certain disk format,
 * which then provides the data as a block device to the
 * FAT reader.
 *
 * Implementations of this class should implement the actual
 * reading of the image in a custom method, store all the
 * extracted disk data in a ByteBuffer which is then returned
 * by the abstract method.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseStDisk implements BlockDevice {

    /**
     * Must return the ByteBuffer holding the ST floppy disk
     * image data (FAT/DOS image).
     *
     * @return The floppy disk image data.
     */
    protected abstract ByteBuffer getData();

    @Override
    public long getSize() throws IOException {
        return getData().capacity();
    }

    @Override
    public int getSectorSize() throws IOException {
        return BYTES_PER_SECTOR;
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        int toRead = dest.remaining();
        if ((devOffset + toRead) > getSize()) throw new IOException(
                "reading past end of device");
        dest.put(getData().array(), (int)devOffset, toRead);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws ReadOnlyException, IOException, IllegalArgumentException {
        throw new IOException("* NOT IMPLEMENTED *");
    }

    @Override
    public void flush() throws IOException {
        // NOP
    }

    @Override
    public void close() throws IOException {
        // NOP
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        // TODO - write support
        return true;
    }
}
