package cc.carm.plugin.minesql.api.source;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.function.SQLHandler;
import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.conf.drivers.H2MemConfig;
import cc.carm.plugin.minesql.api.conf.impl.FileBasedConfig;
import cc.carm.plugin.minesql.api.conf.impl.RemoteAuthConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class SQLSourceConfig {

    public static SQLSourceConfig createMySQL(@NotNull String host, int port, @NotNull String database,
                                              @NotNull String username, @Nullable String password,
                                              @Nullable String extraSettings) {
        return new RemoteAuthConfig(SQLDriverType.MYSQL, host, port, database, username, password, extraSettings).createSource();
    }

    public static SQLSourceConfig createMariaDB(@NotNull String host, int port, @NotNull String database,
                                                @NotNull String username, @Nullable String password,
                                                @Nullable String extraSettings) {
        return new RemoteAuthConfig(SQLDriverType.MARIADB, host, port, database, username, password, extraSettings).createSource();
    }

    public static SQLSourceConfig createH2File(@NotNull File file) {
        return create(SQLDriverType.H2_FILE, file.getAbsolutePath());
    }

    public static SQLSourceConfig createH2File(@NotNull String filePath) {
        return new FileBasedConfig(SQLDriverType.H2_FILE, filePath).createSource();
    }

    public static SQLSourceConfig createH2Mem(@Nullable String databaseName) {
        return new H2MemConfig(databaseName).createSource();
    }

    public static SQLSourceConfig create(@NotNull SQLDriverType sourceType, @NotNull String url) {
        return create(sourceType.getDriverClass(), sourceType.getJdbcPrefix() + url, sourceType.getInitializer());
    }

    public static SQLSourceConfig create(@NotNull String driverClass, @NotNull String jdbcURL) {
        return create(driverClass, jdbcURL, null);
    }

    public static SQLSourceConfig create(@NotNull String driverClassName, @NotNull String jdbcURL,
                                         @Nullable SQLHandler<SQLManager> initializer) {
        return new SQLSourceConfig(driverClassName, jdbcURL, initializer, null);
    }

    public static SQLSourceConfig create(@NotNull String driverClassName, @NotNull String jdbcURL,
                                         @Nullable SQLHandler<SQLManager> initializer, @Nullable SQLPoolSettings settings) {
        return new SQLSourceConfig(driverClassName, jdbcURL, initializer, settings);
    }

    private @NotNull String driverClassName;
    private @NotNull String jdbcURL;
    private @Nullable String username;
    private @Nullable String password;

    private @Nullable SQLHandler<SQLManager> initializer;
    private @NotNull SQLPoolSettings settings;

    public SQLSourceConfig(@NotNull String driverClassName, @NotNull String jdbcURL,
                           @Nullable SQLHandler<SQLManager> initializer, @Nullable SQLPoolSettings settings) {
        this.driverClassName = driverClassName;
        this.jdbcURL = jdbcURL;
        this.initializer = initializer;
        this.settings = Optional.ofNullable(settings).orElse(new SQLPoolSettings());
    }

    public @NotNull String getDriverClassName() {
        return driverClassName;
    }

    public SQLSourceConfig setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public @NotNull String getJdbcURL() {
        return jdbcURL;
    }

    public SQLSourceConfig setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
        return this;
    }

    public @Nullable String getUsername() {
        return username;
    }

    public SQLSourceConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public SQLSourceConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public @Nullable SQLHandler<SQLManager> getInitializer() {
        return initializer;
    }

    public SQLSourceConfig setInitializer(SQLHandler<SQLManager> initializer) {
        this.initializer = initializer;
        return this;
    }

    public @NotNull SQLPoolSettings getSettings() {
        return settings;
    }

    public SQLSourceConfig setSettings(SQLPoolSettings settings) {
        this.settings = settings;
        return this;
    }

    public SQLSourceConfig editSettings(Consumer<SQLPoolSettings> consumer) {
        consumer.accept(settings);
        return this;
    }

}
