package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.DBConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Logger;

public interface EasySQLPluginPlatform {

    @NotNull Map<String, DBConfiguration> readConfigurations();

    Logger getLogger();

}
