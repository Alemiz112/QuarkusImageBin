package eu.mizerak.alemiz.imgbin.provider;

import com.google.gson.JsonObject;
import eu.mizerak.alemiz.imgbin.utils.Utils;
import org.jboss.logging.Logger;

import java.sql.SQLException;

public class ImageManager {

    private static final Logger log = Logger.getLogger(ImageManager.class);
    private static ImageManager instance;

    public static ImageManager init(JsonObject config) {
        if (instance != null) {
            throw new RuntimeException("ImageManager was already initialized!");
        }
        return instance = new ImageManager(config);
    }

    public static ImageManager get() {
        return instance;
    }

    private final int imageKeyLength;
    private final int maxImageBytesSize;

    public ImageManager(JsonObject config) {
        int imageKeyLength = config.get("public-key-length").getAsInt();
        if (System.getenv("IMG_KEY_LEN") != null) {
            imageKeyLength = Integer.parseInt(System.getenv("IMG_KEY_LEN"));
        }
        this.imageKeyLength = imageKeyLength;
        log.info("Setting image key length to " + this.imageKeyLength);

        int maxSize = config.get("max-image-size").getAsInt(); // in MB
        if (System.getenv("IMG_MAX_SIZE") != null) {
            maxSize = Integer.parseInt(System.getenv("IMG_MAX_SIZE"));
        }

        this.maxImageBytesSize = maxSize * 1024 * 1024; // in Bytes
        log.info("Setting max image size to " + maxSize + "MB");
    }

    public String uploadImage(String imageName, byte[] imageBytes) {
        String publicId = Utils.generatePublicId(this.imageKeyLength);

        MySql mySql = MySql.get();
        int privateId;
        try {
             privateId = mySql.uploadImage(imageName, publicId, imageBytes);
        } catch (SQLException e) {
            log.error("Failed to upload image to database!", e);
            return null;
        }

        log.debug("Uploaded image with privateId="+privateId);
        return Utils.generateImageId(privateId, publicId);
    }

    public byte[] getImage(String imageId) {
        try {
            log.debug("Retrieving image with imageId="+imageId);
            return MySql.get().getImageData(imageId);
        } catch (SQLException e) {
            log.error("Failed to load image from database! imageId="+imageId, e);
        }
        return null;
    }

    public int getMaxImageBytesSize() {
        return this.maxImageBytesSize;
    }
}
