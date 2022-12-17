package cc.carm.plugin.minesql.api.conf.drivers;

import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.conf.SQLDriverConfig;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class H2MemConfig extends SQLDriverConfig {

    protected final @Nullable String database;

    public H2MemConfig(@Nullable String database) {
        super(SQLDriverType.H2_MEM);
        this.database = database;
    }

    public @Nullable String getDatabase() {
        return database;
    }

    @Override
    public SQLSourceConfig createSource() {
        return SQLSourceConfig.create(getType().getDriverClass(), buildJDBC(), getType().getInitializer());
    }

    protected String buildJDBC() {
        return getType().getJdbcPrefix() + Optional.ofNullable(getDatabase()).orElse("");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<>();

        values.put("type", getType().name());
        if (getDatabase() != null) values.put("database", getDatabase());

        return values;
    }

}
