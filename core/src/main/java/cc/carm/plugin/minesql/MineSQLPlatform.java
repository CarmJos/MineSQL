package cc.carm.plugin.minesql;

import co.aikar.commands.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public interface MineSQLPlatform {

    @NotNull File getPluginFolder();

    @NotNull Logger getLogger();

    @Nullable CommandManager<?, ?, ?, ?, ?, ?> getCommandManager();

}
