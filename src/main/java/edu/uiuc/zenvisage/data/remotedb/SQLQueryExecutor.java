package edu.uiuc.zenvisage.data.remotedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PostgreSQL database connection portal for my local machine
 * need to change to in general
 *
 */
public class SQLQueryExecutor {
	
	/**
	 * Settings specific to local PSQL database, need to change this!!!!
	 */
	private String database = "postgres";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "postgres";
	private String password = "";
	Connection c = null;
	
	// Initialize connection
	public SQLQueryExecutor() {
	      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection(host, username, password);
		      } catch (Exception e) {
		    	 System.out.println("Connection Failed! Check output console");
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
	      System.out.println("Opened database successfully");
	}
	
	// Query database and return result
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      ResultSet ret = stmt.executeQuery(sQLQuery);
	      stmt.close();
	      return ret;
	}
	
	public int createTable(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      int ret = stmt.executeUpdate(sQLQuery);
	      stmt.close();
	      return ret;
	}
	
	public void dropTable(String tableName) throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "DROP TABLE " + tableName;
	    stmt.executeUpdate(sql);
	    System.out.println("Table " + tableName + " deleted in given database...");
	    stmt.close();
	}	
	
	public static void main(String[] args){
		SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
		try {
			sqlQueryExecutor.dropTable("COMPANY");
			sqlQueryExecutor.createTable("CREATE TABLE COMPANY " +
	                "(ID INT PRIMARY KEY     NOT NULL," +
	                " NAME           TEXT    NOT NULL, " +
	                " AGE            INT     NOT NULL, " +
	                " ADDRESS        CHAR(50), " +
	                " SALARY         REAL)");
			sqlQueryExecutor.query("SELECT * FROM COMPANY");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}