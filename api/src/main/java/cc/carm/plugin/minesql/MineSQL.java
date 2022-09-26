package cc.carm.plugin.minesql;

import cc.carm.plugin.minesql.api.SQLRegistry;

public class MineSQL {

    protected static SQLRegistry api;

    protected static void initializeAPI(SQLRegistry api) {
        MineSQL.api = api;
    }

    public static SQLRegistry get() {
        return api;
    }

}
