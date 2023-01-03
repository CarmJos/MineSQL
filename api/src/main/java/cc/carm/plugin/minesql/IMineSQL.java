package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.minesql.api.SQLRegistry;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import cc.carm.plugin.minesql.api.table.SQLTablesRoot;
import cc.carm.plugin.minesql.api.table.SimpleSQLTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
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

    @NotNull SQLRegistry createRegistry();

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

    /**
     * 读取一个 {@link SQLTablesRoot} 中全部的 {@link SimpleSQLTable} 实例并初始化。
     *
     * @param tablesRoot {@link SQLTablesRoot}实例
     */
    void createTables(@NotNull SQLTablesRoot tablesRoot) throws Exception;

    /**
     * 读取一个 {@link SQLTablesRoot}类中 中全部的静态 {@link SimpleSQLTable} 实例并初始化。
     *
     * @param tablesRootClazz {@link SQLTablesRoot}静态类
     */
    void createTables(@NotNull Class<? extends SQLTablesRoot> tablesRootClazz) throws Exception;

}
