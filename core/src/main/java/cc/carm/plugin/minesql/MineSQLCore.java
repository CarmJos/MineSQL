package cc.carm.plugin.minesql;

import cc.carm.lib.configuration.EasyConfiguration;
import cc.carm.lib.configuration.yaml.YAMLConfigProvider;
import cc.carm.lib.easyplugin.utils.JarResourceUtils;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.lib.githubreleases4j.GithubReleases4J;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import cc.carm.plugin.minesql.api.table.SQLTablesRoot;
import cc.carm.plugin.minesql.api.table.SimpleSQLTable;
import cc.carm.plugin.minesql.command.MineSQLCommand;
import cc.carm.plugin.minesql.command.MineSQLHelpFormatter;
import cc.carm.plugin.minesql.conf.PluginConfiguration;
import cc.carm.plugin.minesql.conf.SQLSourceGroup;
import cc.carm.plugin.minesql.lib.PluginLibraries;
import cc.carm.plugin.minesql.util.DBPropertiesUtil;
import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.Locales;
import net.byteflux.libby.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MineSQLCore implements IMineSQL {

    protected static MineSQLCore instance;

    public static final String REPO_OWNER = "CarmJos";
    public static final String REPO_NAME = "MineSQL";

    protected final MineSQLPlatform platform;

    protected final MineSQLRegistry registry;
    protected final YAMLConfigProvider configProvider;
    protected final PluginConfiguration config;

    public MineSQLCore(MineSQLPlatform platform) {
        instance = this;
        this.platform = platform;

        getLogger().info("加载数据库依赖文件...");
        getPlatform().getLibraryManager().addMavenLocal();
        getPlatform().getLibraryManager().addMavenCentral();
        getPlatform().getLibraryManager().addSonatype();

        for (PluginLibraries value : PluginLibraries.values()) {
            Library lib = value.getLibrary();
            getLogger().info("  加载 " + lib.getArtifactId() + " (" + lib.getVersion() + ") ...");
            getPlatform().getLibraryManager().loadLibrary(value.getLibrary());
        }

        getLogger().info("加载配置文件...");
        this.configProvider = EasyConfiguration.from(new File(platform.getPluginFolder(), "config.yml"));
        this.config = new PluginConfiguration();
        this.configProvider.initialize(this.config);

        getLogger().info("初始化MineSQL API...");
        MineSQL.initializeAPI(this);

        getLogger().info("初始化注册池...");
        this.registry = createRegistry();

        Map<String, Properties> dbProperties = readProperties();
        Map<String, SQLSourceConfig> dbConfigurations = readConfigurations();

        if (dbProperties.isEmpty() && dbConfigurations.isEmpty()) {
            getLogger().warning("未检测到任何数据库配置，将不会预创建任何SQLManager。");
            return;
        }

        dbProperties.forEach((id, properties) -> {
            try {
                getLogger().info("正在初始化数据库 #" + id + " ...");
                SQLManagerImpl sqlManager = create(id, properties);
                this.registry.getManagers().put(id, sqlManager);
                getLogger().info("完成成初始化数据库 #" + id + " 。");
            } catch (Exception ex) {
                getLogger().severe("初始化SQLManager(#" + id + ") 出错，请检查配置文件: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        dbConfigurations.forEach((id, configuration) -> {
            try {
                getLogger().info("正在初始化数据库 #" + id + " ...");
                SQLManagerImpl sqlManager = create(id, configuration);
                this.registry.getManagers().put(id, sqlManager);
                getLogger().info("完成初始化数据库 #" + id + " 。");
            } catch (Exception ex) {
                getLogger().severe("初始化SQLManager(#" + id + ") 出错，请检查配置文件: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    public MineSQLPlatform getPlatform() {
        return platform;
    }

    public static MineSQLCore getInstance() {
        return instance;
    }

    @Override
    public @NotNull MineSQLRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public @NotNull MineSQLRegistry createRegistry() {
        return new MineSQLRegistry();
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull SQLSourceConfig conf) throws Exception {
        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName(conf.getDriverClassName());
        config.setJdbcUrl(conf.getJdbcURL());
        Optional.ofNullable(conf.getUsername()).ifPresent(config::setUsername);
        Optional.ofNullable(conf.getPassword()).ifPresent(config::setPassword);

        Optional.ofNullable(conf.getSettings().getPoolName()).ifPresent(config::setPoolName);
        Optional.ofNullable(conf.getSettings().getMaxPoolSize()).ifPresent(config::setMaxActive);
        Optional.ofNullable(conf.getSettings().getMaxActive()).ifPresent(config::setMaxActive);

        Optional.ofNullable(conf.getSettings().getIdleTimeout()).ifPresent(config::setIdleTimeout);
        Optional.ofNullable(conf.getSettings().getMaxWaitTime()).ifPresent(config::setMaxWait);
        Optional.ofNullable(conf.getSettings().getMaxHoldTime()).ifPresent(config::setHoldTimeout);

        Optional.ofNullable(conf.getSettings().getAutoCommit()).ifPresent(config::setDefaultAutoCommit);
        Optional.ofNullable(conf.getSettings().getReadOnly()).ifPresent(config::setDefaultReadOnly);
        Optional.ofNullable(conf.getSettings().getSchema()).ifPresent(config::setDefaultSchema);

        Optional.ofNullable(conf.getSettings().getValidationSQL()).ifPresent(config::setValidTestSql);
        Optional.ofNullable(conf.getSettings().getValidationTimeout()).ifPresent(config::setValidTestTimeout);
        Optional.ofNullable(conf.getSettings().getValidationInterval()).ifPresent(config::setValidAssumeTime);

        SQLManagerImpl manager = create(name, config);
        if (conf.getInitializer() != null) {
            conf.getInitializer().accept(manager);
        }

        return manager;
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull Properties properties) {
        return create(name, new BeeDataSourceConfig(properties));
    }

    @Override
    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull DataSource source) {
        return new SQLManagerImpl(source, name);
    }

    public @NotNull SQLManagerImpl create(@NotNull String name, @NotNull BeeDataSourceConfig configuration) {
        return create(name, (DataSource) new BeeDataSource(configuration));
    }

    @Override
    public void shutdown(SQLManager manager, @Nullable Consumer<Map<UUID, SQLQuery>> activeQueries) {
        if (activeQueries != null) activeQueries.accept(manager.getActiveQuery());

        if (manager.getDataSource() instanceof BeeDataSource) {
            BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
            dataSource.close();         //Close bee connection pool
        }
    }

    @Override
    public void createTables(@NotNull SQLTablesRoot tablesRoot) throws SQLException {
        for (Field field : tablesRoot.getClass().getDeclaredFields()) {
            initializeTableField(tablesRoot, field);
        }
    }

    @Override
    public void createTables(@NotNull Class<? extends SQLTablesRoot> clazz) throws SQLException {
        initializeTableClass(clazz);
    }

    protected void initializeTableClass(@NotNull Class<?> clazz) throws SQLException {
        if (!SQLTablesRoot.class.isAssignableFrom(clazz)) return;

        for (Field field : clazz.getDeclaredFields()) {
            initializeTableField(clazz, field);
        }

        for (Class<?> subClass : clazz.getDeclaredClasses()) {
            initializeTableClass(subClass);
        }

    }

    protected void initializeTableField(@NotNull Object source, @NotNull Field field) throws SQLException {
        try {
            field.setAccessible(true);
            Object object = field.get(source);

            if (object instanceof SimpleSQLTable) {
                ((SimpleSQLTable) object).create();
            } else if (source instanceof SQLTablesRoot && object instanceof SQLTablesRoot) {
                createTables((SQLTablesRoot) object);
            } else if (source instanceof Class<?> && object instanceof Class<?>) {
                // 当且仅当 源字段与字段 均为静态类时，才对目标字段进行下一步初始化加载。
                initializeTableClass((Class<?>) object);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    public void shutdownAll() {
        this.registry.getManagers().forEach((k, manager) -> {
            getLogger().info("   正在关闭数据库 " + k + "...");
            shutdown(manager, activeQueries -> {
                if (activeQueries.isEmpty()) return;
                getLogger().info("   数据库 " + k + " 仍有 " + activeQueries.size() + " 条活动查询");
                if (manager.getDataSource() instanceof BeeDataSource
                        && getConfig().SETTINGS.FORCE_CLOSE.getNotNull()) {
                    getLogger().info("   将强制关闭全部活跃链接...");
                    BeeDataSource dataSource = (BeeDataSource) manager.getDataSource();
                    dataSource.close();         //Close bee connection pool
                }
            });
        });
        this.registry.getManagers().clear();
    }

    @Override
    public @NotNull File getPluginFolder() {
        return getPlatform().getPluginFolder();
    }

    @Override
    public @NotNull Logger getLogger() {
        return getPlatform().getLogger();
    }

    public PluginConfiguration getConfig() {
        return config;
    }

    public YAMLConfigProvider getConfigProvider() {
        return configProvider;
    }

    public @NotNull Map<String, SQLSourceConfig> readConfigurations() {
        SQLSourceGroup group = getConfig().SOURCES.getNotNull();
        Map<String, SQLSourceConfig> sources = new LinkedHashMap<>();
        group.getSources().entrySet().stream()
                .filter(entry -> !entry.getKey().startsWith("example-"))
                .forEach(entry -> sources.put(entry.getKey(), entry.getValue().createSource()));
        return sources;
    }

    public @NotNull Map<String, Properties> readProperties() {
        if (!getConfig().PROPERTIES.ENABLE.getNotNull()) return new HashMap<>();

        String propertiesFolder = getConfig().PROPERTIES.FOLDER.get();
        if (propertiesFolder == null || propertiesFolder.length() == 0) return new HashMap<>();

        File file = new File(getPluginFolder(), propertiesFolder);
        if (!file.exists() || !file.isDirectory()) {
            if ((propertiesFolder.equals("db-properties/") || propertiesFolder.equals("db-properties"))) {
                try {
                    JarResourceUtils.copyFolderFromJar(
                            "db-properties", getPluginFolder(),
                            JarResourceUtils.CopyOption.COPY_IF_NOT_EXIST
                    );
                } catch (Exception ex) {
                    getLogger().severe("初始化properties示例文件失败：" + ex.getMessage());
                }
            } else {
                file.mkdirs();
            }
        }

        return DBPropertiesUtil.readFromFolder(file);
    }

    @SuppressWarnings("deprecation")
    protected void initializeCommands(CommandManager<?, ?, ?, ?, ?, ?> commandManager) {
        commandManager.enableUnstableAPI("help");
        commandManager.setHelpFormatter(new MineSQLHelpFormatter(commandManager));
        commandManager.getLocales().setDefaultLocale(Locales.SIMPLIFIED_CHINESE);
        commandManager.getCommandContexts().registerContext(SQLManager.class, c -> {
            String name = c.popFirstArg();
            try {
                return getRegistry().getNotNull(name);
            } catch (NullPointerException exception) {
                throw new InvalidCommandArgument("不存在名为 " + name + " 的数据库管理器。");
            }
        });
        commandManager.getCommandCompletions().registerCompletion("sql-managers", c -> {
            if (c.getIssuer().isPlayer()) return Collections.emptyList();
            else return getRegistry().list().keySet();
        });
        commandManager.registerCommand(new MineSQLCommand(this));
    }

    public void checkUpdate(String currentVersion) {
        Logger logger = getLogger();

        Integer behindVersions = GithubReleases4J.getVersionBehind(REPO_OWNER, REPO_NAME, currentVersion);
        String downloadURL = GithubReleases4J.getReleasesURL(REPO_OWNER, REPO_NAME);
        if (behindVersions == null) {
            logger.severe("检查更新失败，请您定期查看插件是否更新，避免安全问题。");
            logger.severe("下载地址 " + downloadURL);
        } else if (behindVersions < 0) {
            logger.severe("检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
            logger.severe("最新版下载地址 " + downloadURL);
        } else if (behindVersions > 0) {
            logger.warning("发现新版本! 目前已落后 " + behindVersions + " 个版本。");
            logger.warning("最新版下载地址 " + downloadURL);
        } else {
            logger.info("检查完成，当前已是最新版本。");
        }
    }


}
