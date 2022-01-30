package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.easysql.api.EasySQLAPI;
import cc.carm.plugin.easysql.api.EasySQLPluginPlatform;
import cn.beecp.BeeDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class EasySQLBukkit extends JavaPlugin implements EasySQLPluginPlatform, Listener {
    private EasySQLAPI apiImpl;

    private boolean setup = false;

    public void setup() {
        if (setup) return;
        apiImpl = new EasySQLManager(this);
        saveDefaultConfig();
        // 读取配置文件 - 预注册 instance
        ConfigurationSection instancesConfig = getConfig().getConfigurationSection("instances");
        if (instancesConfig != null) {
            for (String instanceName : instancesConfig.getKeys(false)) {
                SQLManager sqlManager = new SQLManagerImpl(
                        new BeeDataSource("mysql"
                                , instancesConfig.getString("url")
                                , instancesConfig.getString("username")
                                , instancesConfig.getString("password")
                        )
                );
                this.register(this, instanceName, sqlManager);
            }
        }
        // 获取指定UUID的玩家的盔甲
        setup = true;
    }

    @Override
    public void onLoad() {
        setup(); // 尽可能早的初始化预注册的 SQLManager
    }

    @Override
    public void onEnable() {
        setup();  // 在特定情况下 Bukkit 可能不会调用 onLoad 函数
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
        this.unregister(this);
        apiImpl.getSQLManagers().keySet().forEach(apiImpl::unregisterSQLManager);
        super.onDisable();
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event){
        this.unregister(event.getPlugin());
    }
    // =========== 平台注册方法 ===========

    /**
     * 注册 SQLManager 实例
     * @param plugin 插件实例
     * @param name SQLManager 映射名称
     * @param sqlManager SQLManager 实例
     */
    public void register(@NotNull Plugin plugin, @NotNull String name, @NotNull SQLManager sqlManager){
        if(this.apiImpl.getSQLManagers().containsKey(name)){
            // 名称冲突处理
            throw new IllegalArgumentException("Instance name conflict: " + name+", Already registered by "+this.apiImpl.getNameSpace(name));
        }
        this.apiImpl.registerSQLManager(plugin.getDescription().getName(), name, sqlManager);
    }

    /**
     * 注销指定 SQLManager 实例
     * @param sqlManager SQLManager 实例
     */
    public void unregister(@NotNull SQLManager sqlManager){
        String name = this.apiImpl.getName(sqlManager);
        if(name != null){
            this.apiImpl.unregisterSQLManager(name);
        }
    }

    /**
     * 注销指定名称的 SQLManager
     * @param name SQLManager 名称
     */
    public void unregister(@NotNull String name){
        this.apiImpl.unregisterSQLManager(name);
    }

    /**
     * 注销指定插件注册的所有 SQLManager 实例
     * @param plugin 插件
     */
    public void unregister(@NotNull Plugin plugin){
        this.apiImpl.unregisterSQLManagerAll(plugin.getName());
    }

}
