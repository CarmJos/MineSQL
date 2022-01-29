package cc.carm.plugin.easysql;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.manager.SQLManagerImpl;
import cc.carm.plugin.easysql.api.EasySQLAPI;
import cc.carm.plugin.easysql.api.EasySQLPluginPlatform;
import cn.beecp.BeeDataSource;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class EasySQLBungee extends Plugin implements EasySQLPluginPlatform {
    private EasySQLAPI apiImpl;

    private boolean setup = false;

    private void saveDefaultConfig(){
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setup() {
        if (setup) return;
        apiImpl = new EasySQLManager(this);
        saveDefaultConfig();
        // 读取配置文件 - 预注册 instance
        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Configuration instancesConfig = configuration.getSection("instances");
        if (instancesConfig != null) {
            for (String instanceName : instancesConfig.getKeys()) {
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
        setup = true;
    }

    @Override
    public void onLoad() {
        setup();
    }

    @Override
    public void onEnable() {
        setup();
    }
    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
        this.unregister(this);
        apiImpl.getSQLManagers().keySet().forEach(apiImpl::unregisterSQLManager);
        super.onDisable();
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
        this.apiImpl.unregisterSQLManagerAll(plugin.getDescription().getName());
    }
}
