package cc.carm.plugin.minesql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.minesql.api.SQLRegistry;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import cc.carm.plugin.minesql.api.table.SQLTablesRoot;
import cc.carm.plugin.minesql.api.table.SimpleSQLTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MineSQL {

    private MineSQL() {
        throw new UnsupportedOperationException("API Instance");
    }

    private static IMineSQL instance;

    @ApiStatus.Internal
    @SuppressWarnings("ClassEscapesDefinedScope")
    protected static void initializeAPI(@NotNull IMineSQL api) {
        MineSQL.instance = api;
    }

    public static Logger getLogger() {
        return instance.getLogger();
    }

    /**
     * @return 数据库源文件所在目录，非插件数据目录。
     */
    public static File getDataSourceFolder() {
        return instance.getSourceFolder();
    }

    /**
     * 得到管理器注册池
     *
     * @return {@link SQLRegistry} 注册池
     */
    public static SQLRegistry getRegistry() {
        return instance.getRegistry();
    }

    /**
     * 创建一个独立的管理器注册池。
     *
     * @return {@link SQLRegistry}
     */
    public static @NotNull SQLRegistry createRegistry() {
        return instance.createRegistry();
    }

    /**
     * 创建一个新的 SQLManager 实例
     *
     * @param name          实例名称
     * @param configuration SQLManager 实例的配置
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    public static @NotNull SQLManager create(@NotNull String name,
                                             @NotNull SQLSourceConfig configuration) throws Exception {
        return instance.create(name, configuration);
    }

    /**
     * 创建一个新的 SQLManager 实例
     *
     * @param name       实例名称
     * @param properties SQLManager 实例的配置文件
     * @return {@link SQLManager} 实例
     * @throws Exception 若创建失败则抛出异常
     */
    public static @NotNull SQLManager create(@NotNull String name,
                                             @NotNull Properties properties) throws Exception {
        return instance.create(name, properties);
    }

    public static @NotNull SQLManager create(@NotNull String name, @NotNull DataSource source) throws Exception {
        return instance.create(name, source);
    }

    /**
     * 终止并关闭一个 SQLManager 实例。
     *
     * @param manager       SQLManager实例
     * @param activeQueries 终止前仍未被关闭的SQLQuery列表
     */
    public static void shutdown(SQLManager manager, @Nullable Consumer<Map<UUID, SQLQuery>> activeQueries) {
        instance.shutdown(manager, activeQueries);
    }

    /**
     * 终止并关闭一个 SQLManager 实例。
     *
     * @param manager    SQLManager实例
     * @param forceClose 是否强制关闭进行中的查询
     */
    public static void shutdown(SQLManager manager, boolean forceClose) {
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
    public static void shutdown(SQLManager manager) {
        shutdown(manager, true);
    }


    /**
     * 读取一个 {@link SQLTablesRoot} 中全部的 {@link SimpleSQLTable} 实例并初始化。
     *
     * @param tablesRoot {@link SQLTablesRoot}实例
     */
    public static void createTables(@NotNull SQLTablesRoot tablesRoot) throws Exception {
        instance.createTables(tablesRoot);
    }

    /**
     * 读取一个 {@link SQLTablesRoot}类中 中全部的静态 {@link SimpleSQLTable} 实例并初始化。
     *
     * @param tablesRootClazz {@link SQLTablesRoot}静态类
     */
    public static void createTables(@NotNull Class<? extends SQLTablesRoot> tablesRootClazz) throws Exception {
        instance.createTables(tablesRootClazz);
    }

}
