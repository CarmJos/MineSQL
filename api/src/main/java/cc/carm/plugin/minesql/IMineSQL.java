package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.minesql.api.SQLRegistry;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

interface IMineSQL {

    @NotNull File getPluginFolder();

    default @NotNull File getSourceFolder() {
        return new File(getPluginFolder(), "db-files");
    }

    @NotNull Logger getLogger();

    @NotNull SQLRegistry getRegistry();

    /**
     * 创建一个新的 SQLManager 实例
     *
     * @param name          实例名称
     * @param configuration SQLManager 实例的配置
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    @NotNull SQLManager create(@NotNull String name,
                               @NotNull SQLSourceConfig configuration) throws Exception;

    /**
     * 创建一个新的 SQLManager 实例
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

}
