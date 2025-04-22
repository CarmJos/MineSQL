package cc.carm.plugin.minesql.conf;

import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.plugin.minesql.MineSQL;
import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.conf.SQLDriverConfig;
import cc.carm.plugin.minesql.api.conf.drivers.H2MemConfig;
import cc.carm.plugin.minesql.api.conf.impl.FileBasedConfig;
import cc.carm.plugin.minesql.api.conf.impl.RemoteAuthConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SQLSourceGroup {

    protected final LinkedHashMap<String, SQLDriverConfig> sources;

    public SQLSourceGroup(LinkedHashMap<String, SQLDriverConfig> sources) {
        this.sources = sources;
    }

    public Map<String, SQLDriverConfig> getSources() {
        return Collections.unmodifiableMap(sources);
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        this.sources.forEach((k, v) -> data.put(k, v.serialize()));
        return data;
    }

    public static @NotNull SQLSourceGroup defaults() {
        LinkedHashMap<String, SQLDriverConfig> configs = new LinkedHashMap<>();
        configs.put("example-mysql", new RemoteAuthConfig(
                SQLDriverType.MARIADB, "127.0.0.1", 3306,
                "minecraft", "minecraft", "minecraft",
                "?sslMode=false"
        ));
        configs.put("example-h2-file", new FileBasedConfig(SQLDriverType.H2_FILE, "test"));
        configs.put("example-h2-mem", new H2MemConfig("temp"));
        return new SQLSourceGroup(configs);
    }

    public static @NotNull SQLSourceGroup parse(ConfigureSection rootSection) {
        LinkedHashMap<String, SQLDriverConfig> configs = new LinkedHashMap<>();
        for (String name : rootSection.getKeys(false)) {
            if (!rootSection.isSection(name)) continue;
            ConfigureSection section = rootSection.getSection(name);
            if (section == null) continue;
            SQLDriverConfig conf = parse(name, section);
            if (conf != null) configs.put(name, conf);
        }
        return new SQLSourceGroup(configs);
    }

    public static @Nullable SQLDriverConfig parse(String name, ConfigureSection section) {
        @Nullable String driverString = section.getString("type");
        @Nullable SQLDriverType driverType = SQLDriverType.parse(driverString);
        if (driverType == null) {
            MineSQL.getLogger().severe("驱动类型 " + driverString + " 不存在于预设中," + " 请检查配置文件 sources." + name + "。");
            return null;
        }

        switch (driverType) {
            case MYSQL:
            case MARIADB: {
                String host = section.getString("host");
                int port = section.getInt("port", 0);
                String database = section.getString("database");
                if (host == null || database == null || !(port > 0 && port <= 65535)) {
                    MineSQL.getLogger().severe("数据库连接配置有误," + " 请检查配置文件 sources." + name + "。");
                    return null;
                }

                String username = section.getString("username");
                String password = section.getString("password");
                String extra = section.getString("extra");
                return new RemoteAuthConfig(driverType, host, port, database, username, password, extra);
            }
            case H2_MEM: {
                return new H2MemConfig(section.getString("database"));
            }
            case H2_FILE: {
                String filePath = section.getString("file");
                if (filePath == null) {
                    MineSQL.getLogger().severe("数据库文件地址配置有误," + " 请检查配置文件 sources." + name + "。");
                    return null;
                }
                return new FileBasedConfig(SQLDriverType.H2_FILE, filePath);
            }
        }

        return null;
    }


}
