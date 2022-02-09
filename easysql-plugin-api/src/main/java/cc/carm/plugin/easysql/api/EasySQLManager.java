package cc.carm.plugin.easysql.api;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * 入口类
 */
public interface EasySQLManager {

    /**
     * 获取原生注册的指定名称的 SQLManager 实例
     *
     * @param name 要获取的 SQLManager 实例名称, 如果为 null 则获取首个实例
     * @return {@link SQLManager} 实例
     * @throws NullPointerException 若不存在对应实例则抛出空指针异常
     */
    @NotNull SQLManager get(@Nullable String name) throws NullPointerException;

    /**
     * 获取原生注册的指定名称的 SQLManager 实例
     *
     * @param name 要获取的 SQLManager 实例名称, 如果为 null 则获取首个实例
     * @return {@link SQLManager} 实例
     */
    @NotNull Optional<@Nullable SQLManager> getOptional(@Nullable String name);

    /**
     * 获取某命名空间下所有 SQLManager 实例
     *
     * @return {@link SQLManager} 实例集合
     */
    @Unmodifiable
    @NotNull Map<String, SQLManager> list();

    /**
     * 创建并注册一个新的 SQLManager 实例
     *
     * @param name          实例名称
     * @param configuration SQLManager 实例的配置
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@Nullable String name,
                               @NotNull DBConfiguration configuration) throws Exception;

    /**
     * 创建并注册一个新的 SQLManager 实例
     *
     * @param name       实例名称
     * @param properties SQLManager 实例的配置文件
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@Nullable String name,
                               @NotNull Properties properties) throws Exception;

    /**
     * 创建并注册一个新的 SQLManager 实例
     *
     * @param name             实例名称
     * @param propertyFileName 配置文件的资源名称
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@Nullable String name,
                               @NotNull String propertyFileName) throws Exception;

    /**
     * 终止一个 SQLManager 实例。
     *
     * @param manager
     * @return
     */
    default Map<UUID, SQLQuery> shutdown(SQLManager manager) {
        return shutdown(manager, true);
    }

    Map<UUID, SQLQuery> shutdown(SQLManager manager, boolean forceClose);

}
