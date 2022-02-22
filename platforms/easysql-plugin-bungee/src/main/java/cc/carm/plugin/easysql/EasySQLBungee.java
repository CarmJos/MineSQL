package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.EasySQLRegistry;
import cc.carm.plugin.easysql.util.PropertiesUtil;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EasySQLBungee extends Plugin implements EasySQLPluginPlatform {

    private boolean setup = false;

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setup() {
        if (setup) return;
        saveDefaultConfig();
        // 读取配置文件 - 预注册 instance
        Configuration configuration;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Configuration instancesConfig = configuration.getSection("instances");
        if (instancesConfig != null) {
        }
        setup = true;
    }

    @Override
    public void onLoad() {
        setup();
    }

    @Override
    public void onEnable() {
        setup();
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
    }

    @Override
    public @NotNull EasySQLRegistry getRegistry() {
        return null;
    }

    @Override
    public @NotNull Map<String, DBConfiguration> readConfigurations() {
        return new HashMap<>();
    }

    @Override
    public @NotNull Map<String, Properties> readProperties() {
        return PropertiesUtil.readDBProperties(new File(getDataFolder(), "db-properties"));
    }


}
