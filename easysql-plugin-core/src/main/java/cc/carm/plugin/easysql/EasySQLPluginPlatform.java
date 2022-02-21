package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.EasySQLRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public interface EasySQLPluginPlatform {

    @NotNull EasySQLRegistry getRegistry();

    @NotNull Map<String, DBConfiguration> readConfigurations();

    @NotNull Map<String, Properties> readProperties();

    Logger getLogger();

    default void initializeAPI(EasySQLRegistry registry) {
        EasySQLAPI.initializeAPI(registry);
    }

}
