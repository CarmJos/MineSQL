package cc.carm.plugin.minesql.api.source;

import org.jetbrains.annotations.Nullable;

public class SQLPoolSettings {

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

    public @Nullable String getValidationSQL() {
        return validationSQL;
    }

    public SQLPoolSettings setValidationSQL(String validationSQL) {
        this.validationSQL = validationSQL;
        return this;
    }

    public @Nullable Long getValidationInterval() {
        return validationInterval;
    }

    public SQLPoolSettings setValidationInterval(@Nullable Long validationInterval) {
        this.validationInterval = validationInterval;
        return this;
    }

    public @Nullable String getPoolName() {
        return poolName;
    }

    public SQLPoolSettings setPoolName(String poolName) {
        this.poolName = poolName;
        return this;
    }

    public @Nullable String getSchema() {
        return schema;
    }

    public SQLPoolSettings setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public @Nullable Boolean getAutoCommit() {
        return autoCommit;
    }

    public SQLPoolSettings setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public @Nullable Boolean getReadOnly() {
        return readOnly;
    }

    public SQLPoolSettings setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public @Nullable Integer getMaxHoldTime() {
        return maxHoldTime;
    }

    public SQLPoolSettings setMaxHoldTime(@Nullable Integer maxHoldTime) {
        this.maxHoldTime = maxHoldTime;
        return this;
    }

    public @Nullable Integer getValidationTimeout() {
        return validationTimeout;
    }

    public SQLPoolSettings setValidationTimeout(Integer validationTimeout) {
        this.validationTimeout = validationTimeout;
        return this;
    }

    public @Nullable Long getIdleTimeout() {
        return idleTimeout;
    }

    public SQLPoolSettings setIdleTimeout(Long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

    public @Nullable Integer getMaxActive() {
        return maxActive;
    }

    public SQLPoolSettings setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public @Nullable Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public SQLPoolSettings setMaxPoolSize(@Nullable Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public @Nullable Long getMaxWaitTime() {
        return maxWaitTime;
    }

    public SQLPoolSettings setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
        return this;
    }


}
