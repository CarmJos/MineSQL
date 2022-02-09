package cc.carm.plugin.easysql;

import cc.carm.lib.easyplugin.EasyPlugin;
import cc.carm.plugin.easysql.api.DBConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EasySQLBukkit extends EasyPlugin implements EasySQLPluginPlatform {

    @Override
    protected void load() {

    }

    @Override
    protected boolean initialize() {
        return false;
    }

    @Override
    public @NotNull Map<String, DBConfiguration> readConfigurations() {
        return new HashMap<>();
    }

    @Override
    public void outputInfo() {
        log("\n" +
                "&5&l ______                 _____  ____  _        &d&l_____  _             _       \n" +
                "&5&l|  ____|               / ____|/ __ \\| |      &d&l|  __ \\| |           (_)      \n" +
                "&5&l| |__   __ _ ___ _   _| (___ | |  | | |      &d&l| |__) | |_   _  __ _ _ _ __  \n" +
                "&5&l|  __| / _` / __| | | |\\___ \\| |  | | |      &d&l|  ___/| | | | |/ _` | | '_ \\ \n" +
                "&5&l| |___| (_| \\__ \\ |_| |____) | |__| | |____  &d&l| |    | | |_| | (_| | | | | |\n" +
                "&5&l|______\\__,_|___/\\__, |_____/ \\___\\_\\______| &d&l|_|    |_|\\__,_|\\__, |_|_| |_|\n" +
                "&5&l                  __/ |                                      &d&l __/ |        \n" +
                "&5&l                 |___/                                       &d&l|___/         "
        );
    }

}
