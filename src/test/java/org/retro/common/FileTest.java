package org.retro.common;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class FileTest {

    @Test
    public void testFileSuffix() {
        VirtualFile file = new VirtualFile("myfile.prg");
        assertThat(file.getSuffix(), is("prg"));

        file = new VirtualFile("my.file.prg");
        assertThat(file.getSuffix(), is("prg"));

        file = new VirtualFile("myfile");
        assertThat(file.getSuffix(), is(nullValue()));
    }

    @Test
    public void testFileEquals() {
        String filename = "DUMMY.PRG";
        VirtualDirectory parent = new VirtualDirectory("STUFF");
        ByteBuffer content = ByteBuffer.allocate(1000);

        VirtualFile virtualFileOne = new VirtualFile(parent, filename, content);

        VirtualFile virtualFileTwo = new VirtualFile(filename);
        virtualFileTwo.setContent(content);
        virtualFileTwo.setParent(parent);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(true));

        virtualFileOne = new VirtualFile(filename);
        virtualFileTwo = new VirtualFile(parent, filename);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(false));

        virtualFileOne = new VirtualFile(parent, filename);
        virtualFileTwo = new VirtualFile(filename);

        assertThat(virtualFileOne.equals(virtualFileTwo), is(false));
    }

    @Test
    public void testCreateFile() {
        String filename = "DUMMY.PRG";
        VirtualDirectory parent = new VirtualDirectory("STUFF");
        ByteBuffer content = ByteBuffer.allocate(1000);

        VirtualFile virtualFile = new VirtualFile(filename);
        assertThat(virtualFile.getName(), is(filename));

        virtualFile = new VirtualFile(filename);
        assertThat(virtualFile.getName(), is(filename));

        virtualFile = new VirtualFile(parent, filename);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(parent));

        virtualFile = new VirtualFile(parent, filename);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(notNullValue()));
        assertThat(virtualFile.getParent(), is(parent));

        virtualFile = new VirtualFile(parent, filename, content);
        assertThat(virtualFile.getName(), is(filename));
        assertThat(virtualFile.getParent(), is(notNullValue()));
        assertThat(virtualFile.getParent(), is(parent));
        assertThat(virtualFile.getContent(), is(notNullValue()));
        assertThat(virtualFile.getContent(), is(content));
    }
}
