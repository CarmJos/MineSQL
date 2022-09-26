package cc.carm.plugin.minesql;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.lib.easyplugin.i18n.EasyPluginMessageProvider;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.plugin.minesql.api.DBConfiguration;
import cc.carm.plugin.minesql.util.DBPropertiesUtil;
import cc.carm.plugin.minesql.util.JarResourceUtils;
import cn.beecp.BeeDataSource;
import co.aikar.commands.PaperCommandManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MineSQLBukkit extends EasyPlugin implements MineSQLPlatform {

    public MineSQLBukkit() {
        super(new EasyPluginMessageProvider.zh_CN());
    }

    protected static MineSQLBukkit instance;

    private BukkitConfiguration configuration;
    private MineSQLRegistry registry;

    private PaperCommandManager commandManager;

    @Override
    protected void load() {
        MineSQLBukkit.instance = this;

        log("加载配置文件...");
        getInstance().saveDefaultConfig();
        this.configuration = new BukkitConfiguration(getInstance().getConfig());

        log("初始化EasySQL注册器...");
        this.registry = new MineSQLRegistry(this);

        log("初始化EasySQLAPI...");
        initializeAPI(getRegistry()); // 尽快地初始化接口，方便其他插件调用
    }

    @Override
    protected boolean initialize() {
        log("初始化指令管理器...");
        this.commandManager = new PaperCommandManager(this);
        log("注册相关指令...");
        initializeCommands(getCommandManager());

        if (getConfiguration().isMetricsEnabled()) {
            log("启用统计数据...");
            Metrics metrics = new Metrics(this, 14075);
            metrics.addCustomChart(new SimplePie("update_check",
                    () -> getConfiguration().isUpdateCheckerEnabled() ? "ENABLED" : "DISABLED")
            );
            metrics.addCustomChart(new SimplePie("properties_configuration",
                    () -> getConfiguration().isPropertiesEnabled() ? "ENABLED" : "DISABLED")
            );
        }

        if (getConfiguration().isUpdateCheckerEnabled()) {
            log("开始检查更新...");
            getRegistry().checkUpdate(getDescription().getVersion());
        } else {
            log("已禁用检查更新，跳过。");
        }
        return true;
    }

    @Override
    protected void shutdown() {
        log("终止全部数据库连接...");
        for (String dbName : new HashSet<>(getRegistry().list().keySet())) {
            log("   正在关闭数据库 " + dbName + "...");
            SQLManager manager = getRegistry().get(dbName);
            getRegistry().shutdown(manager, activeQueries -> {
                log("   数据库 " + dbName + " 仍有有 " + activeQueries + " 条活动查询，强制关闭中...");
                if (manager.getDataSource() instanceof BeeDataSource) {
                    BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
                    dataSource.close();         //Close bee connection pool
                }
            });
        }
        getRegistry().getManagers().clear(); // release all managers
    }

    @Override
    public boolean isDebugging() {
        return getConfiguration().isDebugEnabled();
    }

    @Override
    @NotNull
    public MineSQLRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public @NotNull
    Map<String, DBConfiguration> readConfigurations() {
        return getConfiguration().getDBConfigurations();
    }

    @Override
    public @NotNull
    Map<String, Properties> readProperties() {
        if (!getConfiguration().isPropertiesEnabled()) return new HashMap<>();
        String propertiesFolder = getConfiguration().getPropertiesFolder();
        if (propertiesFolder == null || propertiesFolder.length() == 0) return new HashMap<>();

        File file = new File(getDataFolder(), propertiesFolder);
        if (!file.exists() || !file.isDirectory()) {
            try {
                JarResourceUtils.copyFolderFromJar(
                        "db-properties", file,
                        JarResourceUtils.CopyOption.COPY_IF_NOT_EXIST
                );
            } catch (Exception ex) {
                error("初始化properties示例文件失败：" + ex.getMessage());
            }
        }

        return DBPropertiesUtil.readFromFolder(file);
    }

    @Override
    public void outputInfo() {
        Optional.ofNullable(JarResourceUtils.readResource(this.getResource("PLUGIN_INFO"))).ifPresent(this::log);
    }

    public static MineSQLBukkit getInstance() {
        return MineSQLBukkit.instance;
    }

    protected BukkitConfiguration getConfiguration() {
        return configuration;
    }

    protected PaperCommandManager getCommandManager() {
        return commandManager;
    }


}
