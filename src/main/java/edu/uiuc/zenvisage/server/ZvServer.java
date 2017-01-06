package edu.uiuc.zenvisage.server;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.service.ZvMain;

public class ZvServer {

	private Server server;
	private static int port = 8080;
	private static String metatable="zenvisage_metatable";
	private static String metafilelocation="zenvisage_metafilelocation";
	

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
		createMetaTables();
		loadDemoDatasets();
		ZvServer zvServer = new ZvServer();
		zvServer.start();	
	}
	
	public  static void createMetaTables() throws SQLException{
		SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
		if(!sqlQueryExecutor.isTableExists(metatable)){
			String dropPublicSchemaSQL = "DROP schema public cascade;";
			String createPublicSchemaSQL = "CREATE schema public;";
			String createMetaTableSQL = "CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);";
			sqlQueryExecutor.executeUpdate(dropPublicSchemaSQL);
			sqlQueryExecutor.executeUpdate(createPublicSchemaSQL);
			sqlQueryExecutor.createTable(createMetaTableSQL);			
		}
		
		if(!sqlQueryExecutor.isTableExists(metafilelocation)){
			String createMetaFileLocationSQL ="CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);";
			sqlQueryExecutor.createTable(createMetaFileLocationSQL);		
		}
	}
	
	
	public static void loadDemoDatasets() throws SQLException, IOException{
		List<String> dataset1 = new ArrayList<String>(); // real_estate
		
		//Add values to the list here -- filepath, csv, txt  :same for datasets below
//		List<String> dataset2 = new ArrayList<String>(); //weather
//		List<String> dataset3 = new ArrayList<String>(); //flight
//		List<String> dataset4 = new ArrayList<String>(); //cmu
				
		ZvMain.uploadDatasettoDB(dataset1,false);
//		ZvMain.uploadDatasettoDB(dataset2);
//		ZvMain.uploadDatasettoDB(dataset3);
//		ZvMain.uploadDatasettoDB(dataset4);
	}

}
