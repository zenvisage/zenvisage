package edu.uiuc.zenvisage.data.remotedb;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Test;

import edu.uiuc.zenvisage.service.ZvMain;

public class SQLQueryExecutorTest {
	private ZvMain zvMain = new ZvMain(); // This creates a SQLQueryExecutor automatically with the right path.
	private SQLQueryExecutor db = zvMain.sqlQueryExecutor;
	@Test
	public void connectionTest() {
		assertEquals(db.getClass().getName(),"edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor");
	}
	@Test
	public void simpleTableTest() throws SQLException  {
		db.executeUpdate("CREATE TABLE simpleTableTest( ID int, attr1 varchar(255));");
		db.dropTable("simpleTableTest");	 
	}
	@Test
	public void tablelistTest() throws SQLException  {
		ArrayList<String> listOfTables= db.gettablelist();
		System.out.print(listOfTables);
		listOfTables.contains("zenvisage_dynamic_classes");
		listOfTables.contains("zenvisage_metafilelocation");
		listOfTables.contains("users");
		listOfTables.contains("users_tables");
		listOfTables.contains("zenvisage_metatable");
	}
	
	
	
}
