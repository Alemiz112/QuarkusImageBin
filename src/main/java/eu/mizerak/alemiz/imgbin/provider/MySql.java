package eu.mizerak.alemiz.imgbin.provider;

import com.google.gson.JsonObject;
import eu.mizerak.alemiz.imgbin.utils.Utils;

import java.sql.*;

public class MySql {

    private static MySql instance;

    public static MySql init(JsonObject config) {
        if (instance != null) {
            throw new RuntimeException("MySQL was already initialized!");
        }
        return instance = new MySql(config);
    }

    public static MySql get() {
        return instance;
    }

    private final MySQLConnectionInfo connectionInfo;

    public MySql(JsonObject config) {
        String host = config.get("mysql-host").getAsString();
        String database = config.get("mysql-database").getAsString();
        String user = config.get("mysql-user").getAsString();
        String password = config.get("mysql-password").getAsString();

        // Overwrite using env-vars if present
        if (System.getenv("DB_HOST") != null) {
            host = System.getenv("DB_HOST");
        }
        if (System.getenv("DB_DATABASE") != null) {
            database = System.getenv("DB_DATABASE");
        }
        if (System.getenv("DB_USER") != null) {
            user = System.getenv("DB_USER");
        }
        if (System.getenv("DB_PASS") != null) {
            password = System.getenv("DB_PASS");
        }

        this.connectionInfo = new MySQLConnectionInfo(host, database, user, password);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            throw new RuntimeException("Unable to load MySql driver", e);
        }
    }

    public void onStartup() throws SQLException {
        try (Connection conn = this.connectionInfo.getMySqlInstance()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS images(id INT AUTO_INCREMENT primary key NOT NULL, public_id TEXT, image_name TEXT, image LONGBLOB);");
        }
    }

    /**
     * Uploads image and returns its assigned private ID
     * @param imageName name of image defined by user
     * @param publicId randomly generated public image id
     * @param imageBytes bytes of image
     * @return self-incremented private image id
     * @throws SQLException
     */
    public int uploadImage(String imageName, String publicId, byte[] imageBytes) throws SQLException {
        try (Connection conn = this.connectionInfo.getMySqlInstance()) {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO `images` (public_id, image_name, image) values(?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, publicId);
            statement.setString(2, imageName);
            statement.setBytes(3, imageBytes);
            statement.executeUpdate();

            ResultSet result = statement.getGeneratedKeys();
            result.next();
            return result.getInt(1);
        }
    }

    public byte[] getImageData(String imageId) throws SQLException {
        String publicId = Utils.getPublicId(imageId);
        int privateId = Utils.getPrivateId(imageId);

        try (Connection conn = this.connectionInfo.getMySqlInstance()) {
            ResultSet query = conn.createStatement().executeQuery("SELECT image FROM `images` WHERE `id`="+privateId+" AND `public_id`='"+publicId+"';");
            query.next();

            if (query.getRow() < 1) {
                return null;
            }
            return query.getBytes("image");
        }
    }
}
