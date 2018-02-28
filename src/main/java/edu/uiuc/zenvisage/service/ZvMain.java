/**
 *
 */
package edu.uiuc.zenvisage.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.JsonNode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.SchemeToMetatable;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.roaringdb.db.Column;
import edu.uiuc.zenvisage.data.roaringdb.db.ColumnMetadata;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.db.DatabaseMetaData;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.data.roaringdb.executor.ExecutorResult;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.KMeans;
import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.LinearNormalization;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.Original;
import edu.uiuc.zenvisage.service.utility.PasswordStorage.CannotPerformOperationException;
import edu.uiuc.zenvisage.service.utility.PasswordStorage.InvalidHashException;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
import edu.uiuc.zenvisage.server.DatabaseAutoLoader;
import edu.uiuc.zenvisage.server.UploadHandleServlet;
import edu.uiuc.zenvisage.service.utility.Zscore;
import edu.uiuc.zenvisage.zql.QueryGraph;
import edu.uiuc.zenvisage.zql.ScatterProcessNode;
import edu.uiuc.zenvisage.zql.ZQLParser;
import edu.uiuc.zenvisage.zql.ZQLTableToGraph;
import edu.uiuc.zenvisage.zql.executor.ZQLExecutor;
import edu.uiuc.zenvisage.zql.executor.ZQLTable;
import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowResult;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowVizResult;
import edu.uiuc.zenvisage.service.distance.*;

/**
 * @author tarique
 *
 */
public class ZvMain {

	private Result cachedResult = new Result();
	private BaselineQuery cachedQuery = new BaselineQuery();
//	private InMemoryDatabase inMemoryDatabase;
//	private Map<String,Database> inMemoryDatabases = new HashMap<String,Database>();

	public static Map<String,Database> inMemoryDatabases;
	//public Executor executor = new Executor(inMemoryDatabase);
	public Analysis analysis;
	public Distance distance;
	public Normalization normalization;
	public Normalization outputNormalization;
	public PiecewiseAggregation paa;
	public ArrayList<List<Double>> data;
//	public String databaseName;
	public String buffer = null;
	
	public static SQLQueryExecutor sqlQueryExecutor;
	
	static final Logger logger = LoggerFactory.getLogger(ZvMain.class);

	public ZvMain() {
		sqlQueryExecutor = new SQLQueryExecutor();
		inMemoryDatabases = new HashMap<String,Database>();
		System.out.println("ZVMAIN LOADED");
	}
	
	public int getDatasetLength(String zAttr, String datasetname) throws SQLException{
		String query = "SELECT COUNT(DISTINCT("+zAttr+")) FROM "+datasetname+';';
		System.out.println("query:"+query);
		ResultSet ret = sqlQueryExecutor.query(query);
		int size = 0;
		while (ret.next()){
			size = ret.getInt("count");
		}
		sqlQueryExecutor.st.close();
		ret.close();
		return size;
	}

	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException, SQLException {
		UploadHandleServlet uploadHandler = new UploadHandleServlet();
		List<String> names = uploadHandler.upload(request, response);
		uploadDatasettoDB2(names,true);
	}
	
	public void insertZenvisageMetatable(Variables variables) throws SQLException, IOException, InterruptedException{
		SchemeToMetatable schemeToMetatable = new SchemeToMetatable();
		String[] ret = schemeToMetatable.schemeFileToMetaSQLStream3(variables);
		String datasetName = variables.getDatasetName();
		if(sqlQueryExecutor.isTableExists(datasetName)){
			sqlQueryExecutor.dropCSV(datasetName);
		}
		while(sqlQueryExecutor.isTableExists(datasetName)){
			Thread.sleep(500);
		}
		if(!sqlQueryExecutor.isTableExists(datasetName)){
			if(sqlQueryExecutor.insert(ret[0], "zenvisage_metatable", "tablename",  variables.getDatasetName())){
				System.out.println("MetaType Data successfully inserted into Postgres");
			} else {
				System.out.println("MetaType already exists!");
			}
			sqlQueryExecutor.createTable(ret[1]);
		}else{
			System.out.println("Table already exists!");
		}
	}
		
	/*
	 * This is for auto uploader.
	 */
    public void uploadDatasettoDB(List<String> names, boolean overwrite) throws SQLException, IOException, InterruptedException{
		SchemeToMetatable schemeToMetatable = new SchemeToMetatable();
		
		if (names.size() == 3) {

			/*create csv table*/
			if(!sqlQueryExecutor.isTableExists(names.get(0))){
				
				/*insert zenvisage_metafilelocation*/
				String locationTupleSQL = "INSERT INTO zenvisage_metafilelocation (database, metafilelocation, csvfilelocation) VALUES "+
						"('" + names.get(0) +"', '"+ names.get(2)+"', '"+ names.get(1)+"');";
				if(sqlQueryExecutor.insert(locationTupleSQL, "zenvisage_metafilelocation", "database", names.get(0))){
					System.out.println("Metafilelocation Data successfully inserted into Postgres");
				} else {
					System.out.println("Metafilelocation aluploadDatasettoDBready exists!");
				}
				
				/*insert zenvisage_metatable*/
				
				if(sqlQueryExecutor.insert(schemeToMetatable.schemeFileToMetaSQLStream(names.get(2), names.get(0)), "zenvisage_metatable", "tablename",  names.get(0))){
					System.out.println("MetaType Data successfully inserted into Postgres");
				} else {
					System.out.println("MetaType already exists!");
				}
				
				sqlQueryExecutor.createTable(schemeToMetatable.createTableSQL);
				sqlQueryExecutor.insertTable(names.get(0), names.get(1), schemeToMetatable.columns);
				System.out.println(names.get(0) + " not exists! Created " + names.get(0) + " table from "+names.get(1));
				System.out.println("Successful upload! "+ names.get(0) +" "+names.get(2) + " "+  names.get(1));
				
			} else if(overwrite) {//
				sqlQueryExecutor.dropTable(names.get(0));
				sqlQueryExecutor.createTable(schemeToMetatable.schemeFileToCreatTableSQL(names.get(2), names.get(0)));
				sqlQueryExecutor.insertTable(names.get(0), names.get(1), schemeToMetatable.columns);
				System.out.println(names.get(0) + " exists! Overwrite and create " + names.get(0) + " from "+names.get(1));
			}

			//new Database(names.get(0), names.get(2), names.get(1), true);
			//inMemoryDatabase = createDatabase(names.get(0), names.get(2), names.get(1));


//			inMemoryDatabases.put(names.get(0), inMemoryDatabase);
		}
		
	}
   
   
   public void uploadDatasettoDB2(List<String> names, boolean overwrite) throws SQLException, IOException, InterruptedException{
		if (names.size() == 2) {
			/*create csv table*/	
			if(overwrite){
				while(!sqlQueryExecutor.isTableExists(names.get(0))){
					 Thread.sleep(1000); 
				}
				sqlQueryExecutor.insertTable2(names.get(0), names.get(1));
				System.out.println("Successfully uploaded csv: " + names.get(0));
			
			}
		}
	}
	
//   public String runZQLCompleteQuery(String zqlQuery) throws IOException, InterruptedException, SQLException{
//		  System.out.println(zqlQuery);
//	   	  inMemoryDatabase = inMemoryDatabases.get("real_estate");
//		  executor = new Executor(inMemoryDatabase);
//		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.executor=executor;
//		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
//     	  String result=new ObjectMapper().writeValueAsString(edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.execute(zqlTable));
//     	  System.out.println(result);
//     	  return result;
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(ZQLTest.createZQLTable()));
//
//		}

   /**
    * 
    * @param zqlQuery Receives as a string the JSON format of a ZQLTable
    * @return String representing JSON format of Result (output of running ZQLTable through our query graph)
    * @throws IOException
    * @throws InterruptedException
    */
   public String runQueryGraph(String zqlQuery) throws IOException, InterruptedException{
	   System.out.println(zqlQuery);
	   edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
	   ZQLTableToGraph parser = new ZQLTableToGraph();
	   QueryGraph graph;
	   try {
		   graph = parser.processZQLTable(zqlTable, null);
		   VisualComponentList output = edu.uiuc.zenvisage.zql.QueryGraphExecutor.execute(graph);
		   //convert it into front-end format.
		   String result = new ObjectMapper().writeValueAsString(convertVCListtoVisualOutput(output));
		   //System.out.println(" Query Graph Execution Results Are:");
		   //System.out.println(result);
		   System.out.println("Done");
		   return result;
	   } catch (SQLException e) {
		   e.printStackTrace();
		   return "";
	   }
   }
   
   public String runScatterQueryGraph(String zqlQuery) throws IOException, InterruptedException{
	   System.out.println(zqlQuery);
	   long startTime = System.currentTimeMillis();
	   edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
	   long endTime = System.currentTimeMillis();
	   logger.info("Mapping json to table took " + (endTime - startTime) + "ms");
	   
	   ZQLTableToGraph parser = new ZQLTableToGraph();
	   QueryGraph graph;
	   try {
		   startTime = System.currentTimeMillis();
		   graph = parser.processZQLTable(zqlTable, null);
		   endTime = System.currentTimeMillis();
		   logger.info("Parsing ZQLTable to Graph took " + (endTime - startTime) + "ms");
		   
		   startTime = System.currentTimeMillis();
		   VisualComponentList output = edu.uiuc.zenvisage.zql.QueryGraphExecutor.execute(graph);
		   endTime = System.currentTimeMillis();
		   logger.info("Execution took " + (endTime - startTime) + "ms");		   
		   //convert it into front-end format.
		   String result = new ObjectMapper().writeValueAsString(convertVCListtoScatterOutput(output));
		   System.out.println("Done");
		   return result;
	   } catch (SQLException e) {
		   e.printStackTrace();
		   return "";
	   }
   }

   /**
    * 
    * @param zqlQuery Receives as a string the JSON format of a ZQLTable
    * @return String representing JSON format of Result (output of running ZQLTable through our query graph)
    * @throws IOException
    * @throws InterruptedException
    */
   public String runZQLScript(String script) throws IOException, InterruptedException{
	   System.out.println(script);
	   //edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
	   //ZQLTableToGraph parser = new ZQLTableToGraph();
	   QueryGraph graph;
	   graph = ZQLParser.parseScript(script);
	   VisualComponentList output = edu.uiuc.zenvisage.zql.QueryGraphExecutor.execute(graph);
	   //convert it into front-end format.
	   String result = new ObjectMapper().writeValueAsString(convertVCListtoVisualOutput(output));
	   //System.out.println(" Query Graph Execution Results Are:");
	   //System.out.println(result);
	   System.out.println("Done");
	   return result;
   }   
   
   public ScatterOutput convertVCListtoScatterOutput(VisualComponentList vcList) {
	   ScatterOutput finalOutput = new ScatterOutput();
		//VisualComponentList -> Result. Only care about the outputcharts. this is for submitZQL
	    for(VisualComponent viz : vcList.getVisualComponentList()) {
	    	ScatterChart outputChart = new ScatterChart();

	    	outputChart.zval = viz.getZValue().toString();
	    	ArrayList<WrapperType> xList = viz.getPoints().getXList();
	    	ArrayList<WrapperType> yList = viz.getPoints().getYList();
	    	for(int i = 0; i < viz.getPoints().getXList().size(); i++) {
	    		outputChart.points.add(new Point(xList.get(i).getNumberValue(), yList.get(i).getNumberValue()));
	    	}
	    	finalOutput.outputCharts.add(outputChart);
	    }
		return finalOutput;	   
   }
   
   public Result convertVCListtoVisualOutput(VisualComponentList vcList){
		Result finalOutput = new Result();
		//VisualComponentList -> Result. Only care about the outputcharts. this is for submitZQL
	    for(VisualComponent viz : vcList.getVisualComponentList()) {
	    	Chart outputChart = new Chart();

	    	outputChart.setzType( viz.getzAttribute() );
	    	outputChart.setxType( viz.getxAttribute() );
	    	outputChart.setyType( viz.getyAttribute() );
	    	outputChart.title = viz.getZValue().getStrValue();
	    	outputChart.setNormalizedDistance(viz.getScore());
	    	// outputChart.setxType((++i) + " : " + viz.getZValue().getStrValue());
	    	// outputChart.setyType("avg" + "(" + viz.getyAttribute() + ")");
	    	// outputChart.title = "From Query Graph";

	    	for(WrapperType xValue : viz.getPoints().getXList()) {
	    		outputChart.xData.add(xValue.toString());
	    	}
	    	for(WrapperType yValue : viz.getPoints().getYList()) {
	    		outputChart.yData.add(yValue.toString());
	    	}
	    	finalOutput.outputCharts.add(outputChart);
	    }
		return finalOutput;
	 }

   //old
//   public String runZQLQuery(String zqlQuery) throws IOException, InterruptedException{
//		  inMemoryDatabase = inMemoryDatabases.get("real_estate");
//		  executor = new Executor(inMemoryDatabase);
//		  ZQLExecutor.executor=executor;
//		  ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery,ZQLTable.class);
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(zqlTable));
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(ZQLTest.createZQLTable()));
//
//		}


//	public String getScatterPlot(String query) throws JsonParseException, JsonMappingException, IOException {
//		System.out.print(query);
//		ScatterPlotQuery q = new ObjectMapper().readValue(query, ScatterPlotQuery.class);
//		Map<String, ScatterResult> output = executor.getScatterData(q);
//		if (output == null) return "";
//		Result finalOutput = new Result();
//		finalOutput.method = q.method;
//		if (q.method == "ScatterRep") {
//			ScatterRep.compute(output, q, finalOutput);
//		}
//		else {
//			ScatterRank.compute(output, q, finalOutput);
//		}
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(finalOutput);
//	}

	/**
	 * Given a front end sketch or drag and drop, run similarity search through the query graph backend
	 * @param zvQuery
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public String runSimilaritySearch(String zvQuery) throws InterruptedException, IOException {
		String result = "";
		
		ZvQuery args = new ObjectMapper().readValue(zvQuery, ZvQuery.class);
//		this.databaseName=args.databasename;
	    ZQLTableToGraph parser = new ZQLTableToGraph();
	   //QueryGraph graph = parser.processZQLTable(zqlTable);
	   //VisualComponentList output = edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryGraphExecutor.execute(graph);		
		
		return result;
	}
	
	private ZQLTable createSimilairtySearchTable(ZvQuery args) {
		ZQLTable table = new ZQLTable();
		List<ZQLRow> rows = new ArrayList<ZQLRow>();
		
		Name name1 = new Name();
		name1.setName("f1");
		
		return null;
	}

	public void runCreateClasses(String query) throws IOException, SQLException{
		System.out.println("Create Dynamic Classes Configuration Query:" + query);
	    DynamicClass dc = new ObjectMapper().readValue(query, DynamicClass.class);
	    sqlQueryExecutor.persistDynamicClassPowerSetMethod(dc);
	    sqlQueryExecutor.persistDynamicClassDetails(dc);
	    sqlQueryExecutor.createDynamicClassAggregation(dc);
	}
	
	public String runRetrieveClasses(String query) throws IOException, SQLException{
		DynamicClass dc = sqlQueryExecutor.retrieveDynamicClassDetails(query);
		String retrieved = new ObjectMapper().writeValueAsString(dc);
		System.out.println("Retrieved Dynamic Classes Configuration Query:"+retrieved);
		return retrieved;
	}
	
	public ArrayList<String> getTablelist() throws IOException, SQLException{
		ArrayList<String> retrieved = sqlQueryExecutor.gettablelist();
		System.out.println("Retrieved table list in db:"+retrieved);
		return retrieved;
	}
	
	public Map<String, ArrayList<String>> userinfo(String username) throws IOException, SQLException{
		Map<String, ArrayList<String>> retrieved = sqlQueryExecutor.userinfo(username);
		System.out.println("Retrieved userinfo in db:"+retrieved);
		return retrieved;
	}
	
    public boolean checkUser(String username, String password) throws IOException, SQLException, CannotPerformOperationException, InvalidHashException{
        boolean retrieved = sqlQueryExecutor.checkuser(username, password);
        System.out.println("Login status:" + retrieved);
        return retrieved;
    }
    
    public boolean register(String username, String password) throws IOException, SQLException, CannotPerformOperationException, InvalidHashException{
        boolean retrieved = sqlQueryExecutor.register(username, password);
        System.out.println("Registration status:" + retrieved);
        return retrieved;
    }
    
    public boolean insertUserTablePair(String username, String tablename) throws IOException, SQLException, CannotPerformOperationException, InvalidHashException{
        boolean retrieved = sqlQueryExecutor.insertusertablepair(username, tablename);
        System.out.println("User table pair inserted:" + retrieved);
        return retrieved;
    }
    
	/* Will be obsolete when the new separated query method is utilized */
//	public String runDragnDropInterfaceQuery(String query) throws InterruptedException, IOException{
//		// get data from database
//		System.out.println(query);
//		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
//		 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
//		 if (args.method.equals("SimilaritySearch"))
//			 setFilter(q, args);
//		 ExecutorResult executorResult = executor.getData(q);
//		 if (executorResult == null) return "";
//		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;
//		 // setup result format
//		 Result finalOutput = new Result();
//		 finalOutput.method = args.method;
//		 //finalOutput.xUnit = inMemoryDatabase.getColumnMetaData(args.xAxis).unit;
//		 //finalOutput.yUnit = inMemoryDatabase.getColumnMetaData(args.yAxis).unit;
//		 // generate new result for query
//		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, executorResult.xMap);
//		 // generate the corresponding distance metric
//		 if (args.distance_metric.equals("Euclidean")) {
//			 distance = new Euclidean();
//		 }
//		 else {
//			 distance = new DTWDistance();
//		 }
//		 // generate the corresponding data normalization metric
//		 if (args.distanceNormalized) {
//			 normalization = new Zscore();
////			 normalization = new Original();
//		 }
//		 else {
//			 normalization = new Original();
//		 }
//		 // generate the corresponding output normalization
//
//		 outputNormalization = new Original();
//		 // reformat database data
//		 DataReformation dataReformatter = new DataReformation(outputNormalization);
//		 double[][] normalizedgroups = dataReformatter.reformatData(output);
//		 // generate the corresponding analysis method
//		 if (args.method.equals("Outlier")) {
//			 Clustering cluster = new KMeans(distance, normalization, args);
//			 analysis = new Outlier(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
//		 }
//		 else if (args.method.equals("RepresentativeTrends")) {
//			 Clustering cluster = new KMeans(distance, normalization, args);
//			 analysis = new Representative(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
//		 }
//		 else if (args.method.equals("SimilaritySearch")) {
//			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
//			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
//			 ((Similarity) analysis).setDescending(false);
//		 }
//		 else if (args.method.equals("DissimilaritySearch")) {
//			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
//			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
//			 ((Similarity) analysis).setDescending(true);
//		 }
//		 analysis.compute(output, normalizedgroups, args);
//
//		 ObjectMapper mapper = new ObjectMapper();
//		 return mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
//	}
	public Result runErrorQuery(String query, String method) throws InterruptedException, IOException, SQLException{
		 System.out.println("runErrorQuery executing!");
		 ZvQuery args_error = new ObjectMapper().readValue(query,ZvQuery.class);
		 args_error.setYaxisAsError(); 
//		 this.databaseName=args_error.databasename;
		 String databaseName = args_error.databasename;
		 Query q_error = new Query("query").setGrouby(args_error.groupBy+","+args_error.xAxis).setAggregationFunc(args_error.aggrFunc).setAggregationVaribale(args_error.getAggrVar());
		 if (method.equals("SimilaritySearch"))
			 setFilter(q_error, args_error);
		 System.out.println("args_error:"+args_error.toString());
		 System.out.println("Before SQL");
		 //sqlQueryExecutor.ZQLQuery(Z, X, Y, table, whereCondition);
		 sqlQueryExecutor.ZQLQueryEnhanced(q_error.getZQLRow(), databaseName);
		 System.out.println("After SQL");
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output =  sqlQueryExecutor.getVisualComponentList().toInMemoryHashmap();
		 
		 System.out.println("After To HashMap");
		 output = cleanUpDataWithAllZeros(output);
		 
		 
		 //
		output= SmoothingUtil.applySmoothing(output,args_error);
		 
		 // setup result format
		 Result finalOutput = new Result();
		 finalOutput.method = method;
		 

		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args_error, HashBiMap.create());
		 chartOutput.chartOutput(output, args_error, finalOutput);
		 
		 return finalOutput;
						 // jaewoo implementation for error bars 
//		 DataReformation dataReformatter = new DataReformation(normalization);
//		 double[][] normalizedgroups;
//			
//
//			 normalizedgroups = dataReformatter.reformatData(output);
//			 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args_error);
//			 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args_error.dataX, args_error.dataY, args_error.xRange, normalizedgroups[0].length);
//			 interpolatedQuery= SmoothingUtil.applySmoothing(interpolatedQuery,args_error);
//				Analysis analysis_error = new Similarity(chartOutput,distance,normalization,paa,args_error,dataReformatter, interpolatedQuery);
//				((Similarity) analysis_error).setDescending(false);
//				//analysis_error.compute(output, normalizedgroups, args_error);
//				return analysis_error.getChartOutput().finalOutput;
		 
		
	}
	public Analysis buildAnalysisDragnDropInterfaceQuery(String query, String method) throws InterruptedException, IOException, SQLException{
	//public Result runDragnDropInterfaceQuery(String query, String method) throws InterruptedException, IOException, SQLException{
		// get data from database
		 System.out.println("runDragnDropInterfaceQuery");
		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
//		 this.databaseName=args.databasename;
		 String databaseName = args.databasename;
		 if (args.downloadAll){
			 int size = getDatasetLength(args.groupBy,args.databasename);
			 System.out.println("size:"+Integer.toString(size));
			 args.setOutlierCount(size);
			 query = new ObjectMapper().writeValueAsString(args);
		 }
		 
		 VisualComponentList rawVisualComponentList=null;
		 boolean noAgg=false;
		 if(args.aggrFunc.equals("")){
			 noAgg=true;
			 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
				//	 if (method.equals("SimilaritySearch"))
						 setFilter(q, args);
						 
			 sqlQueryExecutor.ZQLQueryEnhanced(q.getZQLRow(), databaseName);
			 System.out.println("After SQL for no agg");
			 rawVisualComponentList =  sqlQueryExecutor.getVisualComponentList();		
			 args.setAggrFunc("avg");
		 }
		 	 
		 
		 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
	//	 if (method.equals("SimilaritySearch"))
			 setFilter(q, args);
			 
		 

//		 ExecutorResult executorResult = executor.getData(q);
//		 if (executorResult == null) return "";
//		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;
		 /*
		  * Instead of calling roaring db, we feed in VC output from postgres
		  * ExecutorResult executorResult = executor.getData(q);
		  * if (executorResult == null) return "";
		  * LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;
		  */
		 System.out.println("Before SQL");
		 //sqlQueryExecutor.ZQLQuery(Z, X, Y, table, whereCondition);
		 sqlQueryExecutor.ZQLQueryEnhanced(q.getZQLRow(), databaseName);
		 System.out.println("After SQL");
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output =  sqlQueryExecutor.getVisualComponentList().toInMemoryHashmap();
		 System.out.println("output size:"+output.size());
	
		 System.out.println("After To HashMap");
		 output = cleanUpDataWithAllZeros(output);
		 
		 
		 //
		output= SmoothingUtil.applySmoothing(output,args);
		 
		 // setup result format
		 Result finalOutput = new Result();
		 finalOutput.method = method;
		 //finalOutput.xUnit = inMemoryDatabase.getColumnMetaData(args.xAxis).unit;
		 //finalOutput.yUnit = inMemoryDatabase.getColumnMetaData(args.yAxis).unit;
		 // generate new result for query

//		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, executorResult.xMap);
		 /*
		  * We don't have xMap now since we use posgres
		  * ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, executorResult.xMap);
		  */
		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, HashBiMap.create());

		 // generate the corresponding distance metric
		 if (args.distance_metric.equals("Euclidean")) {
			 distance = new Euclidean();
		 }
		 else if (args.distance_metric.equals("Segmentation")){
			 distance = new SegmentationDistance();
		 }
		 else if (args.distance_metric.equals("MVIP")){
			 distance = new MVIP();
		 }
		 else {
			 distance = new DTWDistance();
		 }
		 // generate the corresponding data normalization metric
		 //args.distanceNormalized="original";
		 System.out.println("args.distanceNormalized:"+args.distanceNormalized);
		 if (args.distanceNormalized.equals("linear")) {
//			 normalization = new LinearNormalization();
			 normalization = new LinearNormalization();
//			 normalization = new Original();
		 }
		 else if (args.distanceNormalized.equals("zscore")) {
			 normalization = new Zscore();
			 //normalization = new LinearNormalization();
		 }
		 else if (args.distanceNormalized.equals("original")) {
			 normalization = new Original();
			 //normalization = new LinearNormalization();
		 }
		 // generate the corresponding output normalization

		 outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(normalization);
		 double[][] normalizedgroups;

		 System.out.println("Before Methods");
		 // generate the corresponding analysis method
		 if (method.equals("Outlier")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Outlier(chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("RepresentativeTrends")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Representative(chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("SimilaritySearch")) {
			 //paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase); // O(1)

			 if (args.considerRange) {
				 double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args); // O(V*P)
				 normalizedgroups = overlappedDataAndQueries[0];
				 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
				 double[][] overlappedQuery = overlappedDataAndQueries[1];
				 overlappedQuery= SmoothingUtil.applySmoothing(overlappedQuery,args);
				 analysis = new Similarity(chartOutput,distance,normalization,args,dataReformatter, overlappedQuery);
			 }
			 else {
				 normalizedgroups = dataReformatter.reformatData(output);
				 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
				 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length); // O(P)
				 interpolatedQuery= SmoothingUtil.applySmoothing(interpolatedQuery,args);
				 analysis = new Similarity(chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 }

			 ((Similarity) analysis).setDescending(false);
		 }
		 else { //(method.equals("DissimilaritySearch"))
			 //paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);

			 if (args.considerRange) {
				 double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args);
				 normalizedgroups = overlappedDataAndQueries[0];
				 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
				 double[][] overlappedQuery = overlappedDataAndQueries[1];
				 overlappedQuery= SmoothingUtil.applySmoothing(overlappedQuery,args);
				 analysis = new Similarity(chartOutput,distance,normalization,args,dataReformatter, overlappedQuery);
			 }
			 else {
				 normalizedgroups = dataReformatter.reformatData(output);
				 normalizedgroups= SmoothingUtil.applySmoothing(normalizedgroups,args);
				 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length);
				 interpolatedQuery= SmoothingUtil.applySmoothing(interpolatedQuery,args);
				 analysis = new Similarity(chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 }
			 ((Similarity) analysis).setDescending(true);
		 }
		 System.out.println("After Interpolation and normalization");
		 if (args.getDownload() && method.equals("RepresentativeTrends")){
			 analysis.download(output, normalizedgroups, args);
		 }else{
			 System.out.println("computer analysis using output, normalizedgroups, args");
			 System.out.println("output size:"+output.size());
			 System.out.println("normalizedgroups length:"+normalizedgroups.length);
			 analysis.compute(output, normalizedgroups, args);
		 }
		 
		 if(noAgg)
		 convertToRawViz(analysis,rawVisualComponentList);
		 
		 System.out.println("After Distance calulations");
		 return analysis; 
	}
	
	public void convertToRawViz(Analysis analysis,VisualComponentList rawVisualComponentList){
		
	}
	
	
	public Result runDragnDropInterfaceQuery(String query, String method) throws InterruptedException, IOException, SQLException{
		 Analysis analysis = buildAnalysisDragnDropInterfaceQuery(query,method);
		 return analysis.getChartOutput().finalOutput;
	}
	
	public String saveRepresentativeDragnDropInterfaceQuery(String query, String method) throws InterruptedException, IOException, SQLException{
		 Analysis analysis = buildAnalysisDragnDropInterfaceQuery(query,method);
		 return analysis.downloadData;
	}
		
		
//		if (args.getDownload() && method.equals("RepresentativeTrends")){
//		 String downloadData = analysis.download(output, normalizedgroups, args);
//		 System.out.println("After Distance calulations");
//		 return downloadData;
//	 }else{
//		 
//	 }


	public synchronized String runDragnDropInterfaceQuerySeparated(String query, String method) throws InterruptedException, IOException, SQLException{
		 System.out.println("runDragnDropInterfaceQuerySeparated:");
		 ZvQuery args = new ObjectMapper().readValue(query, ZvQuery.class);
		 if (args.getDownload() && method.equals("RepresentativeTrends")){
			 String res = saveRepresentativeDragnDropInterfaceQuery(query, method);
			 ObjectMapper mapper = new ObjectMapper();
			 System.out.println("After mapping to output string");
			 return res;
		 }
		 else{
			 Result result = runDragnDropInterfaceQuery(query,method);
			 ObjectMapper mapper = new ObjectMapper();
			 System.out.println("After Interpolation and normalization");
			 String res = mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
			 System.out.println("After mapping to output string");
			 return res;
		 }
	}
	
	public synchronized String runDragnDropInterfaceQuerySeparated_error(String query, String method) throws InterruptedException, IOException, SQLException{
		 Result result = runErrorQuery(query,method);
		 ObjectMapper mapper = new ObjectMapper();
		 String res = mapper.writeValueAsString(result);
		 return res;
	}
	
	public synchronized String saveDragnDropInterfaceQuerySeparated(String query, String method) throws InterruptedException, IOException, SQLException{
		// Save Results Query to a csv file
		 ZvQuery args = new ObjectMapper().readValue(query, ZvQuery.class);
		 System.out.println(args.databasename);
//		 this.databaseName=args.databasename;
		 String databaseName = args.databasename;
		 if (args.downloadAll){
			 int size = getDatasetLength(args.groupBy,args.databasename);
			 System.out.println("size:"+Integer.toString(size));
			 args.setOutlierCount(size);
			 query = new ObjectMapper().writeValueAsString(args);
//			 System.out.println("query:"+query);
		 }
		 System.out.println("saveDragnDropInterfaceQuerySeparated:");
		 System.out.println("method:"+method);
		 Result result = runDragnDropInterfaceQuery(query,method);
		 System.out.println("Result:"+result);
		 
		 System.out.println("After Interpolation and normalization");
		 
		 ArrayList<Chart> outputCharts = result.outputCharts;
		 boolean downloadX = args.deriveDownloadX();
		 boolean includeQuery = args.getIncludeQuery();
		 
		 String dataX = String.join(",", Arrays.toString(args.getDataX()));
		 String dataY = String.join(",", Arrays.toString(args.getDataY()));
		 
//		 FileWriter fx = null;
//		 BufferedWriter bx = null;
		 Chart sampleChartSchema = outputCharts.get(0);
		 String prefix = "";
		 String JsonString="{";
		 String xJsonString="";
		 String yJsonString="";
		 String xvalString="";
		 String yvalString="";
		 if (method.equals("Outlier")) {
			 prefix = "outlier_";
		 }
		 
		 if (downloadX){
			 xJsonString+='\"'+prefix+sampleChartSchema.xType+".csv\":[";
		 }
		 yJsonString+='\"'+prefix+sampleChartSchema.yType+".csv\":[";
//		 if (args.deriveDownloadX()){
//			 fx = new FileWriter(prefix+sampleChartSchema.xType+".csv");
//			 bx = new BufferedWriter(fx);
//		 }
//		 FileWriter fy = new FileWriter(prefix+sampleChartSchema.yType+".csv");
//		 BufferedWriter by = new BufferedWriter(fy);

		 // Writing query
		 if (method.equals("SimilaritySearch") && includeQuery) {
			 //by.write("query ,"+"1.0,"+ dataY.substring(1, dataY.length() - 1)+"\n");
			 yvalString+="\""+"query ,"+"1.0,"+ dataY.substring(1, dataY.length() - 1)+"\",";
			 if (downloadX){
				 //bx.write("query ,"+ dataX.substring(1, dataX.length() - 1)+"\n");
				 xvalString+="\""+"query ,"+ dataX.substring(1, dataX.length() - 1)+"\",";
			 }
		 }
		 
		 // Writing individual visualizations
		 for (int i = 0; i < outputCharts.size(); i++){
			 Chart viz = outputCharts.get(i);
			 if (args.downloadThresh!=0.0){ // If nonzero downloadThresh set, then use it as a cutoff
				 if (viz.normalizedDistance>=args.downloadThresh){
					 //by.write(viz.title+','+viz.normalizedDistance+','+ String.join(",", viz.yData)+"\n");
					 yvalString+="\""+viz.title+','+viz.normalizedDistance+','+ String.join(",", viz.yData)+"\",";
					 if (downloadX){
						 //bx.write(viz.title+','+ String.join(",", viz.xData)+"\n");
						 xvalString+="\""+viz.title+','+ String.join(",", viz.xData)+"\",";
					 }
				 }
			 }else{
				 //by.write(viz.title+','+viz.normalizedDistance+','+ String.join(",", viz.yData)+"\n");
				 yvalString+="\""+viz.title+','+viz.normalizedDistance+','+ String.join(",", viz.yData)+"\",";
				 if (downloadX){
					 //bx.write(viz.title+','+ String.join(",", viz.xData)+"\n");
					 xvalString+="\""+viz.title+','+ String.join(",", viz.xData)+"\",";
				 }	 
			 }
		 }
		 
		 yJsonString+=yvalString.substring(0, yvalString.length() - 1)+"]";
		 JsonString+=yJsonString;
		 if (downloadX){
			 xJsonString+=xvalString.substring(0, xvalString.length() - 1)+"]";
			 JsonString+=","+xJsonString;
		 }
		 JsonString+="}";
		 System.out.println("JsonString:"+JsonString);
		 return JsonString;
//		 if (downloadX){bx.close();}
//		 by.close();
//		 return "bob";
	}


	/**
	 * @param query
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws InterruptedException
	 */
//	public String getBaselineData(String query) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
//		BaselineQuery bq = new ObjectMapper().readValue(query, BaselineQuery.class);
//		if (!bq.equals(cachedQuery)) {
//			List<LinkedHashMap<String, LinkedHashMap<Float, Float>>> output = new ArrayList<LinkedHashMap<String, LinkedHashMap<Float, Float>>>();
//			for (int i = 0; i < bq.yAxis.size(); i++) {
//				Query q = new Query("query").setGrouby(bq.zAxis + "," + bq.xAxis).setAggregationFunc(bq.aggrFunc)
//						.setAggregationVaribale(bq.yAxis.get(i));
//				setBaselineFilter(q, bq);
//				ExecutorResult executorResult = executor.getData(q);
//				if (executorResult == null)
//					return "";
//
//				output.add(executorResult.output);
//			}
//			Result finalOutput = new Result();
//			finalOutput.method = "Basic search";
//			ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, null, null);
//			chartOutput.baselineOutput(output, bq, finalOutput);
//			cachedResult = finalOutput;
//		}
//		Result response = new Result(cachedResult, bq.pageNum);
//		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(response);
//	}

	public String outlier(String method,String sql,String outliercount) throws IOException{
		return readFile();
	}
	
	LinkedHashMap<String, LinkedHashMap<Float, Float>> cleanUpDataWithAllZeros(LinkedHashMap<String, LinkedHashMap<Float, Float>> output) {
		List<String> toRemove = new ArrayList<String>();
		for (String s : output.keySet()) {
			LinkedHashMap<Float, Float> v = output.get(s);
			int flag = 1;
			for (Float f : v.keySet()) {
				if (v.get(f) != 0) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				toRemove.add(s);
			}
		}
		for (String s: toRemove) {
			output.remove(s);
		}
		return output;
	}


//	public String getDatabaseNames() throws JsonGenerationException, JsonMappingException, IOException{
//		return new ObjectMapper().writeValueAsString(inMemoryDatabases.keySet());
//	}


	public String getInterfaceFomData(String query) throws IOException, InterruptedException, SQLException{
		FormQuery fq = new ObjectMapper().readValue(query,FormQuery.class);
//		this.databaseName = fq.getDatabasename();
		String databaseName = fq.getDatabasename();
		//inMemoryDatabase = inMemoryDatabases.get(this.databaseName);
		String locations[] = sqlQueryExecutor.getMetaFileLocation(databaseName);
				//System.out.println(locations[0]+"\n"+locations[1]);
		Database inMemoryDatabase;
		if(inMemoryDatabases.containsKey(databaseName)){
			inMemoryDatabase = inMemoryDatabases.get(databaseName);
		} else {
			inMemoryDatabase = new Database(databaseName, locations[0], locations[1], false);
			inMemoryDatabases.put(databaseName, inMemoryDatabase);
		}
		//executor = new Executor(inMemoryDatabase);

		buffer = new ObjectMapper().writeValueAsString(inMemoryDatabase.getFormMetdaData());
		System.out.println("BUFFER:" +buffer);
//		System.out.println( new ObjectMapper().writeValueAsString(inMemoryDatabases.get(fq.getDatabasename()).getFormMetdaData()) );
		return buffer;
    }
	
	public String getInterfaceFormData2(String query) throws IOException, InterruptedException, SQLException{
		FormQuery fq = new ObjectMapper().readValue(query,FormQuery.class);
		String databaseName = fq.getDatabasename();
		Database inMemoryDatabase;
		if(inMemoryDatabases.containsKey(databaseName)){
			inMemoryDatabase = inMemoryDatabases.get(databaseName);
		} else {
			inMemoryDatabase = new Database(databaseName, null, null, false);
			inMemoryDatabases.put(databaseName, inMemoryDatabase);
		}
		
		//executor = new Executor(inMemoryDatabase);
		

		buffer = new ObjectMapper().writeValueAsString(inMemoryDatabase.getFormMetdaData());
		System.out.println("BUFFER:" +buffer);
//		System.out.println( new ObjectMapper().writeValueAsString(inMemoryDatabases.get(fq.getDatabasename()).getFormMetdaData()) );
		return buffer;
    }



	/**
	 * @param q
	 * @param arg
	 */
	public void setFilter(Query q, ZvQuery arg) {
		
		if (!arg.filter.equals(""))
			q.setCompositeFilter(arg.filter);
		
		if (arg.predicateValue.equals("")) return;
		Query.Filter filter = new Query.FilterPredicate(arg.predicateColumn,Query.FilterOperator.fromString(arg.predicateOperator),arg.predicateValue);
		q.setFilter(filter);
	}

	public void setBaselineFilter(Query q, BaselineQuery bq) {
		if (bq.predicateValue.equals("")) return;
		Query.Filter filter = new Query.FilterPredicate(bq.predicateColumn, Query.FilterOperator.fromString(bq.predicateOperator), bq.predicateValue);
		q.setFilter(filter);
	}

	public String readFile() throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader("/src/data1.txt"));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}

}
