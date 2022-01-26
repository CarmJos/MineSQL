package cc.carm.plugin.easysql.velocity;


import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.logging.Logger;

@Plugin(id = "easysql-plugin", name = "EasySQL Plugin For Velocity", version = "1.0.0",
		description = "",
		url = "https://github.com/CarmJos/EasySQL-Plugin", authors = "CarmJos"
)
public class EasySQLPlugin {

	private static EasySQLPlugin instance;

	private final ProxyServer server;
	private final Logger logger;

	@Inject
	public EasySQLPlugin(ProxyServer server, Logger logger) {
		instance = this;
		this.server = server;
		this.logger = logger;

	}


	public static EasySQLPlugin getInstance() {
		return instance;
	}

	public ProxyServer getServer() {
		return server;
	}

	public Logger getLogger() {
		return logger;
	}


}
