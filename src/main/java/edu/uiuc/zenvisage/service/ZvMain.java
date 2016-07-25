/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.data.roaringdb.executor.ExecutorResult;
import edu.uiuc.zenvisage.model.BaselineQuery;
import edu.uiuc.zenvisage.model.FormQuery;
import edu.uiuc.zenvisage.model.ScatterPlotQuery;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.KMeans;
import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.Original;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
import edu.uiuc.zenvisage.server.UploadHandleServlet;
import edu.uiuc.zenvisage.service.utility.Zscore;
import edu.uiuc.zenvisage.zql.executor.ZQLExecutor;
import edu.uiuc.zenvisage.zql.executor.ZQLTable;

/**
 * @author tarique
 *
 */
public class ZvMain {
	
	private Result cachedResult = new Result();
	private BaselineQuery cachedQuery = new BaselineQuery();
//	private InMemoryDatabase inMemoryDatabase;
	private Map<String,Database> inMemoryDatabases = new HashMap<String,Database>();

	private Database inMemoryDatabase;
	
	public Executor executor = new Executor(inMemoryDatabase);
	public Analysis analysis;
	public Distance distance;
	public Normalization normalization;
	public Normalization outputNormalization;
	public PiecewiseAggregation paa;
	public ArrayList<List<Double>> data;

	public ZvMain() throws IOException, InterruptedException{
		System.out.println("ZVMAIN LOADED");
		loadData();
		
	}
	
	public  void loadData() throws IOException, InterruptedException{
		
//		inMemoryDatabase = createDatabase("income","zenvisage/WEB-INF/classes/data/census_test_schema.txt","zenvisage/WEB-INF/classes/data/census-income-test.csv");
//		inMemoryDatabases.put("income", inMemoryDatabase);
//		inMemoryDatabase = createDatabase("real_estate","zenvisage/WEB-INF/classes/data/real_estate.txt","zenvisage/WEB-INF/classes/data/real_estate.csv");
//		inMemoryDatabases.put("real_estate", inMemoryDatabase);
//		//inMemoryDatabase = createDatabase("iris", "zenvisage/WEB-INF/classes/data/iris_schema.txt","zenvisage/WEB-INF/classes/data/iris_data.csv");
//		//inMemoryDatabases.put("iris", inMemoryDatabase);
//		
//		inMemoryDatabase = createDatabase("seed2", "zenvisage/WEB-INF/classes/data/seed2_schema.txt", "zenvisage/WEB-INF/classes/data/seed2.csv");
//		inMemoryDatabases.put("seed2", inMemoryDatabase);
		
		
		
//		inMemoryDatabase = createDatabase("income","WEB-INF/classes/data/census_test_schema.txt","WEB-INF/classes/data/census-income-test.csv");
//		inMemoryDatabases.put("income", inMemoryDatabase);
//		inMemoryDatabase = createDatabase("real_estate","WEB-INF/classes/data/real_estate.txt","WEB-INF/classes/data/real_estate.csv");
//		inMemoryDatabases.put("real_estate", inMemoryDatabase);
//		//inMemoryDatabase = createDatabase("iris", "zenvisage/WEB-INF/classes/data/iris_schema.txt","zenvisage/WEB-INF/classes/data/iris_data.csv");
//		//inMemoryDatabases.put("iris", inMemoryDatabase);
//		
//		inMemoryDatabase = createDatabase("seed2", "WEB-INF/classes/data/seed2_schema.txt", "WEB-INF/classes/data/seed2.csv");
//		inMemoryDatabases.put("seed2", inMemoryDatabase);
		
		
//		inMemoryDatabase = createDatabase("income","src/main/resources/data/census_test_schema.txt","src/main/resources/data/census-income-test.csv");
//		inMemoryDatabases.put("income", inMemoryDatabase);
//		inMemoryDatabase = createDatabase("real_estate","src/main/resources/data/real_estate.txt","src/main/resources/data/real_estate.csv");
//		inMemoryDatabases.put("real_estate", inMemoryDatabase);
//		//inMemoryDatabase = createDatabase("iris", "zenvisage/WEB-INF/classes/data/iris_schema.txt","zenvisage/WEB-INF/classes/data/iris_data.csv");
//		//inMemoryDatabases.put("iris", inMemoryDatabase);
//		
//		inMemoryDatabase = createDatabase("seed2", "src/main/resources/data/seed2_schema.txt", "src/main/resources/data/seed2.csv");
//		inMemoryDatabases.put("seed2", inMemoryDatabase);
		
		inMemoryDatabase = createDatabase("income","/data/census_test_schema.txt","/data/census-income-test.csv");
		inMemoryDatabases.put("income", inMemoryDatabase);
		inMemoryDatabase = createDatabase("real_estate","/data/real_estate.txt","/data/real_estate.csv");
		inMemoryDatabases.put("real_estate", inMemoryDatabase);
		//inMemoryDatabase = createDatabase("iris", "zenvisage/WEB-INF/classes/data/iris_schema.txt","zenvisage/WEB-INF/classes/data/iris_data.csv");
		//inMemoryDatabases.put("iris", inMemoryDatabase);
		
		inMemoryDatabase = createDatabase("seed2", "/data/seed2_schema.txt", "/data/seed2.csv");
		inMemoryDatabases.put("seed2", inMemoryDatabase);
		
		
		System.out.println("Done loading data");
		
		//inMemoryDatabase = createDatabase("seed2", "zenvisage/WEB-INF/classes/data/seed2_schema.txt", "zenvisage/WEB-INF/classes/data/seed2.csv");
		//inMemoryDatabases.put("seed2", inMemoryDatabase);
		
	/*	inMemoryDatabase = DataLoader.createDatabase("seed", "src/data/seed_schema.txt", "src/data/seed.csv");
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
	
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException {
		UploadHandleServlet uploadHandler = new UploadHandleServlet();
		List<String> names = uploadHandler.upload(request, response);
		
		if (names.size() == 3) {
			System.out.println("successful upload!");
			inMemoryDatabase = createDatabase(names.get(0),"/data/" + names.get(2),"/data/" + names.get(1));
			inMemoryDatabases.put(names.get(0), inMemoryDatabase);
		}
		
		for (String s : inMemoryDatabases.keySet()) {
			System.out.println(s);
		}
	}
			
   public String runZQLCompleteQuery(String zqlQuery) throws IOException, InterruptedException, SQLException{
		  System.out.println(zqlQuery);
	   	  inMemoryDatabase = inMemoryDatabases.get("real_estate");
		  executor = new Executor(inMemoryDatabase);
		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.executor=executor;
		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
     	  String result=new ObjectMapper().writeValueAsString(edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.execute(zqlTable));
     	  System.out.println(result);
     	  return result;
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(ZQLTest.createZQLTable()));
			
		}
   
   public String runZQLQuery(String zqlQuery) throws IOException, InterruptedException{
		  inMemoryDatabase = inMemoryDatabases.get("real_estate");
		  executor = new Executor(inMemoryDatabase);
		  ZQLExecutor.executor=executor;
		  ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery,ZQLTable.class);
  	  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(zqlTable));
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(ZQLTest.createZQLTable()));
			
		}
		
	
	public String getScatterPlot(String query) throws JsonParseException, JsonMappingException, IOException {
		System.out.print(query);
		ScatterPlotQuery q = new ObjectMapper().readValue(query, ScatterPlotQuery.class);
		Map<String, ScatterResult> output = executor.getScatterData(q);
		if (output == null) return "";
		Result finalOutput = new Result();
		finalOutput.method = q.method;
		if (q.method == "ScatterRep") {
			ScatterRep.compute(output, q, finalOutput);
		}
		else {
			ScatterRank.compute(output, q, finalOutput);
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(finalOutput);
	}
	
	
	/* Will be obsolete when the new separated query method is utilized */
	public String runDragnDropInterfaceQuery(String query) throws InterruptedException, IOException{
		// get data from database
		System.out.println(query);
		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
		 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
		 if (args.method.equals("SimilaritySearch"))
			 setFilter(q, args);
		 ExecutorResult executorResult = executor.getData(q);
		 if (executorResult == null) return "";
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;
		 // setup result format
		 Result finalOutput = new Result();
		 finalOutput.method = args.method;
		 //finalOutput.xUnit = inMemoryDatabase.getColumnMetaData(args.xAxis).unit;
		 //finalOutput.yUnit = inMemoryDatabase.getColumnMetaData(args.yAxis).unit;
		 // generate new result for query
		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, executorResult.xMap);
		 // generate the corresponding distance metric
		 if (args.distance_metric.equals("Euclidean")) {
			 distance = new Euclidean();
		 }
		 else {
			 distance = new DTWDistance();
		 }
		 // generate the corresponding data normalization metric
		 if (args.distanceNormalized) {
			 normalization = new Zscore();
//			 normalization = new Original();
		 }
		 else {
			 normalization = new Original();
		 }
		 // generate the corresponding output normalization
		 
		 outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(outputNormalization);
		 double[][] normalizedgroups = dataReformatter.reformatData(output);
		 // generate the corresponding analysis method
		 if (args.method.equals("Outlier")) {
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Outlier(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
		 }
		 else if (args.method.equals("RepresentativeTrends")) {
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Representative(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
		 }
		 else if (args.method.equals("SimilaritySearch")) {
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
			 ((Similarity) analysis).setDescending(false);
		 }
		 else if (args.method.equals("DissimilaritySearch")) {
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
			 ((Similarity) analysis).setDescending(true);
		 }
		 analysis.compute(output, normalizedgroups, args);
		 
		 ObjectMapper mapper = new ObjectMapper();
		 return mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
	}
	
	
	public String runDragnDropInterfaceQuerySeparated(String query, String method) throws InterruptedException, IOException{
		// get data from database
//		System.out.println(query);
		
		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
		 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
		 if (method.equals("SimilaritySearch"))
			 setFilter(q, args);
		 ExecutorResult executorResult = executor.getData(q);
		 if (executorResult == null) return "";
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;		 
		 
		 // setup result format
		 Result finalOutput = new Result();
		 finalOutput.method = method;
		 //finalOutput.xUnit = inMemoryDatabase.getColumnMetaData(args.xAxis).unit;
		 //finalOutput.yUnit = inMemoryDatabase.getColumnMetaData(args.yAxis).unit;
		 // generate new result for query
		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, executorResult.xMap);
		 // generate the corresponding distance metric
		 if (args.distance_metric.equals("Euclidean")) {
			 distance = new Euclidean();
//			 distance = new SegmentationDistance();
		 }
		 else {
			 distance = new DTWDistance();
		 }
		 // generate the corresponding data normalization metric
		 if (args.distanceNormalized) {
			 normalization = new Zscore();
		 }
		 else {
			 normalization = new Original();
		 }
		 // generate the corresponding output normalization
		 
		 outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(outputNormalization);
		 double[][] normalizedgroups = dataReformatter.reformatData(output);
		 // generate the corresponding analysis method
		 if (method.equals("Outlier")) {
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Outlier(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
		 }
		 else if (method.equals("RepresentativeTrends")) {
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Representative(executor,inMemoryDatabase,chartOutput,distance,normalization,cluster,args);
		 }
		 else if (method.equals("SimilaritySearch")) {
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
			 ((Similarity) analysis).setDescending(false);
		 }
		 else if (method.equals("DissimilaritySearch")) {
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter);
			 ((Similarity) analysis).setDescending(true);
		 }
		 analysis.compute(output, normalizedgroups, args);
		 ObjectMapper mapper = new ObjectMapper();
		 
		 String str = mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
//		 System.out.println(str);
		 
		 return str;
	}


	/**
	 * @param query
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws InterruptedException 
	 */
	public String getBaselineData(String query) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		BaselineQuery bq = new ObjectMapper().readValue(query, BaselineQuery.class);
		if (!bq.equals(cachedQuery)) {
			List<LinkedHashMap<String, LinkedHashMap<Float, Float>>> output = new ArrayList<LinkedHashMap<String, LinkedHashMap<Float, Float>>>();
			for (int i = 0; i < bq.yAxis.size(); i++) {
				Query q = new Query("query").setGrouby(bq.zAxis + "," + bq.xAxis).setAggregationFunc(bq.aggrFunc)
						.setAggregationVaribale(bq.yAxis.get(i));
				setBaselineFilter(q, bq);
				ExecutorResult executorResult = executor.getData(q);
				if (executorResult == null)
					return "";

				output.add(executorResult.output);
			}
			Result finalOutput = new Result();
			finalOutput.method = "Basic search";
			ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, null, null);
			chartOutput.baselineOutput(output, bq, finalOutput);
			cachedResult = finalOutput;
		}
		Result response = new Result(cachedResult, bq.pageNum);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	public String outlier(String method,String sql,String outliercount) throws IOException{
		return readFile();
	}
	
	
	public String getDatabaseNames() throws JsonGenerationException, JsonMappingException, IOException{
		return new ObjectMapper().writeValueAsString(inMemoryDatabases.keySet());
	}
	
	
	public String getInterfaceFomData(String query) throws IOException{
		FormQuery fq = new ObjectMapper().readValue(query,FormQuery.class);
		inMemoryDatabase = inMemoryDatabases.get(fq.getDatabasename());
		executor = new Executor(inMemoryDatabase);
		return new ObjectMapper().writeValueAsString(inMemoryDatabases.get(fq.getDatabasename()).getFormMetdaData());
	}
	
	/**
	 * @param q
	 * @param arg
	 */
	public void setFilter(Query q, ZvQuery arg) {
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
