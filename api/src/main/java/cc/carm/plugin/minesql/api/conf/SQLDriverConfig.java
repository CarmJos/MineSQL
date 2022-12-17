package cc.carm.plugin.minesql.api.conf;

import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class SQLDriverConfig {

    protected final @NotNull SQLDriverType type;

    public SQLDriverConfig(@NotNull SQLDriverType type) {
        this.type = type;
    }

    public abstract SQLSourceConfig createSource();

    public @NotNull SQLDriverType getType() {
        return type;
    }

    public abstract @NotNull Map<String, Object> serialize();

}
