package cc.carm.plugin.easysql.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ResourceReadUtil {


    public static @Nullable String[] readResource(@Nullable InputStream resourceStream) {
        if (resourceStream == null) return null;
        try (Scanner scanner = new Scanner(resourceStream, "UTF-8")) {
            List<String> contents = new ArrayList<>();
            while (scanner.hasNextLine()) {
                contents.add(scanner.nextLine());
            }
            return contents.toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMavenPropertiesPath(@NotNull String groupID, @NotNull String artifactID) {
        return String.format("/META-INF/maven/%s/%s/pom.properties", groupID, artifactID);
    }

    public static synchronized @Nullable String getVersion(@NotNull Object provider,
                                                           @NotNull String groupID,
                                                           @NotNull String artifactID) {
        String path = getMavenPropertiesPath(groupID, artifactID);
        String version = null;
        // Using maven properties to get the version
        try (InputStream is = provider.getClass().getResourceAsStream(path)) {
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                version = p.getProperty("version", "");
            }
        } catch (Exception ignored) {
        }

        if (version != null) return version;

        // Fine, lets try Java API
        Package pkg = provider.getClass().getPackage();
        if (pkg != null) {
            version = pkg.getImplementationVersion();
            if (version == null) {
                version = pkg.getSpecificationVersion();
            }
        }

        return version;
    }


}
