package cc.carm.plugin.minesql.lib;

import cc.carm.plugin.minesql.util.VersionReader;
import net.byteflux.libby.Library;
import org.jetbrains.annotations.NotNull;

public enum PluginLibraries {

    BEECP("com.github.chris2018998", "beecp"),
    H2_DRIVER("com.h2database", "h2"),
    MYSQL_DRIVER("com.mysql", "mysql-connector-j"),
    MARIADB_DRIVER("org.mariadb.jdbc", "mariadb-java-client");

    public static final VersionReader READER = new VersionReader();

    private final @NotNull String groupID;
    private final @NotNull String artifactID;

    PluginLibraries(@NotNull String groupID, @NotNull String artifactID) {
        this.groupID = groupID;
        this.artifactID = artifactID;
    }

    public @NotNull Library getLibrary() {
        return Library.builder().id(name())
                .groupId(this.groupID).artifactId(this.artifactID)
                .version(getVersion())
                .build();
    }

    public @NotNull String getVersion() {
        return READER.get(name().toLowerCase().replace('_', '-'));
    }


}
