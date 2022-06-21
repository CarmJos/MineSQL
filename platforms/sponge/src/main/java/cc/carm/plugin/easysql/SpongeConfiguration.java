package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.SQLDriverType;
import cc.carm.plugin.easysql.configuration.PluginConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2022/6/20<br>
 * MineSQL<br>
 *
 * @author huanmeng_qwq
 */
public class SpongeConfiguration implements PluginConfiguration {
    private final ConfigurationNode config;

    public SpongeConfiguration(ConfigurationNode config) {
        this.config = config;
    }

    @Override
    public boolean isDebugEnabled() {
        return config.node("debug").getBoolean(false);
    }

    @Override
    public boolean isMetricsEnabled() {
        return config.node("metrics").getBoolean(false);
    }

    @Override
    public boolean isUpdateCheckerEnabled() {
        return config.node("check-update").getBoolean(true);
    }

    @Override
    public boolean isPropertiesEnabled() {
        return config.node("properties", "enable").getBoolean(true);
    }

    @Override
    public String getPropertiesFolder() {
        return config.node("properties", "folder").getString("db-properties/");
    }

    @Override
    public @NotNull Map<String, DBConfiguration> getDBConfigurations() {
        Map<String, DBConfiguration> dbConfigurations = new LinkedHashMap<>();
        ConfigurationNode dbConfigurationsSection = config.node("databases");
        if (dbConfigurationsSection != null) {
            for (Map.Entry<Object, ? extends ConfigurationNode> dbEntry : dbConfigurationsSection.childrenMap().entrySet()) {
                if (dbEntry.getKey().toString().startsWith("example-")) continue;
                ConfigurationNode dbSection = dbEntry.getValue();
                if (dbSection == null) continue;

                String driverString = dbSection.getString("driver-type");
                SQLDriverType driverType = SQLDriverType.parse(driverString);
                if (driverType == null) {
                    EasySQLSponge.getInstance().getLog().error("不存在预设的驱动类型 " + driverString + "," + " 请检查配置文件 databases." + dbEntry + "。");
                    continue;
                }
                String host = dbSection.getString("host");
                if (host == null) {
                    EasySQLSponge.getInstance().getLog().error("地址配置不得为空," + " 请检查配置文件 databases." + dbEntry + "。");
                    continue;
                }

                int port = dbSection.node("port").getInt(-1);
                if (port < 0) {
                    EasySQLSponge.getInstance().getLog().error("端口未配置正确," + " 请检查配置文件 databases." + dbEntry + "。");
                    continue;
                }
                DBConfiguration dbConfiguration = DBConfiguration.create(driverType, host + ":" + port);

                String username = dbSection.getString("username");
                String password = dbSection.getString("password");

                dbConfiguration.setUsername(username);
                dbConfiguration.setPassword(password);
                dbConfigurations.put(dbEntry.getKey().toString(), dbConfiguration);
            }
        }
        return dbConfigurations;
    }
}
