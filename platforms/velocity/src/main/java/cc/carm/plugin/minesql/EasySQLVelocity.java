package cc.carm.plugin.minesql;


import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.logging.Logger;


@Plugin(id = "easysql-plugin", name = "EasySQL-Plugin",
        version = "1.0.0",
        description = "EasySQL Plugin For Velocity",
        url = "https://github.com/CarmJos/EasySQL-Plugin",
        authors = {"CarmJos", "GhostChu"}
)
public class EasySQLVelocity {

    private static EasySQLVelocity instance;

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public EasySQLVelocity(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;
        // register listeners
        server.getEventManager().register(this, this);
    }

    public static EasySQLVelocity getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
    }

}
