package eu.mizerak.alemiz.imgbin.utils;

import java.io.*;
import java.security.SecureRandom;

public class Utils {

    public static final String IMAGE_KEY_DELIMITER = "-";
    private static final String alphabet = "0123456789abcdefghijklmnoprstuvwxyzABCDEFGHIJKLMNOPRSTUVWXYZ";

    public static String generatePublicId(int len) {
        if (len < 1) {
            throw new IllegalArgumentException("String length must be higher than 0!");
        }

        SecureRandom random = new SecureRandom();

        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return new String(chars);
    }

    public static String generateImageId(int privateId, String publicId) {
        return privateId+"-"+publicId;
    }

    public static String getPublicId(String imageId) {
        String[] parts = imageId.split(IMAGE_KEY_DELIMITER);
        if (parts.length < 2) {
            throw new IllegalStateException("Malformed image ID provided!");
        }
        return parts[1];
    }

    public static int getPrivateId(String imageId) {
        String[] parts = imageId.split(IMAGE_KEY_DELIMITER);
        if (parts.length < 2) {
            throw new IllegalStateException("Malformed image ID provided!");
        }
        return Integer.parseInt(parts[0]);
    }

    public static String error(String message) {
        return "{\"status\":\"error\",\"message\":\""+message+"\"}";
    }

    public static String success(String message) {
        return "{\"status\":\"ok\",\"message\":\""+message+"\"}";
    }

    public static String success(String message, String result) {
        return "{\"status\":\"ok\",\"message\":\""+message+"\",\"result\":\""+result+"\"}";
    }

    public static void saveFromResources(String fileName, File targetFile) throws IOException {
        if (targetFile.exists()) {
            return;
        }
        targetFile.createNewFile();

        InputStream inputStream = Utils.class.getClassLoader().getResourceAsStream(fileName);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        OutputStream outputStream = new FileOutputStream(targetFile);
        outputStream.write(buffer);
        inputStream.close();
    }
}
