package edu.uiuc.zenvisage.server;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
		ZvServer zvServer = new ZvServer();
		zvServer.loadDemoDatasets();
		zvServer.start();	
	}	
	
	public  static void createMetaTables() throws SQLException{
		SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
		if(!sqlQueryExecutor.isTableExists(metatable)){
			String dropPublicSchemaSQL = "DROP schema public cascade;";
			String createPublicSchemaSQL = "CREATE schema public;";
			String createMetaTableSQL = "CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT, min FLOAT, max FLOAT);";
			sqlQueryExecutor.executeUpdate(dropPublicSchemaSQL);
			sqlQueryExecutor.executeUpdate(createPublicSchemaSQL);
			sqlQueryExecutor.createTable(createMetaTableSQL);			
		}
		
		if(!sqlQueryExecutor.isTableExists(metafilelocation)){
			String createMetaFileLocationSQL ="CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);";
			sqlQueryExecutor.createTable(createMetaFileLocationSQL);		
		}
	}
	
	
	public void loadDemoDatasets() throws SQLException, IOException, InterruptedException{
		List<String> dataset1 = new ArrayList<String>(); // real_estate
		dataset1.add("real_estate");
		File file = new File(this.getClass().getClassLoader().getResource(("real_estate.csv")).getFile());
		dataset1.add(file.getAbsolutePath());
		file = new File(this.getClass().getClassLoader().getResource(("real_estate.txt")).getFile());
		dataset1.add(file.getAbsolutePath());

		List<String> dataset2 = new ArrayList<String>(); //weather
		dataset2.add("weather");
		file = new File(this.getClass().getClassLoader().getResource(("weather.csv")).getFile());
		dataset2.add(file.getAbsolutePath());
		file = new File(this.getClass().getClassLoader().getResource(("weather.txt")).getFile());
		dataset2.add(file.getAbsolutePath());
		
		List<String> dataset3 = new ArrayList<String>(); //flights
		dataset3.add("flights");
		file = new File(this.getClass().getClassLoader().getResource(("flights_dt.csv")).getFile());
		dataset3.add(file.getAbsolutePath());
		file = new File(this.getClass().getClassLoader().getResource(("flights_dt.txt")).getFile());
		dataset3.add(file.getAbsolutePath());
		
		List<String> dataset4 = new ArrayList<String>(); //cmu
		dataset4.add("cmu");
		file = new File(this.getClass().getClassLoader().getResource(("cmu_clean.csv")).getFile());
		dataset4.add(file.getAbsolutePath());
		file = new File(this.getClass().getClassLoader().getResource(("cmu_clean.txt")).getFile());
		dataset4.add(file.getAbsolutePath());
		
				
		//		List<String> dataset3 = new ArrayList<String>(); //flight
//		List<String> dataset4 = new ArrayList<String>(); //cmu
				
//		ZvMain.uploadDatasettoDB(dataset1,false);
//		ZvMain.uploadDatasettoDB(dataset2,false);
		ZvMain.uploadDatasettoDB(dataset3,false);
//		ZvMain.uploadDatasettoDB(dataset4,false);
	}

}
