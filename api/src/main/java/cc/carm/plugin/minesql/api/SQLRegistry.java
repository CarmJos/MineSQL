package cc.carm.plugin.minesql.api;

import cc.carm.lib.easysql.api.SQLManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;

/**
 * 入口类
 */
public interface SQLRegistry {


    /**
     * 获取原生注册的指定名称的 SQLManager 实例
     *
     * @param name 要获取的 SQLManager 实例名称, 如果为 null 则获取首个实例
     * @return {@link SQLManager} 实例
     */
    default @Nullable SQLManager get(@Nullable String name) {
        return getOptional(name).orElse(null);
    }

    /**
     * 获取原生注册的指定名称的 SQLManager 实例，并要求其不得为空
     *
     * @param name 要获取的 SQLManager 实例名称, 如果为 null 则获取首个实例
     * @return {@link SQLManager} 实例
     * @throws NullPointerException 若不存在对应实例则抛出空指针异常
     */
    default @NotNull SQLManager getNotNull(@Nullable String name) throws NullPointerException {
        return getOptional(name).orElseThrow(() -> new NullPointerException("并不存在ID为 #" + name + " 的SQLManager."));
    }

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
     * 注册一个新的 SQLManager 实例
     *
     * @param name       实例名称
     * @param sqlManager 实例
     * @throws IllegalStateException 当所要注册的实例已经存在时抛出
     */
    void register(@NotNull String name, @NotNull SQLManager sqlManager) throws IllegalStateException;

    /**
     * 从注册池中注销一个新的 SQLManager 实例
     *
     * @param name 实例名称
     * @return 所被注销的 {@link SQLManager}
     * @throws NullPointerException 当所要注销的实例不存在时抛出
     */
    @NotNull SQLManager unregister(@NotNull String name) throws NullPointerException;

}
