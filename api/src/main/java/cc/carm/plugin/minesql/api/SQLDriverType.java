package cc.carm.plugin.minesql.api;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.function.SQLHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public enum SQLDriverType {

    MARIADB(
            "org.mariadb.jdbc.Driver", "jdbc:mariadb://",
            new String[]{"maria-db"}, null
    ),

    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", null, null),

    H2_FILE("org.h2.Driver", "jdbc:h2:file:",
            new String[]{"h2"},
            (manager) -> {
                manager.executeSQL("SET MODE=MySQL");
                manager.executeSQL("SET DB_CLOSE_DELAY=-1");
                manager.executeSQL("SET DB_CLOSE_ON_EXIT=FALSE");
            }
    ),

    H2_MEM("org.h2.Driver", "jdbc:h2:mem:",
            new String[]{"h2-memory", "h2-temp"},
            (manager) -> {
                manager.executeSQL("SET MODE=MySQL");
                manager.executeSQL("SET DB_CLOSE_DELAY=-1");
                manager.executeSQL("SET DB_CLOSE_ON_EXIT=FALSE");
            }
    );

    private final @NotNull String driverClass;
    private final @NotNull String jdbcPrefix;
    private final @NotNull String[] databaseAlias;
    private final @Nullable SQLHandler<SQLManager> initializer;

    SQLDriverType(@NotNull String driverClass, @NotNull String jdbcPrefix,
                  @Nullable String[] databaseAlias,
                  @Nullable SQLHandler<SQLManager> initializer) {

        this.driverClass = driverClass;
        this.jdbcPrefix = jdbcPrefix;
        this.databaseAlias = Optional.ofNullable(databaseAlias).orElse(new String[0]);
        this.initializer = initializer;
    }

    public @NotNull String[] getDatabaseAlias() {
        return databaseAlias;
    }

    public @NotNull String getDriverClass() {
        return driverClass;
    }

    public @NotNull String getJdbcPrefix() {
        return jdbcPrefix;
    }

    public @Nullable SQLHandler<SQLManager> getInitializer() {
        return initializer;
    }

    @Contract("null->null")
    public static @Nullable SQLDriverType parse(@Nullable String driverString) {
        if (driverString == null) return null;
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(driverString) || anyMatch(value.getDatabaseAlias(), driverString))
                .findFirst().orElse(null);
    }

    private static boolean anyMatch(String[] array, String value) {
        return Arrays.stream(array).anyMatch(s -> s.equalsIgnoreCase(value));
    }

}
