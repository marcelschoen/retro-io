package org.acme;

import com.retroio.tools.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * Allows to browse a directory with the contents of
 * an unpacked floppy image.
 */
@Path("/files")
public class BrowseFilesResource {

    @ConfigProperty(name = "upload.directory")
    String uploadDirectory;

    @GET
    @Path("/browse")
    @Produces("text/html")
    public Response browse(@QueryParam("path") String path, @QueryParam("image") String imageName) {
        File pathDir = new File(uploadDirectory, path);
        String baseDirName = path.substring(0, path.indexOf("/"));
        File baseDir = new File(uploadDirectory, baseDirName);
        File imageFile = new File(baseDir, imageName);
        System.out.println("Image file: " + imageFile.getAbsolutePath());
        ImageHandler imageHandler = ImageHandlerFactory.get(ImageType.getTypeFromFile(imageFile));
        VirtualDisk virtualDisk;
        try {
            virtualDisk = imageHandler.loadImage(imageFile);
        } catch (VirtualDiskException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }

        String body = "<!DOCTYPE html>\n<html lang=\"en\"><head>\n";
        body += "  <meta charset=\"UTF-8\">\n";
        body += "  <link rel=\"stylesheet\" href=\"/retroio.css\">\n";
        body += "  <title>RetroIO Floppy Image Browser</title>\n";
        body += "</head><body><div style=\"margin: 20px;\">\n";
        body += createHeaderDiv(path, imageName, virtualDisk);
        body += "<h3>Contents</h3>\n";

        body += "<div style=\"font-size: -1; font-size: 0.9rem; margin-left: 20px;\">";
        String uuidPathOnly = path.substring(0, path.indexOf("/"));

        body += new FolderNode(uuidPathOnly, pathDir, true).getFolderDivTag();
        body += "</div></div>";
        body += "</body></html>\n";
        return Response.status(200).entity(body).build();
    }

    private String createHeaderDiv(String path, String filename, VirtualDisk virtualDisk) {
        String headerDiv = "<div>\n";

        headerDiv += "<a href=\"/\">";
        headerDiv += "<img src=\"/images/retroio-logo-main.png\" alt=\"RetroIO\" />";
        headerDiv += "<img src=\"/images/floppy-image-loader-logo.png\" alt=\"Floppy Image Loader \" />";
        headerDiv += "</a>";

        headerDiv += "<h3>Image: " + filename + " / Platform: " + virtualDisk.getType().getPlatform() + "</h3>\n";

        headerDiv += "<ul>\n";
        headerDiv += "<li>Download <a download href=\"/download/zip?path=" + path + "&image=" + filename + "\">as ZIP</a></li>\n";
        headerDiv += "</ul>\n";

        return headerDiv + "</div>\n";
    }
}
