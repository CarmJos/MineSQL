package cc.carm.plugin.minesql.api.table;

import cc.carm.lib.easysql.api.function.SQLHandler;

import java.util.function.Supplier;

/**
 * 表声明类的根节点，用于标注该类用于记录表的结构信息。
 * <br> 创建表请使用 {@link SimpleSQLTable#of(String, String, Supplier, SQLHandler)}。
 */
public abstract class SQLTablesRoot {
}
