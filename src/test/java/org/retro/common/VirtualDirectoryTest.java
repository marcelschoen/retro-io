package org.retro.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VirtualDirectoryTest {

    @Test
    public void testCreateDirectory() {
        long lastModified = 11223344L;
        String dirname = "DATA";
        VirtualDirectory parent = new VirtualDirectory("ABOVE");

        VirtualDirectory virtualDirectory = new VirtualDirectory(dirname);
        assertThat(virtualDirectory.getName(), is(dirname));
    }
}
