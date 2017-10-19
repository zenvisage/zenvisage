package edu.uiuc.zenvisage.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.api.Readconfig;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.service.ZvMain;

public class DatabaseAutoLoader {

	private static String metatable;
	private static String metafilelocation;
	private ZvServer zvServer;
	Boolean reload = true;
	static{
		metatable=Readconfig.getMetatable();
		metafilelocation=Readconfig.getMetafilelocation();
	}
	
	public DatabaseAutoLoader(ZvServer zvServer){
		this.zvServer = zvServer;
	}
	public boolean createMetaTables() throws SQLException{
		
		SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();

		//clean everything
//		String dropPublicSchemaSQL = "DROP schema public cascade;";
//		sqlQueryExecutor.executeUpdate(dropPublicSchemaSQL);
//
		if(!sqlQueryExecutor.isTableExists(metatable) ){
			String createPublicSchemaSQL = "CREATE schema public;";
			String createMetaTableSQL = "CREATE TABLE zenvisage_metafilelocation "
					+ "(database TEXT, metafilelocation TEXT, csvfilelocation TEXT); "
					+ "CREATE TABLE zenvisage_metatable (tablename TEXT, attribute TEXT, "
					+ "type TEXT, axis TEXT, min FLOAT, max FLOAT, "
					+ "selectedX BOOLEAN, selectedY BOOLEAN, selectedZ BOOLEAN);";
			sqlQueryExecutor.executeUpdate(createPublicSchemaSQL);
			sqlQueryExecutor.createTable(createMetaTableSQL);
			reload = true;
		}

		if(!sqlQueryExecutor.isTableExists(metafilelocation)){
			String createMetaFileLocationSQL ="CREATE TABLE zenvisage_metafilelocation "
					+ "(database TEXT, metafilelocation TEXT, csvfilelocation TEXT);";
			sqlQueryExecutor.createTable(createMetaFileLocationSQL);
			reload = true;
		}

		if(!sqlQueryExecutor.isTableExists("zenvisage_dynamic_classes")){
			String createDynamicClassesSQL ="CREATE TABLE zenvisage_dynamic_classes "
					+ "(tablename TEXT, attribute TEXT, ranges TEXT);";
			sqlQueryExecutor.createTable(createDynamicClassesSQL);
			reload = true;
		}
		
		if(!sqlQueryExecutor.isTableExists("dynamic_class_aggregations")){
			String createDynamicClassesSQL ="CREATE TABLE dynamic_class_aggregations "
					+ "(Table_Name TEXT NOT NULL, Tag TEXT NOT NULL, Attributes TEXT NOT NULL, "
					+ "Ranges TEXT NOT NULL, Count INT NOT NULL);";
			sqlQueryExecutor.createTable(createDynamicClassesSQL);
			reload = true;
		}
		return reload;

	}

	public void loadDemoDatasets() throws SQLException, IOException, InterruptedException{
		// URL folderURL = zvServer.getClass().getClassLoader().getResource(("data"));
		// if(folderURL == null){
		// 	System.out.println("Data folder not exists, abort auto loader.");
		// 	return;
		// }else{
		// 	System.out.println("Data folder route found: " + folderURL.toString());
		// }

		List<String> dataset1 = new ArrayList<String>(); // real_estate
		dataset1.add("real_estate");
		File file = new File(zvServer.getClass().getClassLoader().
				getResource(("real_estate.csv")).getFile());
		dataset1.add(file.getAbsolutePath());
		file = new File(zvServer.getClass().getClassLoader().getResource(("real_estate.txt")).getFile());
		dataset1.add(file.getAbsolutePath());

		List<String> dataset2 = new ArrayList<String>(); //weather
		dataset2.add("weather");
		file = new File(zvServer.getClass().getClassLoader().getResource(("weather.csv")).getFile());
		dataset2.add(file.getAbsolutePath());
		file = new File(zvServer.getClass().getClassLoader().getResource(("weather.txt")).getFile());
		dataset2.add(file.getAbsolutePath());

		List<String> dataset3 = new ArrayList<String>(); //flights
		dataset3.add("flights");
		file = new File(zvServer.getClass().getClassLoader().getResource(("flights.csv")).getFile());
		dataset3.add(file.getAbsolutePath());
		file = new File(zvServer.getClass().getClassLoader().getResource(("flights.txt")).getFile());
		dataset3.add(file.getAbsolutePath());

		List<String> dataset4 = new ArrayList<String>(); //cmu
		dataset4.add("cmu");
		file = new File(zvServer.getClass().getClassLoader().getResource(("cmu_clean.csv")).getFile());
		dataset4.add(file.getAbsolutePath());
		file = new File(zvServer.getClass().getClassLoader().getResource(("cmu_clean.txt")).getFile());
		dataset4.add(file.getAbsolutePath());


		List<String> dataset5 = new ArrayList<String>(); //cmu
		dataset5.add("real_estate_tutorial");
		file = new File(zvServer.getClass().getClassLoader().
				getResource(("real_estate_tutorial.csv")).getFile());
		dataset5.add(file.getAbsolutePath());
		file = new File(zvServer.getClass().getClassLoader().getResource(("real_estate.txt")).getFile());
		dataset5.add(file.getAbsolutePath());
		ZvMain zvMain=new ZvMain();
		zvMain.uploadDatasettoDB(dataset1,false);
		zvMain.uploadDatasettoDB(dataset2,false);
		zvMain.uploadDatasettoDB(dataset3,false);
		zvMain.uploadDatasettoDB(dataset4,false);
		zvMain.uploadDatasettoDB(dataset5,false);
	}

	public void run() throws SQLException, IOException, InterruptedException{
		boolean reload = createMetaTables();
		if(reload) loadDemoDatasets();
	}

}
