package cc.carm.plugin.easysql.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBConfiguration {

    public DBConfiguration create(@NotNull SQLSourceType sourceType, @NotNull String url) {
        return new DBConfiguration(sourceType.getDriverClass(), sourceType.getUrlPrefix(), url, sourceType.getDefaultSettings());
    }

    private final @NotNull String driverClassName;
    private final @NotNull String urlPrefix;
    private final @NotNull List<String> defaultSettings;
    private final @NotNull List<String> extraSettings;

    private @NotNull String url;

    private @Nullable String username;
    private @Nullable String password;

    private @Nullable String connectionInitSql;
    private @Nullable String connectionTestQuery;
    private @Nullable String poolName;
    private @Nullable String schema;
    private @Nullable Boolean autoCommit;
    private @Nullable Boolean readOnly;

    private @Nullable Long connectionTimeout;
    private @Nullable Long validationTimeout;
    private @Nullable Long idleTimeout;
    private @Nullable Long leakDetectionThreshold;
    private @Nullable Long maxLifetime;
    private @Nullable Long keepaliveTime;
    private @Nullable Integer maxPoolSize;
    private @Nullable Integer minIdle;

    private DBConfiguration(@NotNull String driverClass, @NotNull String urlPrefix,
                            @NotNull String url, @NotNull String[] defaultSettings) {
        this.driverClassName = driverClass;
        this.urlPrefix = urlPrefix;
        this.url = url;
        this.defaultSettings = Arrays.asList(defaultSettings);
        this.extraSettings = new ArrayList<>();
    }

    public @NotNull String getDriverClassName() {
        return driverClassName;
    }

    public @NotNull String getUrlPrefix() {
        return urlPrefix;
    }

    public @NotNull List<String> getDefaultSettings() {
        return defaultSettings;
    }

    public @NotNull List<String> getExtraSettings() {
        return extraSettings;
    }

    public DBConfiguration setDefaultSettings(@Nullable String[] defaultSettings) {
        this.defaultSettings.clear();
        this.defaultSettings.addAll(Arrays.asList(defaultSettings));
        return this;
    }


    public DBConfiguration clearDefaultSettings() {
        this.defaultSettings.clear();
        return this;
    }

    public DBConfiguration setExtraSettings(@Nullable String[] extraSettings) {
        this.defaultSettings.clear();
        this.extraSettings.addAll(Arrays.asList(extraSettings));
        return this;
    }

    public DBConfiguration addExtraSetting(@NotNull String extraSetting) {
        this.extraSettings.add(extraSetting);
        return this;
    }

    public DBConfiguration clearExtraSettings() {
        this.defaultSettings.clear();
        return this;
    }

    public @NotNull String getUrl() {
        return url;
    }

    public DBConfiguration setUrl(String url) {
        this.url = url;
        return this;
    }

    public @Nullable String getUsername() {
        return username;
    }

    public DBConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public @Nullable String getPassword() {
        return password;
    }

    public DBConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public @Nullable String getConnectionInitSql() {
        return connectionInitSql;
    }

    public DBConfiguration setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
        return this;
    }

    public @Nullable String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public DBConfiguration setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
        return this;
    }

    public @Nullable String getPoolName() {
        return poolName;
    }

    public DBConfiguration setPoolName(String poolName) {
        this.poolName = poolName;
        return this;
    }

    public @Nullable String getSchema() {
        return schema;
    }

    public DBConfiguration setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public @Nullable Boolean getAutoCommit() {
        return autoCommit;
    }

    public DBConfiguration setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public @Nullable Boolean getReadOnly() {
        return readOnly;
    }

    public DBConfiguration setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public @Nullable Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public DBConfiguration setConnectionTimeout(Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public @Nullable Long getValidationTimeout() {
        return validationTimeout;
    }

    public DBConfiguration setValidationTimeout(Long validationTimeout) {
        this.validationTimeout = validationTimeout;
        return this;
    }

    public @Nullable Long getIdleTimeout() {
        return idleTimeout;
    }

    public DBConfiguration setIdleTimeout(Long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public @Nullable Long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public DBConfiguration setLeakDetectionThreshold(Long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
        return this;
    }

    public @Nullable Long getMaxLifetime() {
        return maxLifetime;
    }

    public DBConfiguration setMaxLifetime(Long maxLifetime) {
        this.maxLifetime = maxLifetime;
        return this;
    }

    public @Nullable Long getKeepaliveTime() {
        return keepaliveTime;
    }

    public DBConfiguration setKeepaliveTime(Long keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
        return this;
    }

    public @Nullable Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public DBConfiguration setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public @Nullable Integer getMinIdle() {
        return minIdle;
    }

    public DBConfiguration setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
        return this;
    }
}
