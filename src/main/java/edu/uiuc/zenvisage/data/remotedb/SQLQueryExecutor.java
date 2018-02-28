package edu.uiuc.zenvisage.data.remotedb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import edu.uiuc.zenvisage.model.VariableMeta;
import edu.uiuc.zenvisage.api.Readconfig;
import edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;
import edu.uiuc.zenvisage.model.ClassElement;
import edu.uiuc.zenvisage.service.utility.PasswordStorage;
import edu.uiuc.zenvisage.service.utility.PasswordStorage.CannotPerformOperationException;
import edu.uiuc.zenvisage.service.utility.PasswordStorage.InvalidHashException;

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
//	private String host = "jdbc:postgresql://localhost:" + Readconfig.getPostgresport() + "/"+database;
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username;
	private String password;
	Connection c = null;
	public VisualComponentList visualComponentList;
	public Statement st = null;

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
	      this.st = c.createStatement();
	      ResultSet ret = st.executeQuery(sQLQuery);
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
	
	
//	public ArrayList<String> gettablelist() throws SQLException {
//		Statement stmt = c.createStatement();
//		String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name != 'zenvisage_metatable' AND table_name != 'zenvisage_dynamic_classes' AND table_name != 'zenvisage_metafilelocation' AND table_name != 'users' AND table_name != 'users_tables'";		
//		ResultSet rs = stmt.executeQuery(sql);
//		ArrayList<String> tablelist = new ArrayList<String>();
//		while ( rs.next() ) {
//            String tablename = rs.getString("table_name");
//            tablelist.add(tablename);
//		}
//        return tablelist;
//	}
	
	public ArrayList<String> gettablelist() throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "SELECT tables FROM users_tables WHERE users = 'public'";		
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<String> tablelist = new ArrayList<String>();
		while ( rs.next() ) {
            String tablename = rs.getString("tables");
            tablelist.add(tablename);
		}
		stmt.close();
		rs.close();
        return tablelist;
	}
	
	public Map<String, ArrayList<String>> userinfo(String username) throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "SELECT tables FROM users_tables WHERE users = 'public' OR users ='"+username+"'";
		ResultSet rs = stmt.executeQuery(sql);
		Map<String, ArrayList<String>> info = new HashMap<String, ArrayList<String>>();
		ArrayList<String> tablelist = new ArrayList<String>();
		ArrayList<String> username_info = new ArrayList<String>();
//		ArrayList<String> userrole_info = new ArrayList<String>();
		username_info.add(username);
		info.put	("username", username_info);
		while ( rs.next() ) {
            String tablename = rs.getString("tables");
            tablelist.add(tablename);
		}
		info.put("tablelist", tablelist);
		stmt.close();
		rs.close();
        return info;
	}
	
	
    public boolean checkuser(String username, String password) throws SQLException, CannotPerformOperationException, InvalidHashException {
        Statement stmt = c.createStatement();
        String sql = "SELECT id, password FROM users WHERE id='" + username + "'";
//        System.out.println(sql);
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()) {
	        if(PasswordStorage.verifyPassword(password, rs.getString("password"))) {	
	        			stmt.close();
	                System.out.println("Login Succeed");
	                stmt.close();
	                rs.close();
	                return true;
	        	}
        }
        stmt.close();
        rs.close();
        System.out.println("Login Failed");
        return false;
    }
    
    public boolean register(String username, String password) throws SQLException, CannotPerformOperationException {
    		Statement stmt = c.createStatement();
    		
    		String sqlfinduser = "SELECT id FROM users WHERE id='" + username + "'";
    		ResultSet rs = stmt.executeQuery(sqlfinduser);
    		if(rs.next()) {
    			System.out.println("User exists, please login");
    			stmt.close();
    			rs.close();
    			return false;
    		}
    		else {
    			String hashedpass = PasswordStorage.createHash(password);
        		String sql = "INSERT INTO users (id,password) VALUES ('"+username+"','"+hashedpass+"')";
            System.out.println(sql);
            stmt.execute(sql);
            System.out.println("Register successfully");
            stmt.close();
            rs.close();
            return true;
    		}
    }
    
	public boolean insertusertablepair(String username, String tablename) throws SQLException {
    	Statement stmt = c.createStatement();
    		String sql = "INSERT INTO users_tables (users,tables) VALUES ('"+username+"','"+tablename+"')";
        stmt.execute(sql);
        System.out.println(sql);
        System.out.println("Insert user table pair successfully");
        stmt.close();
		return true;
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
	public void ZQLQueryEnhanced(ZQLRow zqlRow, String databaseName) throws SQLException {
		ZQLQueryEnhanced(zqlRow.getZ().getAttribute(), 
				(String) zqlRow.getViz().getMap().get(VizColumn.aggregation),
				zqlRow.getX().getAttributes().size(),
				zqlRow.getY().getAttributes().size(),
				zqlRow.getX().getAttributes(),
				zqlRow.getY().getAttributes(),
				zqlRow.getConstraint(),
				databaseName);
	}
	
	public void ZQLQueryEnhanced(String z, String agg, int xLen, int yLen, List<String> xAttributes, List<String> yAttributes, String constraints, String databaseName) throws SQLException{
		String sql = null;

		// Cleaning attributes
		databaseName = databaseName.toLowerCase();
		z = z.toLowerCase().replaceAll("'", "").replaceAll("\"", "");
		agg = agg.toLowerCase().replaceAll("'", "").replaceAll("\"", "");
		
		//support list of x, y values, general all possible x,y combinations, generate sql

		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());

		//clean Y attributes to use for query
		//if we have y1<-{'soldprice','listingprice'
		//this would build agg(soldprice),agg(listingprice),
		StringBuilder build = new StringBuilder();
		for(int j = 0; j < yLen; j++) {
			String cleanY = yAttributes.get(j).toLowerCase().replaceAll("'", "").replaceAll("\"", "");
			build.append(agg);
			build.append("(");
			build.append(cleanY);
			build.append(")");
			build.append(",");
		}
		// remove extra ,
		build.setLength(build.length() - 1);
		
		for(int i = 0; i < xLen; i++){
			String x = xAttributes.get(i).toLowerCase().replaceAll("'", "").replaceAll("\"", "");

			boolean hasZ = (z != null) && !z.equals("");
			//zqlRow.getConstraint() has replaced the whereCondiditon
			if (constraints == null || constraints =="") {
				sql = "SELECT " + (hasZ ? (z + "," + x) : ("1 as column1," + x) ) + "," + build.toString() //zqlRow.getViz() should replace the avg() function
						+ " FROM " + databaseName
						+ " GROUP BY " + (hasZ ? (z + "," + x) : x)
						+ " ORDER BY " + x;
			} else {

				sql = "SELECT " + (hasZ ? (z + "," + x) : x) + " ," + build.toString()
				+ " FROM " + databaseName
				+ " WHERE " + appendConstraints(constraints) //zqlRow.getConstraint() has replaced the whereCondiditon
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
			executeSQL(sql, z, databaseName, x, yAttributes);
		}


		/* Testing below */
        //System.out.println("Printing Visual Groups:\n" + this.visualComponentList.toString());
	}

	public void executeSQL(String sql, String z, String databaseName, String x, List<String> yAttributes) throws SQLException{
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		
		System.out.println("Finished SQL Execution");

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
			
			if(zType == null) zType = getMetaType(z, databaseName);
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
		
		//TODO: FIXME Assumes there is always one Y atttribute in the query
		for(String key: vcMap.keySet()) {
			this.visualComponentList.ZToVisualComponents.put(key, vcMap.get(key).get(0));
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
			
			String retS = new String(rs.getString(1));
			st.close();
			rs.close();
			return retS;
		}
		st.close();
		rs.close();
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
 			
 			String[] retStr = new String[]{ rs.getString(1), rs.getString(2)};
 			st.close();
 			rs.close();
 			return retStr;
 		}
 		st.close();
		rs.close();
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
			if(Integer.parseInt(rs0.getString(1))>0){
				st0.close();
				rs0.close();
				return false;
			}
 		}

		Statement st = c.createStatement();

//		System.out.println(sql);
		count = st.executeUpdate(sql);
		st0.close();
		rs0.close();
		st.close();
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
			st0.close();
			return false;
		}
		while (rs0.next())
 		{
			if(rs0.getString(2).equals(tableName)) 
			{
				System.out.println(tableName +" already exists");
				st0.close();
				rs0.close();
				return true;
			}
			
 		}
		st0.close();
		rs0.close();
		return false;
	}
	
	public void dropCSV(String tableName) throws SQLException{
		String sqlDropMeta = "DELETE FROM zenvisage_metatable where tablename = '"+tableName+"'";
		String sqlDropTable = "DROP TABLE "+ tableName;
		Statement stmt = c.createStatement();
		stmt.executeUpdate(sqlDropMeta);
		stmt.executeUpdate(sqlDropTable);
		stmt.close();
	}

	public void insertTable(String tableName, String fileName, List<String> columns) throws SQLException{
		StringBuilder sql = new StringBuilder("COPY "+ tableName + "(");
		for(String s:columns){
			sql.append(s+",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") FROM '"+ fileName +"' DELIMITER ',' CSV HEADER;");
		System.out.println("sql used to upload csv file:"+sql.toString());
	    Statement stmt = c.createStatement();
	    stmt.executeUpdate(sql.toString());
	    stmt.close();
	}
	
	/**
	 * Copy whole CSV table to postgres
	 * 
	 * @param tablename
	 * @param fileName
	 * @throws SQLException
	 */
	public void insertTable2(String tablename, String fileName) throws SQLException{
	  StringBuilder sql = new StringBuilder();
	  String tableAttributes = getTableAttributes(tablename);
	  sql.append("COPY " + tableAttributes + " From '"+ fileName + "' DELIMITER ',' CSV HEADER;");
	  System.out.println("sql used to upload csv file:"+sql.toString());
	  Statement stmt = c.createStatement();
	  stmt.executeUpdate(sql.toString());
	  stmt.close();
	}
	
	
    public void loadData1(String datafilename) throws IOException, SQLException{
	   	BufferedReader bufferedReader = new BufferedReader(new FileReader(datafilename));
		String line;
		line = bufferedReader.readLine();
		String[] header=line.split(",");
		for(int i=0;i<header.length;i++){
			header[i]=header[i].toLowerCase().replaceAll("-", "");
		}
		int count=0;
		String[] terms;
		while ((line = bufferedReader.readLine()) != null){
			terms=line.split(",");
	        count=count+1;
		}
		//set min, max value for each of the column in database
		for(int i=0;i<header.length;i++){
//			if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
//				SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
//				//System.out.println("min:" + columnMetadata.min + "max:"+columnMetadata.max);
//				sqlQueryExecutor.updateMinMax(name, header[i], columnMetadata.min, columnMetadata.max);
//			}
		}
		bufferedReader.close();
    }
	
	public String getTableAttributes(String tablename) throws SQLException{
		StringBuilder sql = new StringBuilder("select column_name from information_schema.columns where table_name = '"+ tablename+"'");
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql.toString());
		StringBuilder ret = new StringBuilder(tablename+"(");
		rs.next();//skip id
		while(rs.next()){
			ret.append(rs.getString(1)+",");
		}
		ret = new StringBuilder(ret.substring(0, ret.length()-("dynamic_class".length()+2)));
		ret.append(")");
		st.close();
		rs.close();
		return ret.toString();
	}
	
	public String[] getTableAttributesInArray(String tablename) throws SQLException{
		StringBuilder sql = new StringBuilder("select column_name from information_schema.columns where table_name = '"+ tablename+"'");
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql.toString());
		ArrayList<String> ret = new ArrayList<>();
		System.out.println("tablename:"+tablename);
		while(rs.next()){
			ret.add(rs.getString(1));
		}
		String[] retArray = new String[ret.size()];
		st.close();
		rs.close();
		return ret.toArray(retArray);
	}
	
	/*out of memory pulling*/
	public ResultSet selectAllFramTable(String tablename) throws SQLException{
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery("select * from "+tablename);
		return rs;
	}
	/*pagination read, potentially prevents heap memory blow up*/
	public ResultSet paginationSelectFromTable(String tablename, int limit, int offset ) throws SQLException{
		this.st = c.createStatement();
		ResultSet rs = st.executeQuery("select * from "+tablename +" order by id limit " + limit + " offset "+ offset);
		return rs;
	}
	
	public long getRowCount(String tablename) throws SQLException{
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery("select count(*) AS exact_count FROM "+tablename);
		while(rs.next()){
			long ret = Long.parseLong(rs.getString(1));
			rs.close();
			return ret;
		}
		st.close();
		return 0;
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
		st.close();
		rs.close();
		return ret;
	}
	
	public ArrayList<VariableMeta> getVariableMetaInfo(String tableName) throws SQLException{
		String sql = "SELECT attribute, type, selectedx, selectedy, selectedz, "
				+ "min, max FROM zenvisage_metatable WHERE tablename = " + "'" + tableName + "'";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		ArrayList<VariableMeta> ret = new ArrayList<>();
		while(rs.next()){
			String vMinS = rs.getString(6);
			String vMaxS = rs.getString(7);
			Float vMin = null;
			if(vMinS != null)
			  vMin= Float.parseFloat(vMinS);
			Float vMax = null;
			if(vMinS != null)
			  vMax = Float.parseFloat(vMaxS);
			ret.add(new VariableMeta(rs.getString(1),rs.getString(2), 
					rs.getBoolean(3),rs.getBoolean(4),
				    rs.getBoolean(5),vMin,vMax));
		}
		st.close();
		rs.close();
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
		
	    
		for (ClassElement e: dc.classes){
			String sql1 = "INSERT INTO zenvisage_dynamic_classes (tablename, attribute,ranges ) VALUES('" + dc.dataset + "','" + e.name + "','" + Arrays.deepToString(e.values) + "')";
			Statement st1 = c.createStatement();
			st1.executeUpdate(sql1);
			st1.close();
		}
		st0.close();
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
		st.close();
		rs.close();
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
		st_attribute.close();
		rs.close();
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
