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

    @Test
    public void testSorting() {
/*
        VirtualDirectory root = new VirtualDirectory("/");
        addFilesToFolder(root);

        VirtualDirectory x1 = new VirtualDirectory(root,"x1");
        addFilesToFolder(x1);
        VirtualDirectory f1 = new VirtualDirectory(root,"f1");
        addFilesToFolder(f1);
        VirtualDirectory a1 = new VirtualDirectory(root,"a1");
        addFilesToFolder(a1);

        List<VirtualFile> contents = root.getContents();
        Iterator<VirtualFile> entries = contents.iterator();

        // 1st all subfolders
        assertThat(entries.next().getName(), is("a1"));
        assertThat(entries.next().getName(), is("f1"));
        assertThat(entries.next().getName(), is("x1"));
        // then all files
        assertThat(entries.next().getName(), is("aaa"));
        assertThat(entries.next().getName(), is("iii"));
        assertThat(entries.next().getName(), is("zzz"));

        VirtualFile sub1 = root.getContents().get(0);
        assertThat(sub1.isDirectory(), is(true));
        assertThat(sub1.getName(), is("a1"));
        VirtualFile sub2 = root.getContents().get(1);
        assertThat(sub2.isDirectory(), is(true));
        assertThat(sub2.getName(), is("f1"));
        VirtualFile sub3 = root.getContents().get(2);
        assertThat(sub3.isDirectory(), is(true));
        assertThat(sub3.getName(), is("x1"));

        VirtualFile file1 = root.getContents().get(3);
        assertThat(file1.isFile(), is(true));
        */
    }

    private void addFilesToFolder(VirtualDirectory folder) {
        folder.addFile(new VirtualFile("zzz"));
        folder.addFile(new VirtualFile("iii"));
        folder.addFile(new VirtualFile("aaa"));
    }
}
