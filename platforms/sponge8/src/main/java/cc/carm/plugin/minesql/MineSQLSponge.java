package cc.carm.plugin.minesql;

import cc.carm.lib.easyplugin.utils.ColorParser;
import cc.carm.lib.easyplugin.utils.JarResourceUtils;
import cc.carm.plugin.minesql.conf.PluginConfiguration;
import co.aikar.commands.CommandManager;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bstats.charts.SimplePie;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author huanmeng_qwq, CarmJos
 */
@Plugin("minesql")
public class MineSQLSponge implements MineSQLPlatform {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;

    @Inject
    private org.apache.logging.log4j.Logger logger;

    private final PluginContainer pluginContainer;
    private final Metrics.Factory metricsFactory;

    protected final MineSQLCore core;
//    protected SpongeCommandManager commandManager;

    @Inject
    public MineSQLSponge(Metrics.Factory factory, PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
        this.metricsFactory = factory;

        getLogger().info("加载基础核心...");
        this.core = new MineSQLCore(this);
    }

    @Listener(order = Order.PRE)
    public void starting(StartingEngineEvent<Server> e) {
        outputInfo();
//        getLogger().info("初始化指令管理器...");
//        this.commandManager = new SpongeCommandManager(pluginContainer);
//
//        getLogger().info("注册相关指令...");
//        this.core.initializeCommands(getCommandManager());

        if (getConfiguration().METRICS.getNotNull()) {
            getLogger().info("启用统计数据...");
            Metrics metrics = this.metricsFactory.make(14078);
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
            Sponge.asyncScheduler().executor(pluginContainer)
                    .execute(() -> this.core.checkUpdate(getVersion()));
        } else {
            getLogger().info("已禁用检查更新，跳过。");
        }
    }

    @Listener
    public void disable(StoppingEngineEvent<Server> e) {
        outputInfo();
        logger.info("终止全部数据库连接...");
        this.core.shutdownAll();
    }

    @Override
    public @NotNull File getPluginFolder() {
        return configDirectory.toFile();
    }

    public @NotNull PluginConfiguration getConfiguration() {
        return this.core.getConfig();
    }

    public @NotNull java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getLogger("MineSQL");
    }

    //fixme acf-sponge是基于sponge5编写的 无法使用
    @Override
    public @Nullable CommandManager<?, ?, ?, ?, ?, ?> getCommandManager() {
        return null;
    }

    public String getVersion() {
        return pluginContainer.metadata().version().toString();
    }

    public void outputInfo() {
        Optional.ofNullable(JarResourceUtils.readResource(this.getClass().getResourceAsStream("PLUGIN_INFO")))
                .map(v -> ColorParser.parse(Arrays.asList(v)))
                .ifPresent(list -> list.forEach(s -> Sponge.server().sendMessage(Component.text(s))));
    }

}
