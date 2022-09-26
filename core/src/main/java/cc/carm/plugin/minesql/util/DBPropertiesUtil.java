package cc.carm.plugin.minesql.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DBPropertiesUtil {

    public static Map<String, Properties> readFromFolder(File propertiesFolder) {
        Map<String, Properties> propertiesMap = new HashMap<>();
        if (!propertiesFolder.exists() || !propertiesFolder.isDirectory()) return propertiesMap;

        File[] files = propertiesFolder.listFiles();
        if (files == null || files.length == 0) return propertiesMap;
        for (File file : files) {
            if (!validateName(file.getName())) continue;
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            try (InputStream is = new FileInputStream(file)) {
                propertiesMap.put(name, read(is));
            } catch (IOException ignored) {
            }
        }

        return propertiesMap;
    }

    public static @NotNull Properties read(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    public static boolean validateName(String name) {
        return !name.contains(" ") && !name.startsWith(".") && name.endsWith(".properties");
    }

}
