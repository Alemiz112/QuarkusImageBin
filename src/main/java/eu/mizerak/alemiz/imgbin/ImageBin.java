package eu.mizerak.alemiz.imgbin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mizerak.alemiz.imgbin.provider.ImageManager;
import eu.mizerak.alemiz.imgbin.provider.MySql;
import eu.mizerak.alemiz.imgbin.utils.Utils;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

@QuarkusMain
public class ImageBin implements QuarkusApplication {

    private static final Logger log = Logger.getLogger(ImageBin.class);
    public static String dataPath = System.getProperty("user.dir") + "/";

    public static void main(String[] args) {
        Quarkus.run(ImageBin.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        log.info("Starting Quarkus-ImageBin!");

        File configFile = new File(dataPath, "config.json");
        if (!configFile.exists()) {
            Utils.saveFromResources("config.json", configFile);
        }

        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(new FileInputStream(configFile)));
        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException("Provided invalid config!");
        }
        JsonObject config = jsonElement.getAsJsonObject();

        MySql mysql = MySql.init(config);
        ImageManager.init(config);

        mysql.onStartup();
        Quarkus.waitForExit();
        return 0;
    }
}
