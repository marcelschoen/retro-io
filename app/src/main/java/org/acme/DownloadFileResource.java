package org.acme;

import com.retroio.tools.ImageHandler;
import com.retroio.tools.ImageHandlerFactory;
import com.retroio.tools.ImageType;
import com.retroio.tools.VirtualDisk;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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
    @Path("/virtualFile")
    @Produces("application/octet-stream")
    /**
     * Allows to have a file extracted from the uploaded floppy image and download
     * it directly.
     */
    public Response getVirtualFile(@QueryParam("relativePath") String relativePath) {
        return null;
    }
}
