package cc.carm.plugin.easysql.api;

import cc.carm.lib.easysql.api.SQLManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 入口类
 */
public interface EasySQLAPI {
	/**
	 * 获取指定名称的 SQLManager 实例
	 * @param name 要获取的 SQLManager 实例名称
	 * @return SQLManager 实例
	 */
	@Nullable SQLManager getSQLManager(@Nullable String name);

	/**
	 * 获取所有 SQLManager 实例
	 * @return SQLManager 实例集合
	 */
	@NotNull Map<String, SQLManager> getSQLManagers();

	/**
	 * 获取指定名称的 SQLManager 实例的命名空间
	 * @param name SQLManager 名称
	 * @return 命名空间
	 */
	@Nullable
	String getNameSpace(@Nullable String name);

	/**
	 * 获取指定 SQLManager 的注册名
	 * @param sqlManager SQLManager 实例
	 * @return 注册名
	 */
	@Nullable
	String getName(@NotNull SQLManager sqlManager);

	/**
	 * 注册 SQLManager 实例
	 * @param namespace 命名空间，通常为插件名称
	 * @param name 实例名称
	 * @param sqlManager SQLManager 实例
	 */
	void registerSQLManager(@NotNull String namespace,@NotNull String name, @NotNull SQLManager sqlManager);

	/**
	 * 注销指定的 SQLManager 实例
	 * @param name 实例名称
	 */
	void unregisterSQLManager(@NotNull String name);

	/**
	 * 注销指定命名空间下的所有 SQLManager 实例
	 * @param namespace 命名空间
	 */
	void unregisterSQLManagerAll(@NotNull String namespace);

}
