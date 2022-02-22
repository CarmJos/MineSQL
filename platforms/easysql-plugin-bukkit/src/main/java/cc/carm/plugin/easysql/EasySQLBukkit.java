package cc.carm.plugin.easysql;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.lib.easyplugin.i18n.EasyPluginMessageProvider;
import cc.carm.plugin.easysql.api.DBConfiguration;
import cc.carm.plugin.easysql.util.PropertiesUtil;
import cc.carm.plugin.easysql.util.ResourceReadUtil;
import co.aikar.commands.PaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class EasySQLBukkit extends EasyPlugin implements EasySQLPluginPlatform {

    public EasySQLBukkit() {
        super(new EasyPluginMessageProvider.zh_CN());
    }

    protected static EasySQLBukkit instance;

    private PaperCommandManager commandManager;
    private EasySQLRegistryImpl registry;

    @Override
    protected void load() {
        EasySQLBukkit.instance = this;
        this.registry = new EasySQLRegistryImpl(this);

        initializeAPI(getRegistry()); // 尽快的初始化接口，方便其他插件调用
    }

    @Override
    protected boolean initialize() {
        this.commandManager = new PaperCommandManager(this);
        initializeCommands(getCommandManager());
        return true;
    }

    @Override
    @NotNull
    public EasySQLRegistryImpl getRegistry() {
        return this.registry;
    }

    @Override
    public @NotNull
    Map<String, DBConfiguration> readConfigurations() {
        return new HashMap<>();
    }

    @Override
    public @NotNull
    Map<String, Properties> readProperties() {
        return PropertiesUtil.readDBProperties(new File(getDataFolder(), "db-properties"));
    }

    @Override
    public void outputInfo() {
        Optional.ofNullable(ResourceReadUtil.readResource(this.getResource("PLUGIN_INFO"))).ifPresent(this::log);
    }

    public static EasySQLBukkit getInstance() {
        return EasySQLBukkit.instance;
    }

    protected PaperCommandManager getCommandManager() {
        return commandManager;
    }


}
