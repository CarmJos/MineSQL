package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.EasySQLRegistry;

public class EasySQLAPI {

    protected static EasySQLRegistry api;

    protected static void init(EasySQLRegistry api) {
        EasySQLAPI.api = api;
    }

    public static EasySQLRegistry get() {
        return api;
    }

}
