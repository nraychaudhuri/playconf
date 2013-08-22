package helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import play.Configuration;
import play.GlobalSettings;

import com.typesafe.config.ConfigFactory;

public class TestGlobalSettings {
    
    public static GlobalSettings testSettings = new GlobalSettings() {
        @Override
        public Configuration onLoadConfig(Configuration config, File path,
                ClassLoader classloader) {
            //ignore the default configuration
            Map<String, Object> dbSettings = new HashMap<String, Object>();
            dbSettings.put("db.testdb.driver", "org.h2.Driver");
            dbSettings.put("db.testdb.user", "sa");
            dbSettings.put("db.testdb.url",
                    "jdbc:h2:mem:playconftest;MODE=MySQL");
            dbSettings.put("ebean.testdb", "models.*, helpers.*");
            return new Configuration(ConfigFactory.parseMap(dbSettings));
        }
    };
}
