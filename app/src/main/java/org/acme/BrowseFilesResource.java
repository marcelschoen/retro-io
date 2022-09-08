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
    public Response browse(@QueryParam("path") String path) {
        File pathDir = new File(uploadDirectory, path);
        File[] contents = pathDir.listFiles();
        String body = "<html><body>";

        for(File entry : contents) {
            body += "<h3>" + entry.getName() + " / " + entry.length() + " bytes</h3>";
        }

        body += "</body></html>";

        return Response.status(200).entity(body).build();
    }
}
