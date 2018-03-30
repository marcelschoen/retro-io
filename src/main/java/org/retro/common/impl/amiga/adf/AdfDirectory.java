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

import java.util.ArrayList;
import java.util.List;

/**
 * Stores contents of one ADF image directory (folder):
 *
 * @author Marcel Schoen
 */
public class AdfDirectory extends AdfFile {

    /** The subdirectory entries. */
    private List<AdfDirectory> subDirectories = new ArrayList<>();

    /** The file entries. */
    private List<AdfFile> fileEntries = new ArrayList<>();

    /**
     * Creates a directory starting at a certain sector.
     *
     * @param sector The sector with the directory data.
     */
    public AdfDirectory(int sector) {
        super(sector);
    }

    /**
     * Returns the list of subdirectories.
     *
     * @return The subfolders (may be empty, but not null).
     */
    public List<AdfDirectory> getSubDirectories() {
        return subDirectories;
    }

    /**
     * Returns the file entries in this directory.
     *
     * @return The list of file entries (may be empty, but not null).
     */
    public List<AdfFile> getFileEntries() {
        return fileEntries;
    }
}
