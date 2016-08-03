package edu.uiuc.zenvisage.server;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ZvServer {

	private Server server;
	private static int port = 8080;

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
