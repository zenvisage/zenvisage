package edu.uiuc.zenvisage.data.roaringdb.db;

import java.util.HashMap;
import java.util.Map;

public class DatabaseCatalog {
	static private Map<String,Database> databases = new HashMap<String,Database>();
	
	static public void addDatabase(String name,Database database){
		databases.put(name, database);
	}
	
	static public Database getDatabase(String name){
		return databases.get(name);		
	}
	

}
