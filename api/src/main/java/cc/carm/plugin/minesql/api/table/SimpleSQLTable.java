package cc.carm.plugin.minesql.api.table;

import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateAction;
import cc.carm.lib.easysql.api.action.PreparedSQLUpdateBatchAction;
import cc.carm.lib.easysql.api.builder.*;
import cc.carm.lib.easysql.api.function.SQLHandler;
import cc.carm.plugin.minesql.MineSQL;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

public class SimpleSQLTable {

    public static @NotNull SimpleSQLTable of(@NotNull String tableName,
                                             @NotNull SQLHandler<TableCreateBuilder> tableBuilder) {
        return new SimpleSQLTable(null, tableName, null, tableBuilder);
    }

    public static @NotNull SimpleSQLTable of(@Nullable String database, @NotNull String tableName,
                                             @NotNull SQLHandler<TableCreateBuilder> tableBuilder) {
        return new SimpleSQLTable(database, tableName, null, tableBuilder);
    }

    public static @NotNull SimpleSQLTable of(@Nullable String database, @NotNull String tableName,
                                             @Nullable String tablePrefix, @NotNull SQLHandler<TableCreateBuilder> tableBuilder) {
        return new SimpleSQLTable(database, tableName, () -> tablePrefix, tableBuilder);
    }

    public static @NotNull SimpleSQLTable of(@Nullable String database, @NotNull String tableName,
                                             @Nullable Supplier<String> tablePrefix, @NotNull SQLHandler<TableCreateBuilder> tableBuilder) {
        return new SimpleSQLTable(database, tableName, tablePrefix, tableBuilder);
    }

    protected final @Nullable String database;
    protected final @NotNull String tableName;

    protected final @Nullable Supplier<String> tablePrefix;
    protected final @NotNull SQLHandler<TableCreateBuilder> tableCreator;

    public SimpleSQLTable(@Nullable String database, @NotNull String tableName,
                          @Nullable Supplier<String> tablePrefix, @NotNull SQLHandler<TableCreateBuilder> table) {
        this.database = database;
        this.tableName = tableName;
        this.tablePrefix = tablePrefix;
        this.tableCreator = table;
    }

    public boolean create() throws SQLException {
        SQLManager sqlManager = getSQLManager();
        if (sqlManager == null) throw new SQLException(getExceptionReason());

        TableCreateBuilder tableBuilder = sqlManager.createTable(getTableName());
        tableCreator.accept(tableBuilder);
        return tableBuilder.build().executeFunction(l -> l > 0, false);
    }

    public @Nullable String getDatabase() {
        return database;
    }

    public @Nullable SQLManager getSQLManager() {
        return MineSQL.getRegistry().get(getDatabase());
    }

    public @NotNull String getTableName() {
        String prefix = getTablePrefix();
        return (prefix != null ? prefix : "") + tableName;
    }

    public @Nullable String getTablePrefix() {
        return Optional.ofNullable(tablePrefix).map(Supplier::get).orElse(null);
    }

    public @NotNull TableQueryBuilder createQuery() {
        return Optional.ofNullable(getSQLManager()).map(this::createQuery)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull TableQueryBuilder createQuery(@NotNull SQLManager sqlManager) {
        return sqlManager.createQuery().inTable(getTableName());
    }

    public @NotNull DeleteBuilder createDelete() {
        return Optional.ofNullable(getSQLManager()).map(this::createDelete)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull DeleteBuilder createDelete(@NotNull SQLManager sqlManager) {
        return sqlManager.createDelete(getTableName());
    }

    public @NotNull UpdateBuilder createUpdate() {
        return Optional.ofNullable(getSQLManager()).map(this::createUpdate)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull UpdateBuilder createUpdate(@NotNull SQLManager sqlManager) {
        return sqlManager.createUpdate(getTableName());
    }

    public @NotNull InsertBuilder<PreparedSQLUpdateAction<Integer>> createInsert() {
        return Optional.ofNullable(getSQLManager()).map(this::createInsert)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull InsertBuilder<PreparedSQLUpdateAction<Integer>> createInsert(@NotNull SQLManager sqlManager) {
        return sqlManager.createInsert(getTableName());
    }

    public @NotNull InsertBuilder<PreparedSQLUpdateBatchAction<Integer>> createInsertBatch() {
        return Optional.ofNullable(getSQLManager()).map(this::createInsertBatch)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull InsertBuilder<PreparedSQLUpdateBatchAction<Integer>> createInsertBatch(@NotNull SQLManager sqlManager) {
        return sqlManager.createInsertBatch(getTableName());
    }

    public @NotNull ReplaceBuilder<PreparedSQLUpdateAction<Integer>> createReplace() {
        return Optional.ofNullable(getSQLManager()).map(this::createReplace)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));

    }

    public @NotNull ReplaceBuilder<PreparedSQLUpdateAction<Integer>> createReplace(@NotNull SQLManager sqlManager) {
        return sqlManager.createReplace(getTableName());
    }

    public @NotNull ReplaceBuilder<PreparedSQLUpdateBatchAction<Integer>> createReplaceBatch() {
        return Optional.ofNullable(getSQLManager()).map(this::createReplaceBatch)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull ReplaceBuilder<PreparedSQLUpdateBatchAction<Integer>> createReplaceBatch(@NotNull SQLManager sqlManager) {
        return sqlManager.createReplaceBatch(getTableName());
    }

    public @NotNull TableAlterBuilder alter() {
        return Optional.ofNullable(getSQLManager()).map(this::alter)
                .orElseThrow(() -> new NullPointerException(getExceptionReason()));
    }

    public @NotNull TableAlterBuilder alter(@NotNull SQLManager sqlManager) {
        return sqlManager.alterTable(getTableName());
    }

    private String getExceptionReason() {
        if (getDatabase() == null) return "Cannot find any SQLManager.";
        else return "Cannot find any SQLManager for \"" + getDatabase() + "\".";
    }

}
