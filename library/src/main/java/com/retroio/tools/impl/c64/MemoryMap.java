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

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * C64 memory map.
 *
 * @author Thomas Frauenknecht
 */
public class MemoryMap {

    private static TreeMap<Long, Memory> memoryMap = new TreeMap<Long, Memory>();

    private MemoryMap() {
        /* singleton */
    }

    public static void addSegment(long adr, Memory mem) {
        memoryMap.put(new Long(adr), mem);
    }

    public static Memory findSegment(int adress) {

        Iterator<Long> keyIterator = memoryMap.keySet().iterator();
        while (keyIterator.hasNext()) {
            Long key = keyIterator.next();
            Memory value = memoryMap.get(key);
//			System.out.println("MemoryMap::findSegment(); Range " + DataUtils.toHex4(value.base) + "-" + DataUtils.toHex4(value.base+value.size));
            if ((adress >= value.base) && (adress <= value.base + value.size)) {
                return value;
            }
        }
//		System.out.println("MemoryMap::findSegment(); no memory defined for adress " + DataUtils.toHex4(adress));
        return null;
    }

    public static void show() {
        Collection<Memory> c = memoryMap.values();
        Iterator<Memory> itr = c.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }
}
