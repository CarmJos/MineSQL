package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.minesql.api.SQLRegistry;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MineSQLRegistry implements SQLRegistry {

    private static MineSQLRegistry instance;

    protected ExecutorService executorPool;
    protected MineSQLCore core;
    private final HashMap<String, SQLManagerImpl> managers = new HashMap<>();

    protected MineSQLRegistry(@NotNull MineSQLCore core) {
        this.core = core;
        MineSQLRegistry.instance = this;
        this.executorPool = Executors.newFixedThreadPool(2, (r) -> {
            Thread thread = new Thread(r, "EasySQLRegistry");
            thread.setDaemon(true);
            return thread;
        });

        Map<String, Properties> dbProperties = core.readProperties();
        Map<String, SQLSourceConfig> dbConfigurations = core.readConfigurations();

        if (dbProperties.isEmpty() && dbConfigurations.isEmpty()) {
            core.getLogger().warning("未检测到任何数据库配置，将不会预创建任何SQLManager。");
            return;
        }

        dbProperties.forEach((id, properties) -> {
            try {
                SQLManagerImpl sqlManager = create(id, properties);
                this.managers.put(id, sqlManager);
            } catch (Exception exception) {
                core.getLogger().warning("初始化SQLManager(#" + id + ") 出错，请检查配置文件.");
                exception.printStackTrace();
            }
        });

        dbConfigurations.forEach((id, configuration) -> {
            try {
                SQLManagerImpl sqlManager = create(id, configuration);
                this.managers.put(id, sqlManager);
            } catch (Exception exception) {
                core.getLogger().warning("初始化SQLManager(#" + id + ") 出错，请检查配置文件.");
                exception.printStackTrace();
            }
        });

    }

    public void shutdownAll() {
        this.managers.forEach((k, manager) -> {
            getCore().getLogger().info("   正在关闭数据库 " + k + "...");
            shutdown(manager, activeQueries -> {
                getCore().getLogger().info("   数据库 " + k + " 仍有有 " + activeQueries + " 条活动查询");
                if (manager.getDataSource() instanceof BeeDataSource
                        && this.core.getConfig().SETTINGS.FORCE_CLOSE.getNotNull()) {
                    getCore().getLogger().info("   将强制关闭全部活跃链接...");
                    BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
                    dataSource.close();         //Close bee connection pool
                }
            });
        });
        this.managers.clear();
    }

    protected HashMap<String, SQLManagerImpl> getManagers() {
        return managers;
    }

    public ExecutorService getExecutor() {
        return executorPool;
    }

    public static MineSQLRegistry getInstance() {
        return instance;
    }

    public MineSQLCore getCore() {
        return core;
    }

    @Override
    public @NotNull SQLManagerImpl get(@Nullable String id) throws NullPointerException {
        return Objects.requireNonNull(this.managers.get(id), "并不存在ID为 #" + id + " 的SQLManager.");
    }

    @Override
    public @NotNull Optional<@Nullable SQLManagerImpl> getOptional(@Nullable String id) {
        return Optional.of(this.managers.get(id));
    }

    @Override
    @Unmodifiable
    public @NotNull Map<String, SQLManagerImpl> list() {
        return ImmutableMap.copyOf(this.managers);
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull SQLSourceConfig conf) {
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName(conf.getDriverClassName());
        config.setJdbcUrl(conf.getJdbcURL());
        Optional.ofNullable(conf.getUsername()).ifPresent(config::setUsername);
        Optional.ofNullable(conf.getPassword()).ifPresent(config::setPassword);

        Optional.ofNullable(conf.getSettings().getPoolName()).ifPresent(config::setPoolName);
        Optional.ofNullable(conf.getSettings().getMaxPoolSize()).ifPresent(config::setMaxActive);
        Optional.ofNullable(conf.getSettings().getMaxActive()).ifPresent(config::setMaxActive);

        Optional.ofNullable(conf.getSettings().getIdleTimeout()).ifPresent(config::setIdleTimeout);
        Optional.ofNullable(conf.getSettings().getMaxWaitTime()).ifPresent(config::setMaxWait);
        Optional.ofNullable(conf.getSettings().getMaxHoldTime()).ifPresent(config::setHoldTimeout);

        Optional.ofNullable(conf.getSettings().getAutoCommit()).ifPresent(config::setDefaultAutoCommit);
        Optional.ofNullable(conf.getSettings().getReadOnly()).ifPresent(config::setDefaultReadOnly);
        Optional.ofNullable(conf.getSettings().getSchema()).ifPresent(config::setDefaultSchema);

        Optional.ofNullable(conf.getSettings().getValidationSQL()).ifPresent(config::setValidTestSql);
        Optional.ofNullable(conf.getSettings().getValidationTimeout()).ifPresent(config::setValidTestTimeout);
        Optional.ofNullable(conf.getSettings().getValidationInterval()).ifPresent(config::setValidAssumeTime);

        return create(name, config);
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull Properties properties) {
        return create(name, new BeeDataSourceConfig(properties));
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull DataSource source) {
        return new SQLManagerImpl(source, name);
    }

    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull BeeDataSourceConfig configuration) {
        return create(name, (DataSource) new BeeDataSource(configuration));
    }

    @Override
    public void shutdown(SQLManager manager, @Nullable Consumer<Map<UUID, SQLQuery>> activeQueries) {
        if (activeQueries != null) activeQueries.accept(manager.getActiveQuery());

        if (manager.getDataSource() instanceof BeeDataSource) {
            BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
            dataSource.close();         //Close bee connection pool
        }
    }

}
