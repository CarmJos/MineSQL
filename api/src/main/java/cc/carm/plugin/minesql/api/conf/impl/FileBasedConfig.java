package cc.carm.plugin.minesql.api.conf.impl;

import cc.carm.plugin.minesql.MineSQL;
import cc.carm.plugin.minesql.api.SQLDriverType;
import cc.carm.plugin.minesql.api.conf.SQLDriverConfig;
import cc.carm.plugin.minesql.api.source.SQLSourceConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileBasedConfig extends SQLDriverConfig {

    protected final @NotNull String filePath;

    public FileBasedConfig(@NotNull SQLDriverType type,
                           @NotNull String filePath) {
        super(type);
        this.filePath = filePath;
    }

    public @NotNull String getFilePath() {
        return filePath;
    }

    @Override
    public SQLSourceConfig createSource() {
        File file = new File(MineSQL.getDataSourceFolder(), filePath);
        return SQLSourceConfig.create(
                getType().getDriverClass(),
                getType().getJdbcPrefix() + file.getAbsolutePath(),
                getType().getInitializer()
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<>();

        values.put("type", getType().name());
        values.put("file", getFilePath());

        return values;
    }


}

