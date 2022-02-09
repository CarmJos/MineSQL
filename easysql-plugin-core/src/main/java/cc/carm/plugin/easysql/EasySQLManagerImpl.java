package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.EasySQLManager;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class EasySQLManagerImpl implements EasySQLManager {

    private final HashMap<String, SQLManager> sqlManagerRegistry = new HashMap<>();

    public EasySQLManagerImpl(@NotNull EasySQLPluginPlatform platform) {
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
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(configuration.getDriverClassName());
        config.setJdbcUrl(configuration.getUrlPrefix() + configuration.getUrl());

        Optional.ofNullable(configuration.getPoolName()).ifPresent(config::setPoolName);
        Optional.ofNullable(configuration.getUsername()).ifPresent(config::setUsername);
        Optional.ofNullable(configuration.getPassword()).ifPresent(config::setPassword);
        Optional.ofNullable(configuration.getMaxPoolSize()).ifPresent(config::setMaximumPoolSize);
        Optional.ofNullable(configuration.getMinIdle()).ifPresent(config::setMinimumIdle);
        Optional.ofNullable(configuration.getIdleTimeout()).ifPresent(config::setIdleTimeout);
        Optional.ofNullable(configuration.getKeepaliveTime()).ifPresent(config::setKeepaliveTime);
        Optional.ofNullable(configuration.getConnectionTimeout()).ifPresent(config::setConnectionTimeout);
        Optional.ofNullable(configuration.getValidationTimeout()).ifPresent(config::setValidationTimeout);
        Optional.ofNullable(configuration.getMaxLifetime()).ifPresent(config::setMaxLifetime);
        Optional.ofNullable(configuration.getLeakDetectionThreshold()).ifPresent(config::setLeakDetectionThreshold);
        Optional.ofNullable(configuration.getConnectionTestQuery()).ifPresent(config::setConnectionTestQuery);
        Optional.ofNullable(configuration.getConnectionInitSql()).ifPresent(config::setConnectionInitSql);
        Optional.ofNullable(configuration.getAutoCommit()).ifPresent(config::setAutoCommit);
        Optional.ofNullable(configuration.getReadOnly()).ifPresent(config::setReadOnly);
        Optional.ofNullable(configuration.getSchema()).ifPresent(config::setSchema);

        return create(name, config);
    }

    @Override
    public @NotNull SQLManager create(@Nullable String name, @NotNull Properties properties) {
        return create(name, new HikariConfig(properties));
    }

    @Override
    public @NotNull SQLManager create(@Nullable String name, @NotNull String propertyFileName) {
        return create(name, new HikariConfig(propertyFileName));
    }

    public @NotNull SQLManager create(@Nullable String name, @NotNull HikariConfig configuration) {
        return new SQLManagerImpl(new HikariDataSource(configuration), name);
    }

    @Override
    public Map<UUID, SQLQuery> shutdown(SQLManager manager, boolean forceClose) {
        Map<UUID, SQLQuery> queries = manager.getActiveQuery();
        if (forceClose) manager.getActiveQuery().values().forEach(SQLQuery::close);

        if (manager.getDataSource() instanceof HikariDataSource) {
            //Close hikari pool
            ((HikariDataSource) manager.getDataSource()).close();
        }

        return queries;
    }
}
