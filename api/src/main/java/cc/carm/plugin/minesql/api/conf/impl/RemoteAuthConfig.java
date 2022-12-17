package cc.carm.plugin.minesql.api.conf.impl;

import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.conf.SQLDriverConfig;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RemoteAuthConfig extends SQLDriverConfig {

    protected final @NotNull String host;
    protected final int port;
    protected final @NotNull String database;
    protected final @Nullable String username;
    protected final @Nullable String password;

    protected final @Nullable String extraSettings;

    public RemoteAuthConfig(@NotNull SQLDriverType type,
                            @NotNull String host, int port, @NotNull String database,
                            @Nullable String username, @Nullable String password,
                            @Nullable String extraSettings) {
        super(type);
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.extraSettings = extraSettings;
    }

    @Override
    public SQLSourceConfig createSource() {
        return SQLSourceConfig.create(
                getType().getDriverClass(), buildJDBC(), getType().getInitializer()
        ).setUsername(getUsername()).setPassword(getPassword());
    }

    public @NotNull String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public @NotNull String getDatabase() {
        return database;
    }

    public @Nullable String getUsername() {
        return username;
    }


    public @Nullable String getPassword() {
        return password;
    }

    public @Nullable String getExtraSettings() {
        return extraSettings;
    }

    protected String buildJDBC() {
        return String.format("%s%s:%s/%s%s", getType().getJdbcPrefix(), getHost(), getPort(), getDatabase(), getExtraSettings());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<>();

        values.put("type", getType().name());

        values.put("host", getHost());
        values.put("port", getPort());
        values.put("database", getDatabase());
        if (getUsername() != null) values.put("username", username);
        if (getPassword() != null) values.put("password", password);
        if (getExtraSettings() != null) values.put("extra", extraSettings);

        return values;
    }

}
