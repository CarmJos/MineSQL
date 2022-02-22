package cc.carm.plugin.easysql.command;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.plugin.easysql.EasySQLRegistryImpl;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;

import java.util.Map;
import java.util.UUID;

import static cc.carm.plugin.easysql.util.ResourceReadUtil.getVersion;


@SuppressWarnings("unused")
@CommandAlias("EasySQL")
@Description("EasySQL-Plugin的主指令，用于开发者进行调试，只允许后台执行。")
public class EasySQLCommand extends BaseCommand {

    @HelpCommand
    @Syntax("&9[页码或子指令名称]")
    @Description("查看指定数据源的统计信息与当前仍未关闭的查询。")
    public void help(CommandIssuer issuer, CommandHelp help) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("只有后台执行才能使用此命令。");
            return;
        }
        help.showHelp();
    }

    @Subcommand("version")
    @Description("查看当前插件版本与核心库(EasySQL)版本。")
    public void version(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("只有后台执行才能使用此命令。");
            return;
        }
        String pluginVersion = getVersion(this, "cc.carm.plugin", "easysql-plugin-core");
        String apiVersion = getVersion(this, "cc.carm.lib", "easysql-api");
        String poolVersion = getVersion(this, "com.github.chris2018998", "beecp");
        if (pluginVersion == null || apiVersion == null) {
            issuer.sendMessage("无法获取当前版本信息，请保证使用原生版本以避免安全问题。");
            return;
        }
        issuer.sendMessage("当前插件版本为 " + pluginVersion + " ，核心接口版本为 " + apiVersion + "。 (基于 BeeCP " + poolVersion + ")");
        issuer.sendMessage("正在检查更新，请稍候...");
        EasySQLRegistryImpl.getInstance().checkUpdate(pluginVersion);
    }

    @Subcommand("list")
    @Description("列出当前所有的数据源管理器与相关信息。")
    public void list(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("只有后台执行才能使用此命令。");
            return;
        }
        Map<String, ? extends SQLManager> runningManagers = EasySQLRegistryImpl.getInstance().list();
        if (runningManagers.isEmpty()) {
            issuer.sendMessage("当前无正在运行的数据库管理器。");
        } else {
            issuer.sendMessage("当前有 " + runningManagers.size() + " 个正在运行的数据库管理器:");
            runningManagers.forEach(
                    (name, manager) -> issuer.sendMessage("- " + name + " (" + manager.getActiveQuery().size() + " running)")
            );
        }

    }

    @Subcommand("info")
    @CommandCompletion("@sql-managers")
    @Syntax("&9<数据源管理器名称>")
    @Description("查看指定数据源的统计信息与当前仍未关闭的查询。")
    public void info(CommandIssuer issuer,
                     @Syntax("数据源管理器名称，一般在配置文件中指定。") SQLManager manager) {
        if (issuer.isPlayer()) {
            issuer.sendMessage("只有后台执行才能使用此命令。");
            return;
        }
        Map<UUID, SQLQuery> activeQueries = manager.getActiveQuery();
        if (activeQueries.isEmpty()) {
            issuer.sendMessage("当前暂无活跃查询。");
        } else {
            issuer.sendMessage("当前有 " + activeQueries.size() + " 个活跃查询:");
            activeQueries.forEach((uuid, query) -> {
                issuer.sendMessage("# " + uuid.toString());
                issuer.sendMessage("- " + query.getSQLContent());
            });
        }
    }

}
