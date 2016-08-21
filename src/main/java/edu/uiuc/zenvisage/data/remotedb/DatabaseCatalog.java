package edu.uiuc.zenvisage.data.remotedb;

import java.util.HashMap;
import java.util.Map;

public class DatabaseCatalog {
	static private Map<String,QueryBasedInMemoryDatabase> databases = new HashMap<String,QueryBasedInMemoryDatabase>();
	
	static public void addDatabase(String name,QueryBasedInMemoryDatabase database){
		databases.put(name, database);
	}
	
	static public QueryBasedInMemoryDatabase getDatabase(String name){
		return databases.get(name);		
	}
}
