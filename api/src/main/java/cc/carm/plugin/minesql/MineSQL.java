package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.api.SQLRegistry;

import java.io.File;
import java.util.logging.Logger;

public class MineSQL {

    private static IMineSQL instance;

    protected static void initializeAPI(IMineSQL api) {
        MineSQL.instance = api;
    }

    public static Logger getLogger() {
        return instance.getLogger();
    }

    public static SQLRegistry getRegistry() {
        return instance.getRegistry();
    }

    /**
     * @return 数据库源文件所在目录，非插件数据目录。
     */
    public static File getDataSourceFolder() {
        return instance.getSourceFolder();
    }

}
