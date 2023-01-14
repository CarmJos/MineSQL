package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.conf.PluginConfiguration;
import co.aikar.commands.CommandManager;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.bstats.charts.SimplePie;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;

/**
 * 2022/6/20<br>
 * MineSQL<br>
 *
 * @author huanmeng_qwq
 */
@Plugin("minesql")
public class MineSQLSponge implements MineSQLPlatform {
    private static MineSQLSponge instance;

    @ConfigDir(sharedRoot = false)
    private Path configDirectory;
    @Inject
    private org.apache.logging.log4j.Logger logger;

    private final PluginContainer pluginContainer;
    private final Metrics metrics;

    protected MineSQLCore core;

    @Inject
    public MineSQLSponge(Metrics.Factory factory, PluginContainer pluginContainer) {
        this.metrics = factory.make(14075);
        instance = this;
        this.core = new MineSQLCore(this);
        this.pluginContainer = pluginContainer;
    }

    @Listener
    public void starting(StartingEngineEvent<Server> e) {
        enable();
    }

    @Listener
    public void disable(StoppingEngineEvent<Server> e) {
        logger.info("终止全部数据库连接...");
        this.core.shutdownAll();
    }


    public void enable() {
        if (getConfiguration().METRICS.getNotNull()) {
            getLog().info("启用统计数据...");
            metrics.addCustomChart(new SimplePie("update_check",
                    () -> getConfiguration().UPDATE_CHECKER.getNotNull() ? "ENABLED" : "DISABLED")
            );
            metrics.addCustomChart(new SimplePie("properties_configuration",
                    () -> getConfiguration().PROPERTIES.ENABLE.getNotNull() ? "ENABLED" : "DISABLED")
            );
        }

        if (getConfiguration().PROPERTIES.ENABLE.getNotNull()) {
            logger.info("开始检查更新，可能需要一小段时间...");
            logger.info("   如不希望检查更新，可在配置文件中关闭。");
            Sponge.asyncScheduler().executor(pluginContainer)
                    .execute(() -> this.core.checkUpdate(pluginContainer.metadata().version().getQualifier()))
            ;
        } else {
            logger.info("已禁用检查更新，跳过。");
        }
    }

    @Override
    public @NotNull File getPluginFolder() {
        return configDirectory.toFile();
    }

    public static @NotNull MineSQLSponge getInstance() {
        return instance;
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


    private Logger getLog() {
        return logger;
    }
}
