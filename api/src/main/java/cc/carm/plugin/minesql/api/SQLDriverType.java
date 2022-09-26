package cc.carm.plugin.minesql.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum SQLDriverType {

    MARIADB("org.mariadb.jdbc.Driver", "jdbc:mariadb://",
            new String[]{"mariadb", "maria-db"}, new String[]{}
    ),
    MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://",
            new String[]{"mysql"}, new String[]{}
    ),
    H2("org.h2.Driver", "jdbc:h2:",
            new String[]{"h2", "h2-db", "h2-database"},
            new String[]{"SET MODE=MySQL", "SET DB_CLOSE_DELAY=-1"}
    );

    private final @NotNull String driverClass;
    private final @NotNull String urlPrefix;
    private final @NotNull String[] databaseAlias;
    private final @NotNull String[] initializeSQLs;

    SQLDriverType(@NotNull String driverClass, @NotNull String urlPrefix,
                  @NotNull String[] databaseAlias,
                  @NotNull String[] initializeSQLs) {

        this.driverClass = driverClass;
        this.urlPrefix = urlPrefix;
        this.databaseAlias = databaseAlias;
        this.initializeSQLs = initializeSQLs;
    }

    public @NotNull String[] getDatabaseAlias() {
        return databaseAlias;
    }

    public @NotNull String getDriverClass() {
        return driverClass;
    }

    public @NotNull String getUrlPrefix() {
        return urlPrefix;
    }

    public @NotNull String[] getInitializeSQLs() {
        return initializeSQLs;
    }

    @Contract("null->null")
    public static @Nullable SQLDriverType parse(@Nullable String driverString) {
        if (driverString == null) return null;
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(driverString) || has(value.getDatabaseAlias(), driverString))
                .findFirst().orElse(null);
    }


    private static boolean has(String[] array, String value) {
        return Arrays.stream(array).anyMatch(s -> s.equalsIgnoreCase(value));
    }
}
