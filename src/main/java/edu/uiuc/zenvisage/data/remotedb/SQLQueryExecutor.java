package edu.uiuc.zenvisage.data.remotedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.postgresql.util.PSQLException;

import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.model.DynamicClass;
import edu.uiuc.zenvisage.api.Readconfig;
import edu.uiuc.zenvisage.model.ClassElement;
import java.util.Arrays;

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
	private String username;
	private String password;
	Connection c = null;
	public VisualComponentList visualComponentList;

	// Initialize connection
	public SQLQueryExecutor() {
		this.username = Readconfig.getUsername();
		this.password = Readconfig.getPassword();
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
	      
	      try {
	    	  Statement s = c.createStatement();
	    	  s.execute("SET SESSION work_mem = '200MB'");
	      } catch (SQLException e) {
	    	  System.out.println("Cannot change work_mem!");
	    	  e.printStackTrace();
	    	  System.exit(0);
	      }
	}

	// Query database and return result
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      ResultSet ret = stmt.executeQuery(sQLQuery);
	      //stmt.close();
	      return ret;
	}
	
	public int createTable(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      System.out.println(sQLQuery);
	      int ret = stmt.executeUpdate(sQLQuery);
	      stmt.close();
	      return ret;
	}
	
	public int executeUpdate(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      int ret = stmt.executeUpdate(sQLQuery);
	      stmt.close();
	      return ret;
	}

	public void dropTable(String tableName) throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "DROP TABLE " + tableName;
	    stmt.executeUpdate(sql);
//	    System.out.println("Table " + tableName + " deleted in given database...");
	    stmt.close();
	}
	
	
	public ArrayList<String> gettablelist() throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name != 'zenvisage_metatable' AND table_name != 'zenvisage_dynamic_classes' AND table_name != 'zenvisage_metafilelocation'";
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<String> tablelist = new ArrayList<String>();
		while ( rs.next() ) {
            String tablename = rs.getString("table_name");
            tablelist.add(tablename);
		}
        return tablelist;
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
//		System.out.println("Running ZQL Query ...");

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
		String sql = null;

		databaseName = databaseName.toLowerCase();
		String z = zqlRow.getZ().getAttribute().toLowerCase().replaceAll("'", "").replaceAll("\"", "");
		String agg = ((String) zqlRow.getViz().getMap().get(VizColumn.aggregation)).toLowerCase().replaceAll("\"", "");

		//support list of x, y values, general all possible x,y combinations, generate sql
		int xLen = zqlRow.getX().getAttributes().size();
		int yLen = zqlRow.getY().getAttributes().size();

		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());

		//clean Y attributes to use for query
		//if we have y1<-{'soldprice','listingprice'
		//this would build agg(soldprice),agg(listingprice),
		StringBuilder build = new StringBuilder();
		for(int j = 0; j < yLen; j++) {
			String cleanY = zqlRow.getY().getAttributes().get(j).toLowerCase().replaceAll("'", "").replaceAll("\"", "");
			build.append(agg);
			build.append("(");
			build.append(cleanY);
			build.append(")");
			build.append(",");
		}
		// remove extra ,
		build.setLength(build.length() - 1);
		
		for(int i = 0; i < xLen; i++){
			String x = zqlRow.getX().getAttributes().get(i).toLowerCase().replaceAll("'", "").replaceAll("\"", "");

			boolean hasZ = (z != null) && !z.equals("");
			//zqlRow.getConstraint() has replaced the whereCondiditon
			if (zqlRow.getConstraint() == null || zqlRow.getConstraint() =="") {
				sql = "SELECT " + (hasZ ? (z + "," + x) : ("1 as column1," + x) ) + "," + build.toString() //zqlRow.getViz() should replace the avg() function
						+ " FROM " + databaseName
						+ " GROUP BY " + (hasZ ? (z + "," + x) : x)
						+ " ORDER BY " + x;
			} else {

				sql = "SELECT " + (hasZ ? (z + "," + x) : x) + " ," + build.toString()
				+ " FROM " + databaseName
				+ " WHERE " + appendConstraints(zqlRow.getConstraint()) //zqlRow.getConstraint() has replaced the whereCondiditon
				+ " GROUP BY " + (hasZ ? (z + "," + x) : x)
				+ " ORDER BY " + x;
			}
			// for scatter plot queries
			if (agg.equals("")) {
				sql = "SELECT " + (hasZ ? (z + "," + x) : ("1 as column1," + x) ) + "," + build.toString() //zqlRow.getViz() should replace the avg() function
				+ " FROM " + databaseName
				+ " ORDER BY " + x;
			}
			
			System.out.println("Running ZQL Query :"+sql);
			//excecute sql and put into VisualComponentList
			executeSQL(sql, zqlRow, databaseName, x, zqlRow.getY().getAttributes());
		}


		/* Testing below */
        //System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
	}

	public void executeSQL(String sql, ZQLRow zqlRow, String databaseName, String x, List<String> yAttributes) throws SQLException{
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		
		System.out.println("Finished SQL Execution");

		WrapperType zValue = null;
		ArrayList <WrapperType> xList = null;
		ArrayList <WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;

		String zType = null, xType = null, yType = null;
		// Since we do not order by Z, we need a hashmap to keep track of all the visualcomponents
		// Since X is sorted though, the XList and YList are sorted correctly
		HashMap<String, List<VisualComponent>> vcMap = new HashMap<String, List<VisualComponent>>();
		sql_loop: while (rs.next())
		{
			if(rs.getString(1) == null || rs.getString(1).isEmpty()) continue;
			if(rs.getString(2) == null || rs.getString(2).isEmpty()) continue;
			
			if(zType == null) zType = getMetaType(zqlRow.getZ().getAttribute().toLowerCase(), databaseName);
			if(zType == null) zType = "string";	// if zAttribute is null, set the zType to be string
			if(xType == null) xType = getMetaType(x, databaseName);	// uses the x and y that have extra stuff like '' removed

			String zStr = rs.getString(1);
			List<VisualComponent> vcList = vcMap.get(zStr);
			
			// adding new x,y points to existing visual components
			if(vcList != null) {
				int rs_col_index = 3;
				// for loop populates vcList for a specific Z
				// So say we have x1<-{'year'} y1<-{'soldprice','listingprice'} Z='state'.'CA'
				// vcList for CA: (year,soldprice) , (year, listingprice) (in that exact order)
				for(int i = 0; i < yAttributes.size(); i++) {
					if (rs.getString(rs_col_index) == null || rs.getString(3).isEmpty()) {
						continue sql_loop;
					}
					VisualComponent vc = vcList.get(i);
					vc.getPoints().getXList().add(new WrapperType(rs.getString(2), xType));
					vc.getPoints().getYList().add(new WrapperType(rs.getString(rs_col_index)));	// don't get individual y meta types -- let WrapperType interpret the int, float, or string
					rs_col_index++;
				}
			}
			else {
				vcList = new ArrayList<VisualComponent>();
				int rs_col_index = 3;
				for(int i = 0; i < yAttributes.size(); i++) {
					if (rs.getString(rs_col_index) == null || rs.getString(3).isEmpty()) {
						continue sql_loop;
					}
					String yAtribute = yAttributes.get(i);
					// don't get individual y meta types -- let WrapperType interpret the int, float, or string
					xList = new ArrayList<WrapperType>();
					yList = new ArrayList<WrapperType>();
					xList.add(new WrapperType(rs.getString(2), xType));
					yList.add(new WrapperType(rs.getString(rs_col_index)));
					tempVisualComponent = new VisualComponent(new WrapperType(zStr, zType), new Points(xList, yList), x, yAtribute);
					vcList.add(tempVisualComponent);
					rs_col_index++;
				}
				vcMap.put(zStr, vcList);
			}

			
		}
		// will be in some unsorted order (b/c hashmap), which is fine
		// what is important is that have all VCs for one pair of X,Y first, then another pair of X,Y, and so on
		for(int i = 0; i < yAttributes.size(); i++) {
			for(List<VisualComponent> vcList: vcMap.values()) {
					this.visualComponentList.addVisualComponent(vcList.get(i));
			}		
		}
		rs.close();
		st.close();
		System.out.println(this.visualComponentList.getVisualComponentList().size() + " Visual Components Created");
	}



	/**
	 * @param constraint
	 * @return
	 */
	private String appendConstraints(String constraints) {
		// TODO Auto-generated method stub
		String appendedConstraints = "";
/*		boolean flag=false;
		for(Constraints constraint: constraints){
			if(flag){
				appendedConstraints+=" AND ";
			}
			appendedConstraints+=constraint.toString();
			flag=true;
		}*/
		appendedConstraints+=constraints;
		
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
//		System.out.println(sql);
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
// 		System.out.println(sql);
 		ResultSet rs = st.executeQuery(sql);
 		while (rs.next())
 		{
// 			System.out.println( rs.getString(1) + "\n" + rs.getString(2));
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

//		System.out.println(sql);
		count = st.executeUpdate(sql);

		return count > 0;
	}

	public boolean isTableExists(String tableName) throws SQLException{
		Statement st0 = c.createStatement();
		String sql0 = "select * from pg_tables where tablename = '" + tableName + "'";
		ResultSet rs0;
		try{
			 rs0 = st0.executeQuery(sql0);
		}
		catch(Exception PSQLException){

			return false;
		}
		while (rs0.next())
 		{
			if(rs0.getString(2).equals(tableName)) 
			{
				System.out.println(tableName +" already exists");	
				return true;
			}
			
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
	
	public void updateMinMax(String tableName, String attribute, float min, float max) throws SQLException{
		String sql = "UPDATE zenvisage_metatable"+ 
				" SET min = " + min + ", max = " + max +
				" WHERE tablename = '" + tableName + "' AND attribute = '" + attribute+"'";
		System.out.println(sql);
		Statement stmt = c.createStatement();
		stmt.executeUpdate(sql);
	    stmt.close();
	}
	
	public ArrayList<Attribute> getAllAttribute(String tableName) throws SQLException{
		String sql = "SELECT attribute, type, axis FROM zenvisage_metatable WHERE tablename = " + "'" + tableName + "'";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		ArrayList<Attribute> ret = new ArrayList<Attribute>();
		while(rs.next()){
			ret.add(new Attribute(rs.getString(1),rs.getString(2),rs.getString(3)));
		}
		return ret;
	}
	
	/**
	 * Removes existing dynamic class details for the selected dataset and adds the new ones
	 * @throws SQLException
	 */
	public void persistDynamicClassDetails(DynamicClass dc) throws SQLException{	
		String sql0 = "DELETE FROM zenvisage_dynamic_classes WHERE tablename='" + dc.dataset + "'";
		System.out.println(sql0);
		Statement st0 = c.createStatement();
		st0.executeUpdate(sql0);
		st0.close();
	    
		for (ClassElement e: dc.classes){
			String sql1 = "INSERT INTO zenvisage_dynamic_classes (tablename, attribute,ranges ) VALUES('" + dc.dataset + "','" + e.name + "','" + Arrays.deepToString(e.values) + "')";
			Statement st1 = c.createStatement();
			st1.executeUpdate(sql1);
			st1.close();
		}
	}
	
	public DynamicClass retrieveDynamicClassDetails(String query) throws SQLException{
		/*
		 * /zv/getClassInfo
		 * {“dataset”: “real_estate”}
		 */
		//get cmu
		String tableName = query.replaceAll("\"", "").replaceAll("}", "").replaceAll(" ","").split(":")[1];
		//String sql = "SELECT attribute, ranges FROM zenvisage_dynamic_classes WHERE tablename = " + "'" + tableName + "'";
		String sql = "SELECT tag, attributes, ranges, count FROM dynamic_class_aggregations WHERE table_name = " + "'" + tableName + "'";
		
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		DynamicClass dc = new DynamicClass();
		
		dc.dataset = tableName;
		List<String[]> l = new ArrayList<String[]>();
		float[][] testArray = {{1.0f, 2.0f, 3.0f}, {4.0f, 5.0f, 6.0f}, {7.0f, 8.0f, 9.0f}};
		
		while(rs.next()){
			System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getString(4));
			l.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
		}
		dc.classes = new ClassElement[l.size()];
		for(int i = 0; i < l.size(); i++){
			String[] cur = l.get(i);
			dc.classes[i] = new ClassElement(dc.dataset,testArray, cur[0], cur[1], cur[2], Integer.parseInt(cur[3]));
		}
		return dc;
	}
	
	/**
	 * Generating powerset of dynamic_classes 
     * and then update with one query instead of update each line with a query
     * 
     * 
	 * Enumerate combinations of criteria of each classes, set all rows satisfy that combination a dynamic_class string
	 * bp [0-10] [20-30] [40-50]
	 * fp [0-10] [20-30]
	 * ad [0-20] [30-40]
	 * if we have bp [0-10] fp [20-30] ad not satisfied,
	 * we mark dynamic_class string as  0.1.-1, 
	 * which means choose first criteria of bp, 
	 * second criteria of fp and none of ad. 
	 * Moreover . is the separator.
	 * @throws SQLException 
	 * http://stackoverflow.com/questions/6446250/sql-statement-with-multiple-sets-and-wheres
	 * http://stackoverflow.com/questions/27800119/postgresql-case-end-with-multiple-conditions
	 * http://dba.stackexchange.com/questions/39815/use-case-to-select-columns-in-update-query
	 *
	 * @param dc
	 * @throws SQLException
	 */
	public void persistDynamicClassPowerSetMethod(DynamicClass dc) throws SQLException{
		Statement st= c.createStatement();
		String sql = dc.retrieveSQL();
		//sql = "UPDATE " + dc.dataset + " SET dynamic_class = 'hello'";
		st.execute(sql);
		st.close();
	}
	
// jaewoo new function 

	public void createDynamicClassAggregation(DynamicClass dc) throws SQLException{
		SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();

		String sql_attribute = "SELECT attribute FROM zenvisage_dynamic_classes WHERE tablename = " + "'" + dc.dataset + "'";
		Statement st_attribute = c.createStatement();
		ResultSet rs = st_attribute.executeQuery(sql_attribute);
		ArrayList<String> attributeList = new ArrayList<String>();
		while(rs.next()){
			attributeList.add(rs.getString(1));
		}
		System.out.println(attributeList);
		
		// create temporary table to store initial permutations 
		
		if(!sqlQueryExecutor.gettablelist().contains("dynamic_class_aggregations_temp")){
				createTable("CREATE TABLE dynamic_class_aggregations_temp  " +
	                "(Table_Name           TEXT    NOT NULL, " +
	                " Tag            TEXT     NOT NULL, " +
	                " Ranges            TEXT     NOT NULL, "+
	                " Attributes           TEXT     NOT NULL) " );
	            
		}
		else{
			sqlQueryExecutor.dropTable("dynamic_class_aggregations_temp");
			createTable("CREATE TABLE dynamic_class_aggregations_temp  " +
	                "(Table_Name           TEXT    NOT NULL, " +
	                " Tag            TEXT     NOT NULL, " +
	                " Ranges            TEXT     NOT NULL, "+
	                " Attributes           TEXT     NOT NULL) " );
			
		}
		
		// generate the sql to insert all permutations. Eg. 0.0.0 to 1.1.2
		Statement st_ranges= c.createStatement();
		String sql_ranges = dc.retrieveSQL_aggregation(attributeList);
		System.out.println("sql: "+sql_ranges);
		st_ranges.execute(sql_ranges);
		st_ranges.close();
		
		// create the final table to hold the aggregations 
	if(!sqlQueryExecutor.gettablelist().contains("dynamic_class_aggregations")){
		createTable("CREATE TABLE dynamic_class_aggregations  " +
                " (Table_Name           TEXT    NOT NULL, " +
                " Tag            TEXT     NOT NULL, " +
                " Attributes            TEXT     NOT NULL, " +
                " Ranges            TEXT     NOT NULL, " +
                " Count           INT     NOT NULL) " );
            
	}
	else{
		sqlQueryExecutor.dropTable("dynamic_class_aggregations");
		createTable("CREATE TABLE dynamic_class_aggregations  " +
                " (Table_Name           TEXT    NOT NULL, " +
                " Tag            TEXT     NOT NULL, " +
                " Attributes            TEXT     NOT NULL, " +
                " Ranges            TEXT     NOT NULL, " +
                " Count           INT     NOT NULL) " );
		
	}
		
		//insert tuples that are a left join between the data table and the temporary table on the tags. 
	
		Statement st= c.createStatement();
		
		String sql = String.format("INSERT INTO dynamic_class_aggregations (table_name,tag,attributes,ranges,count)"
				+ "SELECT d.table_name, d.tag,d.attributes, d.ranges, COUNT(r.dynamic_class)\n"
				+ "FROM dynamic_class_aggregations_temp d LEFT JOIN " + dc.dataset +" r ON r.dynamic_class = d.tag\n"
				+ "GROUP BY d.table_name, d.tag, d.attributes,d.ranges;");
		
		st.execute(sql);
		//System.out.print(t);

		st.close(); 

		// drop the temporary table 
		
		sqlQueryExecutor.dropTable("dynamic_class_aggregations_temp");
		
	}

	public static void main(String[] args) throws SQLException{
		SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
		try {
//			sqlQueryExecutor.dropTable("COMPANY");

//			sqlQueryExecutor.query("SELECT * FROM cmu");
			System.out.println(sqlQueryExecutor.gettablelist());
//			sqlQueryExecutor.gettablelist();

			//sqlQueryExecutor.ZQLQuery("State", "Quarter", "SoldPrice", "real_estate", null);


		} catch (Exception e) {
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
		VizColumn vc = new VizColumn();
		vc.getMap().put(VizColumn.aggregation, "AVG");

		ZQLRow zqlRow = new ZQLRow(new XColumn(xList), new YColumn("soldprice"), new ZColumn("state"),"", vc);
		sqlQueryExecutor.ZQLQueryEnhanced(zqlRow, "real_estate");
	}

	public VisualComponentList getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(VisualComponentList visualComponentList) {
		this.visualComponentList = visualComponentList;
	}
	
	

}
