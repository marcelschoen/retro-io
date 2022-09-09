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
package com.retroio.tools.impl.c64;

import com.retroio.tools.ImageType;
import com.retroio.tools.impl.AbstractBaseVirtualDisk;

/**
 * A virtual C64 floppy disk.
 *
 * @author Marcel Schoen
 */
public class C64VirtualDisk extends AbstractBaseVirtualDisk {
    public C64VirtualDisk(String name) {
        super(ImageType.c64_D64, name);
    }
}
