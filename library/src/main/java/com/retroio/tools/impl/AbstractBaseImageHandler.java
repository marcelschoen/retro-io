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
package com.retroio.tools.impl;

import com.retroio.tools.ImageHandler;
import com.retroio.tools.VirtualDisk;
import com.retroio.tools.VirtualDiskException;

import java.io.File;

/**
 * Abstract base class with some common functionality for
 * image handler implementations.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseImageHandler implements ImageHandler {

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }

    @Override
    public void writeImage(VirtualDisk virtualDisk, File imageFile) throws VirtualDiskException {
        throw new VirtualDiskException("*not implemented*");
    }
}
