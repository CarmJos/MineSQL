package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.api.SQLRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

interface IMineSQL {

    @NotNull SQLRegistry getRegistry();

    @NotNull File getPluginFolder();

    default @NotNull File getSourceFolder() {
        return new File(getPluginFolder(), "db-files");
    }

    @NotNull Logger getLogger();

}
