package org.acme;

import com.retroio.tools.ImageHandler;
import com.retroio.tools.ImageHandlerFactory;
import com.retroio.tools.ImageType;
import com.retroio.tools.VirtualDisk;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

@Path("/download")
public class DownloadFileResource {

    @ConfigProperty(name = "upload.directory")
    String uploadDirectory;

    @GET
    @Path("/zip")
    @Produces("application/zip")
    /**
     * Allows to download a ZIP archive with the contents of the uploaded floppy image.
     */
    public Response getFile(@QueryParam("path") String path, @QueryParam("image") String imageName) {
        try {
            File pathDir = new File(uploadDirectory, path);
            File imageFile = new File(pathDir, imageName);
            ImageHandler handler = ImageHandlerFactory.get(ImageType.getTypeFromFile(imageFile));
            VirtualDisk virtualDisk = handler.loadImage(imageFile);
            StreamingOutput zipOutput = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    virtualDisk.exportAsZip(outputStream);
                }
            };
            String zipName = imageName.substring(0, imageName.indexOf(".")) + ".zip";
            return Response.ok(zipOutput)
                    .type( "application/zip" )
                    .header( "Content-Disposition", "attachment; filename=\"" + zipName + "\"" )
                    .build();
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Unknown error: " + e).build();
        }
    }

    @GET
    @Path("/file")
    @Produces("application/octet-stream")
    /**
     * Allows to have a file extracted from the uploaded floppy image and download
     * it directly.
     */
    public Response getVirtualFile(@QueryParam("path") String path,
                                   @QueryParam("relativePath") String relativePath) {
        try {
            File pathDir = new File(uploadDirectory, path);
            File file = new File(pathDir, relativePath);
            if(file.exists() && file.isFile()) {
                StreamingOutput zipOutput = new StreamingOutput() {
                    @Override
                    public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                        byte[] buffer = new byte[4096];
                        int read = -1;
                        while((read = in.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        in.close();
                    }
                };
                String fileName = relativePath;
                if(fileName.contains("/")) {
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                }
                return Response.ok(zipOutput)
                        .type( "application/octet-stream" )
                        .header( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" )
                        .build();
            } else {
                System.err.println("*** FILE NOT FOUND: " + file.getAbsolutePath());
            }
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Unknown error: " + e).build();
        }
        return null;
    }
}
