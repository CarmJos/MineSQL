package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.plugin.easysql.api.EasySQLAPI;
import cc.carm.plugin.easysql.api.EasySQLPluginPlatform;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EasySQLManager implements EasySQLAPI {
    public static final String VERSION = "1.0.0";
    private final Lock LOCK =new ReentrantLock();
    private final BiMap<String, Set<String>> nameSpaceRegistry = HashBiMap.create();
    private final BiMap<String, SQLManager> sqlManagerRegistry = HashBiMap.create();

    // 这个主要是避免外部插件注册此 EasySQLManager
    // 没有其他意义
    public EasySQLManager(@NotNull EasySQLPluginPlatform pluginPlatform){

    }

    /**
     * 获取指定名称的 SQLManager 实例
     *
     * @param name 要获取的 SQLManager 实例名称
     * @return SQLManager 实例
     */
    @Override
    public @Nullable SQLManager getSQLManager(@Nullable String name) {
        LOCK.lock();
        SQLManager sqlManager = this.sqlManagerRegistry.get(name);
        LOCK.unlock();
        return sqlManager;
    }

    /**
     * 获取所有 SQLManager 实例
     *
     * @return SQLManager 实例集合
     */
    @Override
    public @NotNull Map<String, SQLManager> getSQLManagers() {
        return ImmutableMap.copyOf(this.sqlManagerRegistry);
    }

    /**
     * 获取指定名称的 SQLManager 实例的命名空间
     *
     * @param name SQLManager 名称
     * @return 命名空间
     */
    @Override
    public @Nullable String getNameSpace(@Nullable String name) {
        LOCK.lock();
        String namespace = null;
        for (Map.Entry<String, Set<String>> stringSetEntry : this.nameSpaceRegistry.entrySet()) {
            if (stringSetEntry.getValue().contains(name)) {
                namespace = stringSetEntry.getKey();
                break;
            }
        }
        LOCK.unlock();
        return namespace;
    }

    /**
     * 获取指定 SQLManager 的注册名
     *
     * @param sqlManager SQLManager 实例
     * @return 注册名
     */
    @Override
    public @Nullable String getName(@NotNull SQLManager sqlManager) {
        LOCK.lock();
        String name = sqlManagerRegistry.inverse().get(sqlManager);
        LOCK.unlock();
        return name;
    }

    /**
     * 注册 SQLManager 实例
     *
     * @param namespace  命名空间，通常为插件名称
     * @param name       实例名称
     * @param sqlManager SQLManager 实例
     */
    @Override
    public void registerSQLManager(@NotNull String namespace, @NotNull String name, @NotNull SQLManager sqlManager) {
        LOCK.lock();
        // 注册命名空间
        this.sqlManagerRegistry.put(name, sqlManager);
        // 注册名称
        this.nameSpaceRegistry.putIfAbsent(namespace, this.nameSpaceRegistry.getOrDefault(namespace, new HashSet<>()));
        // 注册名称 -> 命名空间
        this.nameSpaceRegistry.get(namespace).add(name);
        LOCK.unlock();
    }

    /**
     * 注销指定的 SQLManager 实例
     *
     * @param name 实例名称
     */
    @Override
    public void unregisterSQLManager(@NotNull String name) {
        LOCK.lock();
        // 先清除命名空间
        this.sqlManagerRegistry.remove(name);
        // 再清除名称
        for (Map.Entry<String, Set<String>> stringSetEntry : this.nameSpaceRegistry.entrySet()) {
            stringSetEntry.getValue().remove(name);
        }
        LOCK.unlock();
    }

    /**
     * 注销指定命名空间下的所有 SQLManager 实例
     *
     * @param namespace 命名空间
     */
    @Override
    public void unregisterSQLManagerAll(@NotNull String namespace) {
        LOCK.lock();
        Set<String> names = this.nameSpaceRegistry.get(namespace);
        if (names != null) {
            // 删除所有的 SQLManager 实例
            for (String name : names) {
                this.sqlManagerRegistry.remove(name);
            }
        }
        // 删除命名空间
        this.nameSpaceRegistry.remove(namespace);
        LOCK.unlock();
    }
}
