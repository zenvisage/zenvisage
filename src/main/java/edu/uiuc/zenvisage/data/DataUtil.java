/**
 * 
 */
package edu.uiuc.zenvisage.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;


/**
 * @author tarique
 *
 */
public class DataUtil {

	private Database inMemoryDatabase;
	private Map<String,Database> inMemoryDatabases = new HashMap<String,Database>();
	
	public  void loadData() throws IOException, InterruptedException{
		
		inMemoryDatabase = createDatabase("income","src/data/census_test_schema.txt","src/data/census-income-test.csv");
		inMemoryDatabases.put("income", inMemoryDatabase);
		inMemoryDatabase = createDatabase("real_estate","src/data/real_estate.txt","src/data/real_estate.csv");
		inMemoryDatabases.put("real_estate", inMemoryDatabase);
		inMemoryDatabase = createDatabase("iris", "src/data/iris_schema.txt","src/data/iris_data.csv");
		inMemoryDatabases.put("iris", inMemoryDatabase);
		/*
		inMemoryDatabase = DataLoader.createDatabase("seed", "src/data/seed_schema.txt", "src/data/seed.csv");
		inMemoryDatabases.put("seed", inMemoryDatabase);
		inMemoryDatabase = DataLoader.createDatabase("seed2", "src/data/seed2_schema.txt", "src/data/seed2.csv");
		inMemoryDatabases.put("seed2", inMemoryDatabase);
		inMemoryDatabase = DataLoader.createDatabase("seed3", "src/data/seed3_schema.txt", "src/data/seed3.csv");
		inMemoryDatabases.put("seed3", inMemoryDatabase);*/
	}
		
	
	 
    public static Database createDatabase(String name,String schemafile,String datafile) throws IOException, InterruptedException{
    	Database database = new Database(name,schemafile,datafile);
    	return database;
 
    }

	
	
	
}
