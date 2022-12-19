package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.plugin.minesql.api.SQLRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MineSQLRegistry implements SQLRegistry {

    private final HashMap<String, SQLManager> managers = new HashMap<>();

    protected HashMap<String, SQLManager> getManagers() {
        return managers;
    }

    @Override
    public @NotNull Optional<@Nullable SQLManager> getOptional(@Nullable String id) {
        return Optional.of(this.managers.get(id));
    }

    @Override
    @Unmodifiable
    public @NotNull Map<String, SQLManager> list() {
        return Collections.unmodifiableMap(this.managers);
    }

    @Override
    public void register(@NotNull String name, @NotNull SQLManager sqlManager) throws IllegalStateException {
        if (this.managers.containsKey(name)) {
            throw new IllegalStateException("已存在ID为 " + name + " 的SQLManager实例。");
        }
        this.managers.put(name, sqlManager);
    }

    @Override
    public @NotNull SQLManager unregister(@NotNull String name) throws NullPointerException {
        SQLManager manager = this.managers.remove(name);
        if (manager == null) throw new NullPointerException("不存在ID为 " + name + " 的SQLManager实例。");
        return manager;
    }

}
