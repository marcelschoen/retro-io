package org.retro.common.impl.amiga;

import org.retro.common.VirtualDirectory;
import org.retro.common.VirtualDisk;
import org.retro.common.VirtualDiskException;
import org.retro.common.VirtualFile;
import org.retro.common.impl.AbstractBaseImageHandler;
import org.retro.common.impl.amiga.adf.AdfDirectory;
import org.retro.common.impl.amiga.adf.AdfFile;
import org.retro.common.impl.amiga.adf.AdfImage;

import javax.script.Invocable;
import java.io.File;

/**
 * Handler for Commodore Amiga ADF disk images.
 *
 * NOT IMPLEMENTED YET
 *
 * @author Marcel Schoen
 */
public class AmigaImageHandler extends AbstractBaseImageHandler {

    Invocable invocable;

    @Override
    public VirtualDisk loadImage(File imageFile) throws VirtualDiskException {
        String diskName = imageFile.getName();

        try {
            AdfImage adfImage = new AdfImage(imageFile);
            System.out.println("Disk format: " + adfImage.getImageInfo().getDiskFormat());
            System.out.println("Disk type: " + adfImage.getImageInfo().getDiskType().name());
            System.out.println("Disk INTL: " + adfImage.getImageInfo().isINTL());
            System.out.println("Disk DIRC: " + adfImage.getImageInfo().isDIRC());
            System.out.println("Disk root block: " + adfImage.getImageInfo().getRootBlock());
            System.out.println("Disk label: " + adfImage.getImageInfo().getLabel());

            AmigaVirtualDisk virtualDisk = new AmigaVirtualDisk(diskName);
            VirtualDirectory root = new VirtualDirectory("/");
            virtualDisk.setRootContent(root);

            processDir(root, adfImage.getRootFolder());

            return virtualDisk;

        } catch(Exception e) {
            throw new VirtualDiskException("Failed to process ADF image: " + e, e);
        }
    }

    private void processDir(VirtualDirectory parent, AdfDirectory directory) {
        VirtualDirectory newDir = new VirtualDirectory(parent, directory.getName());
        for(AdfFile file : directory.getFileEntries()) {
            VirtualFile newFile = new VirtualFile(newDir, file.getName());
            System.out.println("Set content in file: " + file.getName());
            newFile.setContent(file.getContent());
        }
        for(AdfDirectory dir : directory.getSubDirectories()) {
            processDir(newDir, dir);
        }
    }
}
