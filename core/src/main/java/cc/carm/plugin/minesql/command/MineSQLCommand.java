package cc.carm.plugin.minesql.command;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.minesql.MineSQLCore;
import cc.carm.plugin.minesql.MineSQLRegistry;
import cc.carm.plugin.minesql.util.VersionReader;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;

import java.util.Map;
import java.util.UUID;


@SuppressWarnings("unused")
@CommandAlias("MineSQL")
@Description("MineSQL的主指令，用于开发者进行调试，只允许后台执行。")
public class MineSQLCommand extends BaseCommand {

    protected final MineSQLCore core;

    public MineSQLCommand(MineSQLCore core) {
        this.core = core;
    }

    @HelpCommand
    @Syntax("&9[页码或子指令名称]")
    @Description("查看指定数据源的统计信息与当前仍未关闭的查询。")
    public void help(CommandIssuer issuer, CommandHelp help) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("§c只有后台执行才能使用此命令。");
            return;
        }
        help.showHelp();
    }

    @Subcommand("version")
    @Description("查看当前插件版本与核心库(EasySQL)版本。")
    public void version(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("§c只有后台执行才能使用此命令。");
            return;
        }
        VersionReader reader = new VersionReader();
        String pluginVersion = reader.get("plugin", null);
        if (pluginVersion == null) {
            issuer.sendMessage("§c无法获取当前版本信息，请保证使用原生版本以避免安全问题。");
            return;
        }
        issuer.sendMessage("§r当前插件版本为 §b" + pluginVersion + "§r。 §7(基于 EasySQL &3" + reader.get("api") + "&7)");
        issuer.sendMessage("§8 - &f连接池依赖 BeeCP §9" + reader.get("beecp"));
        issuer.sendMessage("§8 - &f数据库驱动 MySQL §9" + reader.get("mysql-driver"));
        issuer.sendMessage("§8 - &f数据库驱动 MariaDB §9" + reader.get("mariadb-driver"));
        issuer.sendMessage("§8 - &f数据库驱动 h2-database §9" + reader.get("h2-driver"));

        issuer.sendMessage("§r正在检查插件更新，请稍候...");
        core.checkUpdate(pluginVersion);
    }

    @Subcommand("list")
    @Description("列出当前所有的数据源管理器与相关信息。")
    public void list(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("§c只有后台执行才能使用此命令。");
            return;
        }
        Map<String, ? extends SQLManager> runningManagers = MineSQLRegistry.getInstance().list();
        if (runningManagers.isEmpty()) {
            issuer.sendMessage("§r当前无正在运行的数据库管理器。");
        } else {
            issuer.sendMessage("§r当前有 §b" + runningManagers.size() + " §r个正在运行的数据库管理器:");
            runningManagers.forEach(
                    (name, manager) -> issuer.sendMessage("- " + name + " (" + manager.getActiveQuery().size() + " running)")
            );
        }

    }

    @Subcommand("info")
    @CommandCompletion("@sql-managers")
    @Syntax("&9<数据源名称>")
    @Description("查看指定数据源的统计信息与当前仍未关闭的查询。")
    public void info(CommandIssuer issuer,
                     @Syntax("&9数据源名称") @Description("数据源管理器名称，一般在配置文件中指定。") SQLManager manager) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("§c只有后台执行才能使用此命令。");
            return;
        }
        Map<UUID, SQLQuery> activeQueries = manager.getActiveQuery();
        if (activeQueries.isEmpty()) {
            issuer.sendMessage("§r当前暂无活跃查询。");
        } else {
            issuer.sendMessage("§r当前有 §b" + activeQueries.size() + " §r个活跃查询:");
            activeQueries.forEach((uuid, query) -> {
                issuer.sendMessage("§8#§b " + uuid.toString());
                issuer.sendMessage("§8-§f " + query.getSQLContent());
            });
        }
    }

}
