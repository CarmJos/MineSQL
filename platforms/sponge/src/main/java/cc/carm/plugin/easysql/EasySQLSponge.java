package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.configuration.PluginConfiguration;
import cc.carm.plugin.easysql.util.DBPropertiesUtil;
import cc.carm.plugin.easysql.util.JarResourceUtils;
import cn.beecp.BeeDataSource;
import com.google.common.io.MoreFiles;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.bstats.charts.SimplePie;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 * 2022/6/20<br>
 * MineSQL<br>
 *
 * @author huanmeng_qwq
 */
@Plugin("easysql-plugin")
public class EasySQLSponge implements EasySQLPluginPlatform {
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;
    @Inject
    private org.apache.logging.log4j.Logger logger;

    private static EasySQLSponge instance;
    private ConfigurationNode root;
    private PluginConfiguration configuration;
    private EasySQLRegistryImpl registry;
    private PluginContainer pluginContainer;

    private final Metrics metrics;

    @Inject
    public EasySQLSponge(Metrics.Factory factory, PluginContainer pluginContainer) {
        this.metrics = factory.make(14075);
        instance = this;
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().path(resolveConfig()).build();
        try {
            this.root = loader.load();
            this.configuration = new SpongeConfiguration(root);
            this.registry = new EasySQLRegistryImpl(this);
            this.pluginContainer = pluginContainer;
            enable();
        } catch (ConfigurateException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Listener
    public void disable(StoppingEngineEvent<Server> e) {
        getLog().info("终止全部数据库连接...");
        for (String dbName : new HashSet<>(getRegistry().list().keySet())) {
            getLog().info("   正在关闭数据库 " + dbName + "...");
            SQLManager manager = getRegistry().get(dbName);
            getRegistry().shutdown(manager, activeQueries -> {
                getLog().info("   数据库 " + dbName + " 仍有有 " + activeQueries + " 条活动查询，强制关闭中...");
                if (manager.getDataSource() instanceof BeeDataSource) {
                    BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
                    dataSource.close();         //Close bee connection pool
                }
            });
        }
        getRegistry().getManagers().clear(); // release all managers
    }


    public void enable() {
        /*
        //todo acf-commands not support sponge8
        getLog().info("初始化指令管理器...");
        getLog().info("注册相关指令...");
        */

        if (getConfiguration().isMetricsEnabled()) {
            getLog().info("启用统计数据...");
            metrics.addCustomChart(new SimplePie("update_check",
                    () -> getConfiguration().isUpdateCheckerEnabled() ? "ENABLED" : "DISABLED")
            );
            metrics.addCustomChart(new SimplePie("properties_configuration",
                    () -> getConfiguration().isPropertiesEnabled() ? "ENABLED" : "DISABLED")
            );
        }

        if (getConfiguration().isUpdateCheckerEnabled()) {
            getLog().info("开始检查更新...");
            getRegistry().checkUpdate(pluginContainer.metadata().version().getQualifier());
        } else {
            getLog().info("已禁用检查更新，跳过。");
        }
    }

    @Override
    public @NotNull Map<String, DBConfiguration> readConfigurations() {
        return configuration.getDBConfigurations();
    }

    @Override
    public @NotNull Map<String, Properties> readProperties() {
        if (!getConfiguration().isPropertiesEnabled()) return new HashMap<>();
        String propertiesFolder = getConfiguration().getPropertiesFolder();
        if (propertiesFolder == null || propertiesFolder.length() == 0) return new HashMap<>();

        File file = new File(configDirectory.toFile(), propertiesFolder);
        if (!file.exists() || !file.isDirectory()) {
            try {
                JarResourceUtils.copyFolderFromJar(
                        "db-properties", file,
                        JarResourceUtils.CopyOption.COPY_IF_NOT_EXIST
                );
            } catch (Exception ex) {
                logger.error("初始化properties示例文件失败：" + ex.getMessage());
            }
        }

        return DBPropertiesUtil.readFromFolder(file);
    }


    private Path resolveConfig() {
        Path path = configDirectory.resolve("easysql.conf");
        if (!Files.exists(path)) {
            try {
                //noinspection UnstableApiUsage
                MoreFiles.createParentDirectories(configDirectory);
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("easysql.conf")) {
                    Files.copy(is, path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return path;
    }

    public ConfigurationNode getRootConfig() {
        return root;
    }

    public PluginConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public @NotNull EasySQLRegistryImpl getRegistry() {
        return registry;
    }

    public java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getLogger("easysql-plugin");
    }

    public static EasySQLSponge getInstance() {
        return instance;
    }

    public Logger getLog() {
        return logger;
    }
}
