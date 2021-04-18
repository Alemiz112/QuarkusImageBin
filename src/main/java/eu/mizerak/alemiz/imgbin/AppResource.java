package eu.mizerak.alemiz.imgbin;

import eu.mizerak.alemiz.imgbin.provider.ImageManager;
import eu.mizerak.alemiz.imgbin.utils.Utils;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class AppResource {

    @POST
    @Path("upload-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> uploadImage(@MultipartForm UploadForm form) {
        return Uni.createFrom().item(() -> this.uploadImage0(form));
    }

    private String uploadImage0(UploadForm form) {
        String imageName = form.getImageName();
        if (imageName == null || imageName.trim().isEmpty()) {
            return Utils.error("invalid imageName");
        }

        byte[] imageBytes = form.getImageBytes();
        if (imageBytes == null || imageBytes.length < 1) {
            return Utils.error("invalid image");
        }

        ImageManager imageManager = ImageManager.get();
        if (imageBytes.length > imageManager.getMaxImageBytesSize()) {
            return Utils.error("too big file");
        }

        String imageId = imageManager.uploadImage(imageName, imageBytes);
        if (imageId == null) {
            return Utils.error("upload failed");
        }
        return Utils.success("uploaded", imageId);
    }

    @GET
    @Path("image")
    @Produces(MediaType.TEXT_PLAIN)
    public Response voidImage() {
        // Deny requests with no image ID
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("image/{imageId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Response> image(@PathParam String imageId) {
        return Uni.createFrom().item(() -> this.image0(imageId));
    }

    private Response image0(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        byte[] imageBytes = ImageManager.get().getImage(imageId);
        if (imageBytes == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(imageBytes).build();
    }
}