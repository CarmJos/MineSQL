package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.lib.githubreleases4j.GithubReleases4J;
import cc.carm.plugin.minesql.api.DBConfiguration;
import cc.carm.plugin.minesql.api.SQLRegistry;
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
import java.util.logging.Logger;

public class MineSQLRegistry implements SQLRegistry {
    public static final String REPO_OWNER = "CarmJos";
    public static final String REPO_NAME = "EasySQL-Plugin";

    private static MineSQLRegistry instance;

    protected ExecutorService executorPool;
    protected MineSQLPlatform platform;
    private final HashMap<String, SQLManagerImpl> sqlManagerRegistry = new HashMap<>();

    protected MineSQLRegistry(@NotNull MineSQLPlatform platform) {
        this.platform = platform;
        MineSQLRegistry.instance = this;
        this.executorPool = Executors.newFixedThreadPool(2, (r) -> {
            Thread thread = new Thread(r, "EasySQLRegistry");
            thread.setDaemon(true);
            return thread;
        });
        Map<String, Properties> dbProperties = platform.readProperties();
        Map<String, DBConfiguration> dbConfigurations = platform.readConfigurations();

        if (dbProperties.isEmpty() && dbConfigurations.isEmpty()) {
            platform.getLogger().warning("未检测到任何数据库配置，将不会创建任何SQLManager。");
            return;
        }

        dbProperties.forEach((id, properties) -> {
            try {
                SQLManagerImpl sqlManager = create(id, properties);
                this.sqlManagerRegistry.put(id, sqlManager);
            } catch (Exception exception) {
                platform.getLogger().warning("初始化SQLManager(#" + id + ") 出错，请检查配置文件.");
                exception.printStackTrace();
            }
        });

        dbConfigurations.forEach((id, configuration) -> {
            try {
                SQLManagerImpl sqlManager = create(id, configuration);
                this.sqlManagerRegistry.put(id, sqlManager);
            } catch (Exception exception) {
                platform.getLogger().warning("初始化SQLManager(#" + id + ") 出错，请检查配置文件.");
                exception.printStackTrace();
            }
        });

    }

    @Override
    public @NotNull SQLManagerImpl get(@Nullable String id) throws NullPointerException {
        return Objects.requireNonNull(this.sqlManagerRegistry.get(id), "并不存在ID为 #" + id + " 的SQLManager.");
    }

    @Override
    public @NotNull Optional<@Nullable SQLManagerImpl> getOptional(@Nullable String id) {
        return Optional.of(this.sqlManagerRegistry.get(id));
    }

    @Override
    @Unmodifiable
    public @NotNull Map<String, SQLManagerImpl> list() {
        return ImmutableMap.copyOf(this.sqlManagerRegistry);
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull DBConfiguration configuration) throws Exception {
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName(configuration.getDriverClassName());
        config.setJdbcUrl(configuration.getUrlPrefix() + configuration.getUrl());
        Optional.ofNullable(configuration.getUsername()).ifPresent(config::setUsername);
        Optional.ofNullable(configuration.getPassword()).ifPresent(config::setPassword);

        Optional.ofNullable(configuration.getPoolName()).ifPresent(config::setPoolName);
        Optional.ofNullable(configuration.getMaxPoolSize()).ifPresent(config::setMaxActive);
        Optional.ofNullable(configuration.getMaxActive()).ifPresent(config::setMaxActive);

        Optional.ofNullable(configuration.getIdleTimeout()).ifPresent(config::setIdleTimeout);
        Optional.ofNullable(configuration.getMaxWaitTime()).ifPresent(config::setMaxWait);
        Optional.ofNullable(configuration.getMaxHoldTime()).ifPresent(config::setHoldTimeout);

        Optional.ofNullable(configuration.getAutoCommit()).ifPresent(config::setDefaultAutoCommit);
        Optional.ofNullable(configuration.getReadOnly()).ifPresent(config::setDefaultReadOnly);
        Optional.ofNullable(configuration.getSchema()).ifPresent(config::setDefaultSchema);

        Optional.ofNullable(configuration.getValidationSQL()).ifPresent(config::setValidTestSql);
        Optional.ofNullable(configuration.getValidationTimeout()).ifPresent(config::setValidTestTimeout);
        Optional.ofNullable(configuration.getValidationInterval()).ifPresent(config::setValidAssumeTime);

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

    protected HashMap<String, SQLManagerImpl> getManagers() {
        return sqlManagerRegistry;
    }

    public ExecutorService getExecutor() {
        return executorPool;
    }

    public static MineSQLRegistry getInstance() {
        return instance;
    }

    public MineSQLPlatform getPlatform() {
        return platform;
    }

    public void checkUpdate(String currentVersion) {
        Logger logger = getInstance().getPlatform().getLogger();
        getExecutor().execute(() -> {
            Integer behindVersions = GithubReleases4J.getVersionBehind(REPO_OWNER, REPO_NAME, currentVersion);
            String downloadURL = GithubReleases4J.getReleasesURL(REPO_OWNER, REPO_NAME);
            if (behindVersions == null) {
                logger.severe("检查更新失败，请您定期查看插件是否更新，避免安全问题。");
                logger.severe("下载地址 " + downloadURL);
            } else if (behindVersions < 0) {
                logger.severe("检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
                logger.severe("最新版下载地址 " + downloadURL);
            } else if (behindVersions > 0) {
                logger.warning("发现新版本! 目前已落后 " + behindVersions + " 个版本。");
                logger.warning("最新版下载地址 " + downloadURL);
            } else {
                logger.info("检查完成，当前已是最新版本。");
            }
        });
    }
}
