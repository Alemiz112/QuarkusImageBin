package eu.mizerak.alemiz.imgbin;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

public class UploadForm {

    @FormParam("imageName")
    @PartType(MediaType.TEXT_PLAIN)
    private String imageName;

    @FormParam("image")
    private byte[] imageBytes;

    public String getImageName() {
        return this.imageName;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }
}
