package cc.carm.plugin.easysql;


import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.logging.Logger;

@Plugin(id = "easysql-plugin", name = "EasySQL Plugin For Velocity", version = "1.0.0",
		description = "",
		url = "https://github.com/CarmJos/EasySQL-Plugin", authors = "CarmJos"
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


}
