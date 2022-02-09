package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.DBConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public interface EasySQLPluginPlatform {

    @NotNull Map<String, DBConfiguration> readConfigurations();

    @NotNull Map<String, Properties> readProperties();

    Logger getLogger();

}
