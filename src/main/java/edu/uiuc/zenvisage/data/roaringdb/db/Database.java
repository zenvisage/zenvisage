package edu.uiuc.zenvisage.data.roaringdb.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;
import edu.uiuc.zenvisage.data.roaringdb.db.Column;
import edu.uiuc.zenvisage.data.roaringdb.db.DatabaseMetaData;
import edu.uiuc.zenvisage.model.VariableMeta;
import edu.uiuc.zenvisage.data.Query.FilterPredicate;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import  edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;

public class Database {
	private String name;
	private Map<String,Column> columns= new HashMap<String,Column>();
	private Map<String,Column> indexedColumns= new HashMap<String,Column>();
	public DatabaseMetaData databaseMetaData= new DatabaseMetaData();
	public long rowCount;

	public Database(String name,String schemafilename,String datafilename, boolean firstTime) throws IOException, InterruptedException, SQLException{
		this.name=name;
		this.databaseMetaData.dataset = name;
		//readSchema(schemafilename);
		readSchemaFromMetaTable(name);
		/**
		 * Separating schemafile read and metatable read
		 */
		if(schemafilename != null && datafilename != null){
			if(firstTime)
				loadData0(datafilename);
			else
				loadData1(datafilename);
		}else{
			loadData2(name);
		}

		DatabaseCatalog.addDatabase(name, this);
	}

	public Map<String, Column> getColumns() {
		return columns;
	}

	public Map<String, Column> getIndexedColumns() {
		return indexedColumns;
	}

	private void addValue(String columnName,int row,String value){
		Column column=columns.get(columnName);

//		System.out.println(columns.size());
//		System.out.println(columnName);
//		for(String key:columns.keySet()){
//			System.out.print(key +" ");
//		}
		//System.out.println();
		column.add(row, value);
 	}

	private void readSchema(String schemafilename) throws IOException, InterruptedException{
//   	 BufferedReader bufferedReader = new BufferedReader(new FileReader(schemafilename));
//   	 String in = getClass().getClassLoader().getResource(schemafilename).getPath();
//     BufferedReader bufferedReader = new BufferedReader(new FileReader(in));
//		System.out.println(schemafilename);
//		System.out.println(this.databaseMetaData.dataset);
//   	InputStream is = getClass().getResourceAsStream(schemafilename);
   	BufferedReader bufferedReader = new BufferedReader(new FileReader(schemafilename));
	 String line;
	 while ((line = bufferedReader.readLine()) != null){
			 ColumnMetadata columnMetadata= new ColumnMetadata();
			 String[] sections=line.split(":");
			 columnMetadata.name=sections[0].toLowerCase().replaceAll("-", "");
			 String[] terms=sections[1].split(",");
			 columnMetadata.isIndexed=true;
			 columnMetadata.dataType=terms[0];
			 columnMetadata.columnType=terms[8];
			 if("indexed".equals(terms[1])){
				 columnMetadata.isIndexed=true;
			 }
			 else{
				 columnMetadata.isIndexed=false;
			 }

		     if(terms[2].equals("T")){
		    	 databaseMetaData.xAxisColumns.put(columnMetadata.name,columnMetadata);
		     }
		     if(terms[3].equals("T")){
		    	 databaseMetaData.yAxisColumns.put(columnMetadata.name,columnMetadata);
		     }

		     if(terms[4].equals("T")){
		    	 databaseMetaData.zAxisColumns.put(columnMetadata.name,columnMetadata);
		     }

		     if(terms[5].equals("T")){
		    	 databaseMetaData.predicateColumns.put(columnMetadata.name,columnMetadata);
		     }
		     if (terms[6].equals("T")) {
		    	 columnMetadata.unit = terms[7];
		     }

		     new Column(columnMetadata, this);
		 }

		bufferedReader.close();
	}
	
	private void readSchemaFromMetaTable(String tableName) throws IOException, InterruptedException, SQLException{
     SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
	 while(!sqlQueryExecutor.isTableExists(tableName)){
	   Thread.sleep(2000); 
	 }
	 List<VariableMeta> variableMetas = sqlQueryExecutor.getVariableMetaInfo(tableName);
	 for (VariableMeta variableMeta: variableMetas){
		 ColumnMetadata columnMetadata= new ColumnMetadata();
		 columnMetadata.name=variableMeta.getAttribute().toLowerCase().replaceAll("-", "");
		 columnMetadata.isIndexed=false;
		 columnMetadata.dataType=variableMeta.getType();
		 columnMetadata.unit = "0";
		 String columType = "C";
		 switch(columnMetadata.dataType){
		   case "float":columType = "Q";break;
		   case "int":columType = "O";break;
		   case "timestamp":columType = "Q";break;
		 }
		 columnMetadata.columnType=columType;

	     if(variableMeta.isSelectedX()){
	    	 databaseMetaData.xAxisColumns.put(columnMetadata.name,columnMetadata);
	     }
	     if(variableMeta.isSelectedY()){
	    	 databaseMetaData.yAxisColumns.put(columnMetadata.name,columnMetadata);
	     }

	     if(variableMeta.isSelectedZ()){
	    	 databaseMetaData.zAxisColumns.put(columnMetadata.name,columnMetadata);
	    	 columnMetadata.isIndexed=true;
	     }
	     new Column(columnMetadata, this);
	  }
	}

	/**
	 * For loading from temp file without min, max
	 * @param datafilename
	 * @throws IOException
	 * @throws SQLException
	 */
    public void loadData0(String datafilename) throws IOException, SQLException{
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
            for(int i=0;i<header.length;i++){
           	     addValue(header[i].trim(), count, terms[i]);
                }
            count=count+1;
		 }
		this.rowCount=count;

		//set min, max value for each of the column in database
//		for(int i=0;i<header.length;i++){
//			ColumnMetadata columnMetadata = columns.get(header[i]).columnMetadata;
//			if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
//				SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
//				//System.out.println("min:" + columnMetadata.min + "max:"+columnMetadata.max);
//				sqlQueryExecutor.updateMinMax(name, header[i], columnMetadata.min, columnMetadata.max);
//			}
//		}

		bufferedReader.close();
	}

	/**
	 * For loading from temp file wit min, max
	 * @param datafilename
	 * @throws IOException
	 * @throws SQLException
	 */
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
	        for(int i=0;i<header.length;i++){
	       	     addValue(header[i].trim(), count, terms[i]);
	            }
	        count=count+1;
		 }
		this.rowCount=count;

		//set min, max value for each of the column in database
		for(int i=0;i<header.length;i++){
			ColumnMetadata columnMetadata = columns.get(header[i]).columnMetadata;
			if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
				SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
				//System.out.println("min:" + columnMetadata.min + "max:"+columnMetadata.max);
				sqlQueryExecutor.updateMinMax(name, header[i], columnMetadata.min, columnMetadata.max);
			}
		}

		bufferedReader.close();
    }
    
    /**
     * for loading from the postgres database
     * @param datafilename
     * @throws IOException
     * @throws SQLException
     */
    public void loadData2(String tablename) throws IOException, SQLException{
    	SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
		String[] header=sqlQueryExecutor.getTableAttributesInArray(tablename);
		int count=0;
		ResultSet rs = sqlQueryExecutor.selectAllFramTable(tablename);
		while(rs.next()){
			//minus away dynamic class, start from 1, and +1 for id
	        for(int i=1;i<header.length-1;i++){
	        	//System.out.println("header[i].trim():	"+header[i].trim());
	       	    addValue(header[i].trim(), count, rs.getString(i+1));
	        }
	        count=count+1;
		 }
		this.rowCount=count;

		//set min, max value for each of the column in database,
		//minus away dynamic class, start from 1, and +1 for id
		for(int i=1;i<header.length-1;i++){
			ColumnMetadata columnMetadata = columns.get(header[i]).columnMetadata;
			if(columnMetadata.dataType.equals("int") || columnMetadata.dataType.equals("float") ){
				//System.out.println("min:" + columnMetadata.min + "max:"+columnMetadata.max);
				sqlQueryExecutor.updateMinMax(name, header[i], columnMetadata.min, columnMetadata.max);
			}
		}
    }

    public RoaringBitmap getColumn(FilterPredicate filterPredicate) {
    	String columnName = filterPredicate.getPropertyName();
    	Column column = indexedColumns.get(columnName);
    	if (column != null) {
    		return column.getIndexedValues(filterPredicate);
    	}
    	return getUnIndexedColumn(filterPredicate);
    }

    public RoaringBitmap getIndexedColumn(FilterPredicate filterPredicate){
  		Column column=indexedColumns.get(filterPredicate.getPropertyName());
  		return column.getIndexedValues(filterPredicate);
  	 }

    public RoaringBitmap getUnIndexedColumn(FilterPredicate filterPredicate){
  		Column column=columns.get(filterPredicate.getPropertyName());
  		return column.getUnIndexedValues(filterPredicate);
  	 }

    public Map<String,RoaringBitmap> getIndexedColumn(String columnName){
  		Column column=indexedColumns.get(columnName);
  		if (column == null) return null;
  		return column.getIndexedValues();
  	 }

    public List<String> getUnIndexedColumn(String columnName){
    	Column column = columns.get(columnName);
    	if (column == null) return null;
    	return column.getUnIndexedValues();
    }

    // new
    public Map<String,RoaringBitmap> getColumn(String columnName) {
    	Column column = indexedColumns.get(columnName);
    	if (column == null) {
    		column = columns.get(columnName);
    		List<String> values = column.getUnIndexedValues();
    		Map<String,RoaringBitmap> maps = new HashMap<String,RoaringBitmap>();
    		for (int i = 0; i < values.size(); i++) {
    			if (maps.containsKey(values.get(i))) {
    				maps.get(values.get(i)).add(i);
    			}
    			else {
    				RoaringBitmap bits = new RoaringBitmap();
    				bits.add(i);
    				maps.put(values.get(i), bits);
    			}
    		}
    		return maps;
    	}
    	return column.getIndexedValues();
    }


    public ColumnMetadata getColumnMetaData(String columnName){

    	//columnName format incorrect... discovered during zql parser process column test
    	//Column column = columns.get(columnName);
    	Column column = columns.get(columnName.substring(0,1).toUpperCase()+columnName.substring(1));
		return column.columnMetadata;

     }

     public DatabaseMetaData getFormMetdaData(){
	  return databaseMetaData;
      }


}
