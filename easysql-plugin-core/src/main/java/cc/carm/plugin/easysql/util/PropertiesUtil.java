package cc.carm.plugin.easysql.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    public static Map<String, Properties> readDBProperties(File propertiesFolder) {
        Map<String, Properties> propertiesMap = new HashMap<>();
        if (!propertiesFolder.isDirectory()) return propertiesMap;

        File[] files = propertiesFolder.listFiles();
        if (files == null || files.length == 0) return propertiesMap;
        for (File file : files) {
            if (file.getName().startsWith(".") || !file.getName().endsWith(".properties")) continue;
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            try (InputStream stream = new FileInputStream(file)) {
                Properties properties = new Properties();
                properties.load(stream);
                propertiesMap.put(name, properties);
            } catch (IOException ignored) {
            }
        }
        
        return propertiesMap;
    }

}
