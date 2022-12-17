package cc.carm.plugin.minesql.api;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 入口类
 */
public interface SQLRegistry {

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
    @NotNull Optional<? extends SQLManager> getOptional(@Nullable String name);

    /**
     * 获取某命名空间下所有 SQLManager 实例
     *
     * @return {@link SQLManager} 实例集合
     */
    @Unmodifiable
    @NotNull Map<String, ? extends SQLManager> list();

    /**
     * 创建并注册一个新的 SQLManager 实例
     *
     * @param name          实例名称
     * @param configuration SQLManager 实例的配置
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@NotNull String name,
                               @NotNull SQLSourceConfig configuration) throws Exception;

    /**
     * 创建并注册一个新的 SQLManager 实例
     *
     * @param name       实例名称
     * @param properties SQLManager 实例的配置文件
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@NotNull String name,
                               @NotNull Properties properties) throws Exception;

    @NotNull SQLManager create(@NotNull String name, @NotNull DataSource source) throws Exception;

    /**
     * 终止并关闭一个 SQLManager 实例。
     *
     * @param manager       SQLManager实例
     * @param activeQueries 终止前仍未被关闭的SQLQuery列表
     */
    void shutdown(SQLManager manager, @Nullable Consumer<Map<UUID, SQLQuery>> activeQueries);

    /**
     * 终止并关闭一个 SQLManager 实例。
     *
     * @param manager    SQLManager实例
     * @param forceClose 是否强制关闭进行中的查询
     */
    default void shutdown(SQLManager manager, boolean forceClose) {
        shutdown(manager, (unclosedQueries) -> {
            if (forceClose) unclosedQueries.values().forEach(SQLQuery::close);
        });
    }

    /**
     * 终止并关闭一个 SQLManager 实例。
     * <br>若在终止时仍有活跃的查询，则将会强制关闭。
     *
     * @param manager SQLManager实例
     */
    default void shutdown(SQLManager manager) {
        shutdown(manager, true);
    }

}
