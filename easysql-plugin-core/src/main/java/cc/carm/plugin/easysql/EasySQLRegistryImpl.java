package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.EasySQLRegistry;
import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

public class EasySQLRegistryImpl implements EasySQLRegistry {

    private final HashMap<String, SQLManager> sqlManagerRegistry = new HashMap<>();

    public EasySQLRegistryImpl(@NotNull EasySQLPluginPlatform platform) {
        Map<String, DBConfiguration> configurations = platform.readConfigurations();

        if (configurations.isEmpty()) {
            platform.getLogger().warning("未检测到任何数据库配置，将不会创建任何SQLManager。");
            return;
        }

        configurations.forEach((id, configuration) -> {
            try {
                SQLManager sqlManager = create(id, configuration);
                this.sqlManagerRegistry.put(id, sqlManager);
            } catch (Exception exception) {
                platform.getLogger().warning("初始化SQLManager(#" + id + ") 出错，请检查配置文件.");
                exception.printStackTrace();
            }
        });

    }

    @Override
    public @NotNull SQLManager get(@Nullable String id) throws NullPointerException {
        if (!this.sqlManagerRegistry.containsKey(id)) {
            throw new NullPointerException("并不存在ID为 #" + id + " 的SQLManager.");
        }
        return this.sqlManagerRegistry.get(id);
    }

    @Override
    public @NotNull Optional<@Nullable SQLManager> getOptional(@Nullable String name) {
        try {
            return Optional.of(get(name));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    @Unmodifiable
    public @NotNull Map<String, SQLManager> list() {
        return ImmutableMap.copyOf(this.sqlManagerRegistry);
    }


    @Override
    public @NotNull SQLManager create(@Nullable String name, @NotNull DBConfiguration configuration) {
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
    public @NotNull SQLManager create(@Nullable String name, @NotNull Properties properties) {
        return create(name, new BeeDataSourceConfig(properties));
    }

    @Override
    public @NotNull SQLManager create(@Nullable String name, @NotNull String propertyFileName) {
        return create(name, new BeeDataSourceConfig(propertyFileName));
    }

    public @NotNull SQLManager create(@Nullable String name, @NotNull BeeDataSourceConfig configuration) {
        return new SQLManagerImpl(new BeeDataSource(configuration), name);
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
