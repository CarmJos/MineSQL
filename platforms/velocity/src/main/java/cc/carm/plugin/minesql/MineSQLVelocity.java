package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.conf.PluginConfiguration;
import co.aikar.commands.CommandManager;
import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;


@Plugin(id = "minesql", name = "MineSQL (EasySQL-Plugin)",
        version = References.VERSION,
        description = "EasySQL Plugin For Velocity",
        url = "https://github.com/CarmJos/MineSQL",
        authors = {"CarmJos", "GhostChu"}
)
public class MineSQLVelocity implements MineSQLPlatform {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataFolder;

    private final Metrics.Factory metricsFactory;

    protected MineSQLCore core;
    protected VelocityCommandManager commandManager;

    @Inject
    public MineSQLVelocity(ProxyServer server, Logger logger,
                           @DataDirectory Path dataDirectory,
                           Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();
        this.metricsFactory = metricsFactory;

        getLogger().info("加载基础核心...");
        this.core = new MineSQLCore(this);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onInitialize(ProxyInitializeEvent event) {


        getLogger().info("初始化指令管理器...");
        this.commandManager = new VelocityCommandManager(server, this);

        getLogger().info("注册相关指令...");
        this.core.initializeCommands(getCommandManager());

        if (getConfiguration().METRICS.getNotNull()) {
            getLogger().info("启用统计数据...");
            Metrics metrics = this.metricsFactory.make(this, 14078);
            metrics.addCustomChart(new SimplePie("update_check",
                    () -> getConfiguration().UPDATE_CHECKER.getNotNull() ? "ENABLED" : "DISABLED")
            );
            metrics.addCustomChart(new SimplePie("properties_configuration",
                    () -> getConfiguration().PROPERTIES.ENABLE.getNotNull() ? "ENABLED" : "DISABLED")
            );
        }

        if (getConfiguration().UPDATE_CHECKER.getNotNull()) {
            getLogger().info("开始检查更新，可能需要一小段时间...");
            getLogger().info("   如不希望检查更新，可在配置文件中关闭。");
            server.getScheduler().buildTask(this, () -> this.core.checkUpdate(getVersion())).schedule();
        } else {
            getLogger().info("已禁用检查更新，跳过。");
        }

    }

    @Subscribe(order = PostOrder.LAST)
    public void onShutdown(ProxyShutdownEvent event) {
        getLogger().info("终止全部数据库连接...");
        this.core.shutdownAll();
    }

    public ProxyServer getServer() {
        return server;
    }

    public @NotNull Logger getLogger() {
        return logger;
    }

    public String getVersion() {
        return this.server.getPluginManager().getPlugin("minesql")
                .map(PluginContainer::getDescription)
                .flatMap(PluginDescription::getVersion).orElse("1.0.0");
    }

    @Override
    public @NotNull File getPluginFolder() {
        return this.dataFolder;
    }

    @Override
    public @NotNull CommandManager<?, ?, ?, ?, ?, ?> getCommandManager() {
        return commandManager;
    }

    public @NotNull PluginConfiguration getConfiguration() {
        return this.core.getConfig();
    }
}
