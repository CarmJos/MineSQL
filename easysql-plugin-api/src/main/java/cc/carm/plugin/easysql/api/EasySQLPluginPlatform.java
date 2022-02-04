package cc.carm.plugin.easysql.api;

import cc.carm.lib.easysql.api.SQLManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EasySQLPluginPlatform {
    /**
     * 获取 SQL 管理器
     * @param name 注册键
     * @return SQL管理器
     */
    @Nullable
    SQLManager getSqlManager(@NotNull String name);
}
