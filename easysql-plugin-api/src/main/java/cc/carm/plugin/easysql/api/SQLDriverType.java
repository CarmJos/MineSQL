package cc.carm.plugin.easysql.api;

import org.jetbrains.annotations.NotNull;

public enum SQLDriverType {

    MARIADB("org.mariadb.jdbc.Driver", "jdbc:mariadb://", "mariadb", new String[]{}),
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", "mysql", new String[]{}),
    H2("org.h2.Driver", "jdbc:h2:", "h2", new String[]{"MODE=MySQL", "DB_CLOSE_DELAY=-1"});

    private final @NotNull String databaseID;
    private final @NotNull String driverClass;
    private final @NotNull String urlPrefix;
    private final @NotNull String[] defaultSettings;

    SQLDriverType(@NotNull String driverClass, @NotNull String urlPrefix,
                  @NotNull String databaseID, @NotNull String[] initializeSQLs) {
        this.databaseID = databaseID;
        this.driverClass = driverClass;
        this.urlPrefix = urlPrefix;
        this.defaultSettings = initializeSQLs;
    }

    public @NotNull String getDatabaseID() {
        return databaseID;
    }

    public @NotNull String getDriverClass() {
        return driverClass;
    }

    public @NotNull String getUrlPrefix() {
        return urlPrefix;
    }

    public @NotNull String[] getDefaultSettings() {
        return defaultSettings;
    }
}
