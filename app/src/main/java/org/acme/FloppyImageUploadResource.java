package org.acme;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Information "stolen" from:
 *
 * https://github.com/tivrfoa/resteasy-file-upload-with-quarkus/blob/main/src/main/java/org/acme/rest/client/multipart/MultipartClientResource.java
 */
@Path("/handler")
public class FloppyImageUploadResource {

    // https://mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
    private final String UPLOADED_FILE_PATH = ""; // will go to 'target' folder

    @ConfigProperty(name = "upload.directory")
    String uploadDirectory;

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) {

        File uploadDir = new File(uploadDirectory);
        if(!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uuid = UUID.randomUUID().toString();
        File finalDir = new File(uploadDir, uuid);
        finalDir.mkdirs();

        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");

        for (InputPart inputPart : inputParts) {

            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                writeFile(bytes, new File(finalDir, fileName));

                System.out.println("Done");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        URI uri = URI.create("/files/browse?path=" + uuid);
        return Response.status(302).location(uri).build();

    }


    /**
     * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
     * name="file"; filename="filename.extension"] }
     **/
    // get uploaded filename, is there a easy way in RESTEasy?
    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    //save to somewhere
    private void writeFile(byte[] content, File targetFile) throws IOException {

        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(targetFile);

        fop.write(content);
        fop.flush();
        fop.close();

    }
}
