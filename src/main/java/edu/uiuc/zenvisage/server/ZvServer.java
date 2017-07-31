package edu.uiuc.zenvisage.server;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import edu.uiuc.zenvisage.api.Readconfig;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.service.ZvMain;

public class ZvServer {

	private Server server;
	private static int port;	
	
	static{
		port = Readconfig.getPort();
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws Exception {	
		server = new Server(port);	
		
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("zenvisage.war");
		webAppContext.setParentLoaderPriority(true);
		webAppContext.setServer(server);
		webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
		webAppContext.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webAppContext);	
		server.start();
//		ZvMain zvMain = (ZvMain) SpringApplicationContext.getBean("zvMain");
//		zvMain.loadData();
		
		DatabaseAutoLoader databaseAutoLoader = new DatabaseAutoLoader(this);
		databaseAutoLoader.run();
	
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ZvServer zvServer = new ZvServer();
		zvServer.start();	
	}	

}
