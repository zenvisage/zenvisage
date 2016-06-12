package edu.uiuc.zenvisage.zqlcomplete.executor;

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
public class PSQLDatabase {
	
	/**
	 * Settings specific to local PSQL database, need to change this!!!!
	 */
	private String database = "postgres";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "postgres";
	private String password = "";
	Connection c = null;
	
	// Initialize connection
	public PSQLDatabase() {
	      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection(host, username, password);
		      } catch (Exception e) {
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
	      System.out.println("Opened database successfully");
	}
	
	// Query database and return result
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      return stmt.executeQuery(sQLQuery);
	}
}
