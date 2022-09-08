package org.acme;

import org.apache.commons.io.IOUtils;
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
import java.util.List;
import java.util.Map;

/**
 * Information "stolen" from:
 *
 * https://github.com/tivrfoa/resteasy-file-upload-with-quarkus/blob/main/src/main/java/org/acme/rest/client/multipart/MultipartClientResource.java
 */
@Path("/handler")
public class FloppyImageUploadResource {

    // https://mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
    private final String UPLOADED_FILE_PATH = ""; // will go to 'target' folder

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) {

        String fileName = "";
System.out.println("> Process parts...");
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");

        for (InputPart inputPart : inputParts) {
System.out.println("-> part");
            try {

                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);

                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                // constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;

                writeFile(bytes, fileName);

                System.out.println("Done");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
System.out.println("> Done");
        return Response.status(200).entity("uploadFile is called, Uploaded file name : " + fileName).build();

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
    private void writeFile(byte[] content, String filename) throws IOException {

        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(file);

        fop.write(content);
        fop.flush();
        fop.close();

    }
}
