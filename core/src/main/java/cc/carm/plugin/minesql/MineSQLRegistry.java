package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.minesql.api.SQLRegistry;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MineSQLRegistry implements SQLRegistry {

    private static MineSQLRegistry instance;

    protected ExecutorService executorPool;
    protected MineSQLCore core;
    private final HashMap<String, SQLManager> managers = new HashMap<>();

    protected MineSQLRegistry(@NotNull MineSQLCore core) {
        this.core = core;
        MineSQLRegistry.instance = this;
        this.executorPool = Executors.newFixedThreadPool(2, (r) -> {
            Thread thread = new Thread(r, "EasySQLRegistry");
            thread.setDaemon(true);
            return thread;
        });

        Map<String, Properties> dbProperties = core.readProperties();
        Map<String, SQLSourceConfig> dbConfigurations = core.readConfigurations();

        if (dbProperties.isEmpty() && dbConfigurations.isEmpty()) {
            core.getLogger().warning("未检测到任何数据库配置，将不会预创建任何SQLManager。");
            return;
        }

        dbProperties.forEach((id, properties) -> {
            try {
                core.getLogger().info("正在初始化数据库 #" + id + " ...");
                SQLManagerImpl sqlManager = this.core.create(id, properties);
                this.managers.put(id, sqlManager);
                core.getLogger().info("完成成初始化数据库 #" + id + " 。");
            } catch (Exception ex) {
                core.getLogger().severe("初始化SQLManager(#" + id + ") 出错，请检查配置文件: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        dbConfigurations.forEach((id, configuration) -> {
            try {
                core.getLogger().info("正在初始化数据库 #" + id + " ...");
                SQLManagerImpl sqlManager = this.core.create(id, configuration);
                this.managers.put(id, sqlManager);
                core.getLogger().info("完成初始化数据库 #" + id + " 。");
            } catch (Exception ex) {
                core.getLogger().severe("初始化SQLManager(#" + id + ") 出错，请检查配置文件: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

    }

    protected HashMap<String, SQLManager> getManagers() {
        return managers;
    }

    public ExecutorService getExecutor() {
        return executorPool;
    }

    public static MineSQLRegistry getInstance() {
        return instance;
    }

    public MineSQLCore getCore() {
        return core;
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
