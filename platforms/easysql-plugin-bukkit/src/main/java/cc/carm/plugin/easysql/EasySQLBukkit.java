package cc.carm.plugin.easysql;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.plugin.easysql.api.DBConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EasySQLBukkit extends EasyPlugin implements EasySQLPluginPlatform, Listener {

    @Override
    protected void load() {

    }

    @Override
    protected boolean initialize() {
        return false;
    }

    @Override
    public @NotNull Map<String, DBConfiguration> readConfigurations() {
        return new HashMap<>();
    }

}
