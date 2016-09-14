package edu.uiuc.zenvisage.data.remotedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;


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
	private VisualComponentList visualComponentList;
	
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
	
	public void ZQLQuery(String Z, String X, String Y, String table, String whereCondition) throws SQLException{
		Statement st = c.createStatement();
		String sql = null;	
		
		if (whereCondition == null) {
			sql = "SELECT " + Z + "," + X + " ," + "avg(" + Y + ")"
					+ " FROM " + table
					+ " GROUP BY " + Z + ", "+ X
					+ " ORDER BY " + Z + ", "+ X;
		} else {
			sql = "SELECT " + Z + "," + X
			+ " FROM " + table
			+ " WHERE " + whereCondition
			+ " GROUP BY " + Z + ", "+ X
			+ " ORDER BY " + Z + ", "+ X;
		}
		
		ResultSet rs = st.executeQuery(sql);
		System.out.println("Running ZQL Query ...");
		
		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());
		
		WrapperType zValue = null;
		ArrayList <WrapperType> xList = null;
		ArrayList <WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;
		
		while (rs.next())
		{
			
			WrapperType tempZValue = new WrapperType(rs.getString(1));

			if(tempZValue.equals(zValue)){
				xList.add(new WrapperType(rs.getString(2)));
				yList.add(new WrapperType(rs.getString(3)));
			} else {
				zValue = tempZValue;
				xList = new ArrayList<WrapperType>();
				yList = new ArrayList<WrapperType>();
				xList.add(new WrapperType(rs.getString(2)));
				yList.add(new WrapperType(rs.getString(3)));
				tempVisualComponent = new VisualComponent(zValue, new Points(xList, yList));
				this.visualComponentList.addVisualComponent(tempVisualComponent);
			}

		}

		/* Testing below */
        System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
		rs.close();
		st.close();
	}
	
	public void ZQLQueryEnhanced(ZQLRow zqlRow) throws SQLException{
		Statement st = c.createStatement();
		String sql = null;	
		
		
		//zqlRow.getConstraint() has replaced the whereCondiditon
		if (zqlRow.getConstraint() == null) {
			sql = "SELECT " + zqlRow.getZ() + "," + zqlRow.getX() + " ," + "avg(" + zqlRow.getY() + ")" //zqlRow.getViz() should replace the avg() function
					+ " FROM " + zqlRow.getName()
					+ " GROUP BY " + zqlRow.getZ() + ", "+ zqlRow.getX()
					+ " ORDER BY " + zqlRow.getZ() + ", "+ zqlRow.getX();
		} else {
			sql = "SELECT " + zqlRow.getZ() + "," + zqlRow.getX()
			+ " FROM " + zqlRow.getName()
			+ " WHERE " + zqlRow.getConstraint() //zqlRow.getConstraint() has replaced the whereCondiditon
			+ " GROUP BY " + zqlRow.getZ() + ", "+ zqlRow.getX()
			+ " ORDER BY " + zqlRow.getZ() + ", "+ zqlRow.getX();
		}
		
		ResultSet rs = st.executeQuery(sql);
		System.out.println("Running ZQL Query ...");
		
		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());
		
		WrapperType zValue = null;
		ArrayList <WrapperType> xList = null;
		ArrayList <WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;
		
		while (rs.next())
		{
			
			WrapperType tempZValue = new WrapperType(rs.getString(1));

			if(tempZValue.equals(zValue)){
				xList.add(new WrapperType(rs.getString(2)));
				yList.add(new WrapperType(rs.getString(3)));
			} else {
				zValue = tempZValue;
				xList = new ArrayList<WrapperType>();
				yList = new ArrayList<WrapperType>();
				xList.add(new WrapperType(rs.getString(2)));
				yList.add(new WrapperType(rs.getString(3)));
				tempVisualComponent = new VisualComponent(zValue, new Points(xList, yList));
				this.visualComponentList.addVisualComponent(tempVisualComponent);
			}

		}

		/* Testing below */
        System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
		rs.close();
		st.close();
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
			
			sqlQueryExecutor.ZQLQuery("State", "Quarter", "SoldPrice", "real_estate", null);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public VisualComponentList getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(VisualComponentList visualComponentList) {
		this.visualComponentList = visualComponentList;
	}
}