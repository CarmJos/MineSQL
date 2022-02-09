package cc.carm.plugin.easysql;

import cc.carm.plugin.easysql.api.EasySQLManager;

public class EasySQLAPI {

    protected static EasySQLManager api;

    protected static void init(EasySQLManager api) {
        EasySQLAPI.api = api;
    }

    public static EasySQLManager get() {
        return api;
    }

}
