package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.api.DBConfiguration;
import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.configuration.PluginConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitConfiguration implements PluginConfiguration {

    private final @NotNull FileConfiguration configuration;

    protected BukkitConfiguration(@NotNull FileConfiguration configuration) {
        this.configuration = configuration;
    }

    public @NotNull FileConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public boolean isDebugEnabled() {
        return getConfiguration().getBoolean("debug", false);
    }

    @Override
    public boolean isMetricsEnabled() {
        return getConfiguration().getBoolean("metrics", true);
    }

    @Override
    public boolean isUpdateCheckerEnabled() {
        return getConfiguration().getBoolean("check-update", true);
    }

    @Override
    public boolean isPropertiesEnabled() {
        return getConfiguration().getBoolean("properties.enable", true);
    }

    @Override
    public String getPropertiesFolder() {
        return getConfiguration().getString("properties.folder", "db-properties/");
    }

    @Override
    public @NotNull Map<String, DBConfiguration> getDBConfigurations() {
        Map<String, DBConfiguration> dbConfigurations = new LinkedHashMap<>();
        ConfigurationSection dbConfigurationsSection = getConfiguration().getConfigurationSection("databases");
        if (dbConfigurationsSection != null) {
            for (String dbName : dbConfigurationsSection.getKeys(false)) {
                if (dbName.startsWith("example-")) continue;
                ConfigurationSection dbSection = dbConfigurationsSection.getConfigurationSection(dbName);
                if (dbSection == null) continue;

                String driverString = dbSection.getString("driver-type");
                SQLDriverType driverType = SQLDriverType.parse(driverString);
                if (driverType == null) {
                    MineSQLBukkit.getInstance().error("不存在预设的驱动类型 " + driverString + "," + " 请检查配置文件 databases." + dbName + "。");
                    continue;
                }
                String host = dbSection.getString("host");
                if (host == null) {
                    MineSQLBukkit.getInstance().error("地址配置不得为空," + " 请检查配置文件 databases." + dbName + "。");
                    continue;
                }

                int port = dbSection.getInt("port", -1);
                if (port < 0) {
                    MineSQLBukkit.getInstance().error("端口未配置正确," + " 请检查配置文件 databases." + dbName + "。");
                    continue;
                }
                DBConfiguration dbConfiguration = DBConfiguration.create(driverType, host + ":" + port);

                String username = dbSection.getString("username");
                String password = dbSection.getString("password");

                dbConfiguration.setUsername(username);
                dbConfiguration.setPassword(password);

                dbConfigurations.put(dbName, dbConfiguration);
            }
        }
        return dbConfigurations;
    }
}
