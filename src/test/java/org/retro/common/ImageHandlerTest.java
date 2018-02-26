package org.retro.common;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Iterator;

public class ImageHandlerTest {

    @Test
    public void testReadDOSImage() throws Exception {
        System.out.println("======== DOS image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.dos_IMG);

        java.io.File imageFile = Files.createTempFile("dos", "image").toFile();
        imageFile.deleteOnExit();

        IOUtils.copy(getClass().getResourceAsStream("/images/dos/Dos3.3.img"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
//        list(root);

        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
    }

    @Test
    public void testReadAtariSTImage() throws Exception {
        System.out.println("======== Atari ST image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.atarist_ST);

        java.io.File imageFile = Files.createTempFile("st", "image").toFile();
        imageFile.deleteOnExit();

        IOUtils.copy(getClass().getResourceAsStream("/images/atarist/A_000.ST"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
//        list(root);
        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
    }

    @Test
    public void testReadC64Image() throws Exception {
        System.out.println("======== C64 image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.c64_D64);

        java.io.File imageFile = Files.createTempFile("d64", "image").toFile();
        imageFile.deleteOnExit();

        IOUtils.copy(getClass().getResourceAsStream("/images/c64/ARKANDOH.D64"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
        list(root);
        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
    }

    private void list(VirtualDirectory virtualDirectory) {
        Iterator<VirtualFile> iter = virtualDirectory.iterator();
        while(iter.hasNext()) {
            VirtualFile virtualFile = (VirtualFile)iter.next();
            if(virtualFile.isDirectory()) {
                System.out.println("> DIRECTORY: " + virtualFile.getName());
                list((VirtualDirectory) virtualFile);
            } else {
                System.out.println("--> virtualFile: " + virtualFile.getName());
            }
        }
    }
}
