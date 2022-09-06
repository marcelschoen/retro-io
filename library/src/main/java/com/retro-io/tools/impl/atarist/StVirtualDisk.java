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

import org.retro.common.ImageType;
import org.retro.common.impl.AbstractBaseVirtualDisk;

/**
 * Atari ST ".st" floppy disk image.
 *
 * @author Marcel Schoen
 */
public class StVirtualDisk extends AbstractBaseVirtualDisk {

    public StVirtualDisk(String name) {
        super(ImageType.atarist_ST, name);
    }

    public StVirtualDisk(ImageType type, String name) {
        super(type, name);
    }

}
