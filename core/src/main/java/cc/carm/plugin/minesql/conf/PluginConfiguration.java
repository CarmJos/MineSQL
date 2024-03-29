package cc.carm.plugin.minesql.conf;

import cc.carm.lib.configuration.core.ConfigurationRoot;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.ConfigValue;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;

public class PluginConfiguration extends ConfigurationRoot {

    @HeaderComment("排错模式，一般留给开发者检查问题，平常使用无需开启。")
    public final ConfigValue<Boolean> DEBUG = ConfiguredValue.of(Boolean.class, false);

    @HeaderComment({"",
            "统计数据设定",
            "该选项用于帮助开发者统计插件版本与使用情况，且绝不会影响性能与使用体验。",
            "当然，您也可以选择在这里关闭，或在plugins/bStats下的配置文件中关闭所有插件的统计信息。"
    })
    public final ConfigValue<Boolean> METRICS = ConfiguredValue.of(Boolean.class, true);

    @HeaderComment({"",
            "检查更新设定",
            "该选项用于插件判断是否要检查更新，若您不希望插件检查更新并提示您，可以选择关闭。",
            "检查更新为异步操作，绝不会影响性能与使用体验。"
    })
    public ConfigValue<Boolean> UPDATE_CHECKER = ConfiguredValue.of(Boolean.class, true);

    @HeaderComment({"插件注册池配置"})
    public final SettingsConfig SETTINGS = new SettingsConfig();

    @HeaderComment({"",
            "Properties 数据库配置文件配置",
            "相关配置介绍(BeeCP) https://github.com/Chris2018998/BeeCP/wiki/Configuration--List#配置列表"
    })
    public final PropertiesConfig PROPERTIES = new PropertiesConfig();

    @HeaderComment({"",
            "数据库源配置",
            "目前支持的驱动类型(type)有 mariadb、mysql、h2-file(文件数据库) 与 h2-mem(内存临时数据库)。",
            "详细配置介绍请查看 https://github.com/CarmJos/MineSQL/.doc/README.md"
    })
    public ConfigValue<SQLSourceGroup> SOURCES = ConfigValue.builder()
            .asValue(SQLSourceGroup.class).fromSection()
            .parseValue((w, d) -> SQLSourceGroup.parse(w))
            .serializeValue(SQLSourceGroup::serialize)
            .defaults(SQLSourceGroup.defaults())
            .build();

    public static class PropertiesConfig extends ConfigurationRoot {

        @HeaderComment({"该选项用于启用 Properties 配置读取。", "若您不希望插件启用 Properties 文件配置功能，可以选择关闭。"})
        public ConfigValue<Boolean> ENABLE = ConfiguredValue.of(Boolean.class, true);

        @HeaderComment({
                "文件夹路径，将读取该文件夹下的所有 .properties 文件，并以文件名为数据管理器名称。",
                "读取时，将排除以 “.” 开头的文件与非 .properties 文件。",
                "默认为 \"db-properties/\" 相对路径，指向“plugins/MineSQL/db-properties/”；",
                "该选项也支持绝对路径，但使用绝对路径时，请务必注意权限问题。"
        })
        public ConfigValue<String> FOLDER = ConfiguredValue.of(String.class, "db-properties/");

    }

    public static class SettingsConfig extends ConfigurationRoot {

        @HeaderComment({"在插件卸载时是否强制关闭活跃链接"})
        public ConfigValue<Boolean> FORCE_CLOSE = ConfiguredValue.of(Boolean.class, true);

    }


}
