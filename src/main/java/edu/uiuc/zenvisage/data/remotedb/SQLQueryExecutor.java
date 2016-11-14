package edu.uiuc.zenvisage.data.remotedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.postgresql.util.PSQLException;

import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
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
	private String password = "zenvisage";
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
			sql = "SELECT " + Z + "," + X + "," + "avg(" + Y + ")"
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
				tempVisualComponent = new VisualComponent(zValue, new Points(xList, yList), X, Y);
				this.visualComponentList.addVisualComponent(tempVisualComponent);
			}

		}

		/* Testing below */
        //System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
		rs.close();
		st.close();
	}
	
	/*This is the main ZQL->SQLExcecution query*/
	public void ZQLQueryEnhanced(ZQLRow zqlRow, String databaseName) throws SQLException{
		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());

		String sql = null;
		
		databaseName = databaseName.toLowerCase();
		String z = zqlRow.getZ().getAttribute().toLowerCase().replaceAll("'", "").replaceAll("\"", "");
		String agg = zqlRow.getViz().getVariable().toLowerCase().replaceAll("'", "").replaceAll("\"", "");
		
		
		//support list of x, y values, general all possible x,y combinations, generate sql
		int xLen = zqlRow.getX().getAttributes().size();
		int yLen = zqlRow.getY().getAttributes().size();
		for(int i = 0; i < xLen; i++){
			for(int j = 0; j < yLen; j++){
				String x = zqlRow.getX().getAttributes().get(i).toLowerCase().replaceAll("'", "").replaceAll("\"", "");
				String y = zqlRow.getY().getAttributes().get(j).toLowerCase().replaceAll("'", "").replaceAll("\"", "");
				
				//zqlRow.getConstraint() has replaced the whereCondiditon
				if (zqlRow.getConstraint() == null || zqlRow.getConstraint().size() == 0) {
					sql = "SELECT " + z + "," + x + " ," + agg + "(" + y + ")" //zqlRow.getViz() should replace the avg() function
							+ " FROM " + databaseName
							+ " GROUP BY " + z + ", "+ x
							+ " ORDER BY " + z + ", "+ x;
				} else {
					
					sql = "SELECT " + z+ "," + x + " ," + agg + "(" + y + ")"
					+ " FROM " + databaseName
					+ " WHERE " + appendConstraints(zqlRow.getConstraint()) //zqlRow.getConstraint() has replaced the whereCondiditon
					+ " GROUP BY " + z + ", "+ x
					+ " ORDER BY " + z + ", "+ x;
				}
			
				System.out.println("Running ZQL Query :"+sql);
				//excecute sql and put into VisualComponentList
				executeSQL(sql, zqlRow, databaseName, x, y);
			}
		}


		/* Testing below */
        //System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
	}
	
	public void executeSQL(String sql, ZQLRow zqlRow, String databaseName, String x, String y) throws SQLException{
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		
		
		//this.visualComponentList = new VisualComponentList();
		//this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());
		
		WrapperType zValue = null;
		ArrayList <WrapperType> xList = null;
		ArrayList <WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;
		
		String zType = null, xType = null, yType = null;
		while (rs.next())
		{
			if(zType == null) zType = getMetaType(zqlRow.getZ().getAttribute().toLowerCase(), databaseName);
			if(xType == null) xType = getMetaType(x, databaseName);	// uses the x and y that have extra stuff like '' removed
			if(yType == null) yType = getMetaType(y, databaseName);

			WrapperType tempZValue = new WrapperType(rs.getString(1), zType);

			if(tempZValue.equals(zValue)){
				xList.add(new WrapperType(rs.getString(2), xType));
				yList.add(new WrapperType(rs.getString(3), yType));
			} else {
				zValue = tempZValue;
				xList = new ArrayList<WrapperType>();
				yList = new ArrayList<WrapperType>();
				xList.add(new WrapperType(rs.getString(2), xType));
				yList.add(new WrapperType(rs.getString(3), yType));
				tempVisualComponent = new VisualComponent(zValue, new Points(xList, yList), x, y);
				this.visualComponentList.addVisualComponent(tempVisualComponent);
			}

		}
		rs.close();
		st.close();
	}
	
	
	
	/**
	 * @param constraint
	 * @return
	 */
	private String appendConstraints(List<Constraints> constraints) {
		// TODO Auto-generated method stub
		String appendedConstraints = "";
		boolean flag=false;
		for(Constraints constraint: constraints){
			if(flag){
				appendedConstraints+=" OR "; // TODO: Support OR AND AND constraints
			}
			appendedConstraints+=constraint.toString();
			flag=true;
		}
		appendedConstraints+=" ";
		
		return appendedConstraints;
	}

	public String getMetaType(String variable, String table) throws SQLException{
		Statement st = c.createStatement();
		String sql = null;
		sql = "SELECT " + "type"
			+ " FROM " + "zenvisage_metatable"
			+ " WHERE " + "tablename = '" + table
			+ "' AND attribute = '" + variable + "'";
		System.out.println(sql);
		ResultSet rs = st.executeQuery(sql);
		while (rs.next())
		{
			return rs.getString(1);
		}
		return null;
	}
	
	public String[] getMetaFileLocation(String database) throws SQLException {
		Statement st = c.createStatement();
 		String sql = null;	
 		sql = "SELECT " + "metafilelocation, "+"csvfilelocation"
 			+ " FROM " + "zenvisage_metafilelocation"
 			+ " WHERE " + "database = '" + database + "'";
 		System.out.println(sql);
 		ResultSet rs = st.executeQuery(sql);
 		while (rs.next())
 		{
 			System.out.println( rs.getString(1) + "\n" + rs.getString(2));
 			return new String[]{ rs.getString(1), rs.getString(2)};
 		}
 		return null;
 	}
	
	public boolean insert(String sql, String tablename, String tablenameVariable, String databasename) throws SQLException{
		int count = 0;
		Statement st0 = c.createStatement();
		tablename = tablename.toLowerCase();
		databasename = databasename.toLowerCase();
		String sql0 = "SELECT COUNT(*) FROM "
				+ tablename
	 			+ " WHERE " + tablenameVariable + " = '" + databasename + "'";

		ResultSet rs0 = st0.executeQuery(sql0);
		
		//if database already exist return false;
		while (rs0.next())
 		{
			if(Integer.parseInt(rs0.getString(1))>0) return false;
 		}
		
		Statement st = c.createStatement();
	
		System.out.println(sql);
		count = st.executeUpdate(sql);

		return count > 0;
	}
	
	public boolean isTableExists(String tableName) throws SQLException{
		Statement st0 = c.createStatement();
		String sql0 = "select tablename from pg_tables where schemaname='public'";
		ResultSet rs0 = st0.executeQuery(sql0);
		while (rs0.next())
 		{
			if(rs0.getString(1).equals(tableName)) return true;
 		}
		return false;
	}
	
	public void insertTable(String tableName, String fileName, List<String> columns) throws SQLException{
		StringBuilder sql = new StringBuilder("COPY "+ tableName + "(");
		for(String s:columns){
			sql.append(s+",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") FROM '"+ fileName +"' DELIMITER ',' CSV HEADER;");
	    Statement stmt = c.createStatement();
	    stmt.executeUpdate(sql.toString());
	    stmt.close();
	}
	
	public static void main(String[] args) throws SQLException{
		SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
		try {
			sqlQueryExecutor.dropTable("COMPANY");
			
			//sqlQueryExecutor.query("SELECT * FROM COMPANY");
			
			//sqlQueryExecutor.ZQLQuery("State", "Quarter", "SoldPrice", "real_estate", null);
			
			
		} catch (PSQLException e) {
			// TODO Auto-generated catch block
			sqlQueryExecutor.createTable("CREATE TABLE COMPANY " +
	                "(ID INT PRIMARY KEY     NOT NULL," +
	                " NAME           TEXT    NOT NULL, " +
	                " AGE            INT     NOT NULL, " +
	                " ADDRESS        CHAR(50), " +
	                " SALARY         REAL)");
		}
		List<Constraints> constraints = new ArrayList<Constraints>();
		List<String> xList = new ArrayList<String>();
		xList.add("quarter");xList.add("year");
		//ZQLRow zqlRow = new ZQLRow(new XColumn("Quarter"), new YColumn("SoldPrice"), new ZColumn("State"), constraints, new VizColumn("avg"));
		ZQLRow zqlRow = new ZQLRow(new XColumn(xList), new YColumn("soldprice"), new ZColumn("state"), constraints, new VizColumn("avg"));
		sqlQueryExecutor.ZQLQueryEnhanced(zqlRow, "real_estate");
	}

	public VisualComponentList getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(VisualComponentList visualComponentList) {
		this.visualComponentList = visualComponentList;
	}

}
