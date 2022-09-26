package cc.carm.plugin.minesql.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DBConfiguration {

    public static DBConfiguration create(@NotNull SQLDriverType sourceType, @NotNull String url) {
        return new DBConfiguration(sourceType.getDriverClass(), sourceType.getUrlPrefix(), url, sourceType.getInitializeSQLs());
    }

    private final @NotNull String driverClassName;
    private final @NotNull String urlPrefix;
    private final @NotNull List<String> initializeSQLs;

    private @NotNull String url;

    private @Nullable String username;
    private @Nullable String password;

    private @Nullable String connectionInitSql;

    private @Nullable String poolName;
    private @Nullable Integer maxPoolSize;
    private @Nullable Integer maxActive;

    private @Nullable Integer maxHoldTime;

    private @Nullable Long idleTimeout;

    private @Nullable Long maxWaitTime;


    private @Nullable String schema;
    private @Nullable Boolean autoCommit;
    private @Nullable Boolean readOnly;

    private @Nullable String validationSQL;
    private @Nullable Integer validationTimeout;
    private @Nullable Long validationInterval;


    private DBConfiguration(@NotNull String driverClass, @NotNull String urlPrefix,
                            @NotNull String url, @NotNull String[] initializeSQLs) {
        this.driverClassName = driverClass;
        this.urlPrefix = urlPrefix;
        this.url = url;
        this.initializeSQLs = Arrays.asList(initializeSQLs);
    }

    public @NotNull String getDriverClassName() {
        return driverClassName;
    }

    public @NotNull String getUrlPrefix() {
        return urlPrefix;
    }

    public @NotNull List<String> getInitializeSQLs() {
        return initializeSQLs;
    }

    public DBConfiguration setInitializeSQLs(@Nullable String[] initializeSQLs) {
        this.initializeSQLs.clear();
        this.initializeSQLs.addAll(Arrays.asList(initializeSQLs));
        return this;
    }

    public DBConfiguration addInitializeSQL(@NotNull String initializeSQL) {
        this.initializeSQLs.add(initializeSQL);
        return this;
    }

    public DBConfiguration clearExtraSettings() {
        this.initializeSQLs.clear();
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

    public @Nullable String getValidationSQL() {
        return validationSQL;
    }

    public DBConfiguration setValidationSQL(String validationSQL) {
        this.validationSQL = validationSQL;
        return this;
    }

    public @Nullable Long getValidationInterval() {
        return validationInterval;
    }

    public DBConfiguration setValidationInterval(@Nullable Long validationInterval) {
        this.validationInterval = validationInterval;
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

    public @Nullable Integer getMaxHoldTime() {
        return maxHoldTime;
    }

    public DBConfiguration setMaxHoldTime(@Nullable Integer maxHoldTime) {
        this.maxHoldTime = maxHoldTime;
        return this;
    }

    public @Nullable Integer getValidationTimeout() {
        return validationTimeout;
    }

    public DBConfiguration setValidationTimeout(Integer validationTimeout) {
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

    public @Nullable Integer getMaxActive() {
        return maxActive;
    }

    public DBConfiguration setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public @Nullable Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public DBConfiguration setMaxPoolSize(@Nullable Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public @Nullable Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public DBConfiguration setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        return this;
    }

}
