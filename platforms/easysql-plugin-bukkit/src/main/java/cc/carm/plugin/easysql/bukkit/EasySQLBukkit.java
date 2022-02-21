package cc.carm.plugin.easysql.bukkit;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.lib.easyplugin.i18n.EasyPluginMessageProvider;
import cc.carm.plugin.easysql.EasySQLPluginPlatform;
import cc.carm.plugin.easysql.EasySQLRegistryImpl;
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

    protected PaperCommandManager commandManager;
    protected EasySQLRegistryImpl registry;

    @Override
    protected void load() {
        EasySQLBukkit.instance = this;
        this.commandManager = new PaperCommandManager(this);


        initializeAPI(getRegistry());
    }

    @Override
    protected boolean initialize() {
        //TODO COMMANDS
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
        return PropertiesUtil.readDBProperties(new File(getDataFolder(), "properties"));
    }

    @Override
    public void outputInfo() {
        Optional.ofNullable(ResourceReadUtil.readResource(this.getResource("info.txt"))).ifPresent(this::log);
    }

    public static EasySQLBukkit getInstance() {
        return EasySQLBukkit.instance;
    }


    protected PaperCommandManager getCommandManager() {
        return commandManager;
    }


}
