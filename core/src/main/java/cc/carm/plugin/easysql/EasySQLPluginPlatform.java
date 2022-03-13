package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.api.EasySQLRegistry;
import cc.carm.plugin.easysql.command.EasySQLCommand;
import cc.carm.plugin.easysql.command.EasySQLHelpFormatter;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.Locales;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public interface EasySQLPluginPlatform {

    @NotNull EasySQLRegistry getRegistry();

    @NotNull Map<String, DBConfiguration> readConfigurations();

    @NotNull Map<String, Properties> readProperties();

    Logger getLogger();

    default void initializeAPI(EasySQLRegistry registry) {
        EasySQLAPI.initializeAPI(registry);
    }

    @SuppressWarnings("deprecation")
    default void initializeCommands(CommandManager<?, ?, ?, ?, ?, ?> commandManager) {
        commandManager.enableUnstableAPI("help");
        commandManager.setHelpFormatter(new EasySQLHelpFormatter(commandManager));
        commandManager.getLocales().setDefaultLocale(Locales.SIMPLIFIED_CHINESE);
        commandManager.getCommandContexts().registerContext(SQLManager.class, c -> {
            String name = c.popFirstArg();
            try {
                return getRegistry().get(name);
            } catch (NullPointerException exception) {
                throw new InvalidCommandArgument("不存在名为 " + name + " 的数据库管理器。");
            }
        });
        commandManager.getCommandCompletions().registerCompletion("sql-managers", c -> {
            if (c.getIssuer().isPlayer()) return ImmutableList.of();
            else return ImmutableList.copyOf(getRegistry().list().keySet());
        });
        commandManager.registerCommand(new EasySQLCommand());
    }

}
