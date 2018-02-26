package org.retro.common;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class FileTest {

    @Test
    public void testFileEquals() {
        String filename = "DUMMY.PRG";
        long lastModified = 11223344L;
        VirtualDirectory parent = new VirtualDirectory("STUFF");
        ByteBuffer content = ByteBuffer.allocate(1000);

        VirtualFile virtualFileOne = new VirtualFile(parent, filename, content, lastModified);

        VirtualFile virtualFileTwo = new VirtualFile(filename);
        virtualFileTwo.setContent(content);
        virtualFileTwo.setLastModified(lastModified);
        virtualFileTwo.setParent(parent);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(true));

        virtualFileOne = new VirtualFile(filename, lastModified);
        virtualFileTwo = new VirtualFile(parent, filename, lastModified);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(false));

        virtualFileOne = new VirtualFile(parent, filename, lastModified);
        virtualFileTwo = new VirtualFile(filename, lastModified);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(false));
    }

    @Test
    public void testCreateFile() {
        String filename = "DUMMY.PRG";
        long lastModified = 11223344L;
        VirtualDirectory parent = new VirtualDirectory("STUFF");
        ByteBuffer content = ByteBuffer.allocate(1000);

        VirtualFile virtualFile = new VirtualFile(filename);
        assertThat(virtualFile.getName(), is(filename));

        virtualFile = new VirtualFile(filename, lastModified);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getLastModified(), is(lastModified));

        virtualFile = new VirtualFile(parent, filename);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(parent));

        virtualFile = new VirtualFile(parent, filename, lastModified);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(notNullValue()));
        assertThat(virtualFile.getParent(), is(parent));
        assertThat(virtualFile.getLastModified(), is(lastModified));

        virtualFile = new VirtualFile(parent, filename, content);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(notNullValue()));
        assertThat(virtualFile.getParent(), is(parent));
        assertThat(virtualFile.getContent(), is(notNullValue()));
        assertThat(virtualFile.getContent(), is(content));
    }
}
