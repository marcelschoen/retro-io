package org.acme;

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
        File imageFile = new File(pathDir, imageName);

        String body = "<html><body>";
        body += createHeaderDiv(path, imageName);
        body += "<h3>Contents</h3>";
        body += new FolderNode(path, pathDir, true).getFolderDivTag();
        body += "</body></html>";
        return Response.status(200).entity(body).build();
    }

    private String createHeaderDiv(String path, String filename) {
        String headerDiv = "<div>";

        headerDiv += "<h3>Image: " + filename + "</h3>";

        headerDiv += "<ul>";
        headerDiv += "<li>Download <a download href=\"/download/zip?path=" + path + "&image=" + filename + "\">as ZIP</a></li>";
        headerDiv += "</ul>";

        return headerDiv + "</div>";
    }
}
