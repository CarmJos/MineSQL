package cc.carm.plugin.easysql.util;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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


}
