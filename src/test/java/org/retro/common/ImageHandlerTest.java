package org.retro.common;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
        list(root);

        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
    }

    @Test
    public void testReadAmigaADFImage() throws Exception {
        System.out.println("======== Amiga ADF image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.amiga_ADF);

        Path tempDirectory = Files.createTempDirectory("adf");
        java.io.File imageFile = new File(tempDirectory.toFile(), "DMAssist1.adf");

        IOUtils.copy(getClass().getResourceAsStream("/images/amiga/DMAssist1.adf"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
//        root.getContents().forEach(f -> System.out.println("Entry: " + f.getFullName() + ", is file: " + f.isFile()));
        list(root);
        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
//        handler.extractInZipArchive(virtualDisk, targetDir);
    }

    @Test
    public void testReadAtariMSAImage() throws Exception {
        System.out.println("======== Atari MSA image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.atarist_MSA);

        Path tempDirectory = Files.createTempDirectory("msa");
        java.io.File imageFile = new File(tempDirectory.toFile(), "HDDRIVER.MSA");

        IOUtils.copy(getClass().getResourceAsStream("/images/atarist/HDDRIVER.MSA"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
//        root.getContents().forEach(f -> System.out.println("Entry: " + f.getFullName() + ", is file: " + f.isFile()));
        list(root);
        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
//        handler.extractInZipArchive(virtualDisk, targetDir);
    }

    @Test
    public void testReadAtariSTImage() throws Exception {
        System.out.println("======== Atari ST image ===========");
        ImageHandler handler = ImageHandlerFactory.get(ImageType.atarist_ST);

        Path tempDirectory = Files.createTempDirectory("st");
        java.io.File imageFile = new File(tempDirectory.toFile(), "007BATMAN.ST");

        IOUtils.copy(getClass().getResourceAsStream("/images/atarist/007BATMAN.ST"), new FileOutputStream(imageFile));

        VirtualDisk virtualDisk = handler.loadImage(imageFile);

        VirtualDirectory root = virtualDisk.getRootContents();
//        root.getContents().forEach(f -> System.out.println("Entry: " + f.getFullName() + ", is file: " + f.isFile()));
        list(root);
        File targetDir = new File("target");
//        handler.extractVirtualDisk(virtualDisk, targetDir);
        handler.extractInZipArchive(virtualDisk, targetDir);
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
        handler.extractVirtualDisk(virtualDisk, targetDir);
    }

    private void list(VirtualDirectory virtualDirectory) {
        Iterator<VirtualFile> iter = virtualDirectory.iterator();
        while(iter.hasNext()) {
            VirtualFile virtualFile = (VirtualFile)iter.next();
            if(virtualFile.isDirectory()) {
                System.out.println("> DIRECTORY: " + virtualFile.getFullName() + " (" + virtualFile.getName() + ")");
                list((VirtualDirectory) virtualFile);
            } else {
                System.out.println("--> virtualFile: " + virtualFile.getFullName() + " (" + virtualFile.getName() + ")");
            }
        }
    }
}
