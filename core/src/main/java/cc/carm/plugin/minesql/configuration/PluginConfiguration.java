package cc.carm.plugin.minesql.configuration;

import cc.carm.plugin.minesql.api.DBConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PluginConfiguration {

    boolean isDebugEnabled();

    boolean isMetricsEnabled();

    boolean isUpdateCheckerEnabled();

    boolean isPropertiesEnabled();

    String getPropertiesFolder();

    @NotNull Map<String, DBConfiguration> getDBConfigurations();
    
}
