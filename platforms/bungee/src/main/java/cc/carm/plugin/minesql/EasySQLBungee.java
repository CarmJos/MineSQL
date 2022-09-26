package cc.carm.plugin.minesql;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class EasySQLBungee extends Plugin {

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

}
