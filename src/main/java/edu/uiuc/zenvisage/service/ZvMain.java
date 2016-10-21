/**
 *
 */
package edu.uiuc.zenvisage.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
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
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
import edu.uiuc.zenvisage.server.UploadHandleServlet;
import edu.uiuc.zenvisage.service.utility.Zscore;
import edu.uiuc.zenvisage.zql.executor.ZQLExecutor;
import edu.uiuc.zenvisage.zql.executor.ZQLTable;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowResult;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowVizResult;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryGraph;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.ZQLParser;
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

	private Database inMemoryDatabase;

	public Executor executor = new Executor(inMemoryDatabase);
	public Analysis analysis;
	public Distance distance;
	public Normalization normalization;
	public Normalization outputNormalization;
	public PiecewiseAggregation paa;
	public ArrayList<List<Double>> data;
	public String databaseName;
	public String buffer = null;

	public ZvMain() throws IOException, InterruptedException{
		System.out.println("ZVMAIN LOADED");
		loadData();
	}

	public  void loadData() throws IOException, InterruptedException{

//		inMemoryDatabase = createDatabase("real_estate","/data/real_estate.txt","/data/real_estate.csv");
//		inMemoryDatabases.put("real_estate", inMemoryDatabase);
//
//
//		inMemoryDatabase = createDatabase("cmu", "/data/cmuwithoutidschema.txt", "/data/fullcmuwithoutid.csv");
//		inMemoryDatabases.put("cmu", inMemoryDatabase);
//		
//
//		inMemoryDatabase = createDatabase("cmutesting", "/data/cmuhaha.txt", "/data/cmuhaha.csv");
//		inMemoryDatabases.put("cmutesting", inMemoryDatabase);
//
//		inMemoryDatabase = createDatabase("sales", "/data/sales.txt", "/data/sales.csv");
//		inMemoryDatabases.put("sales", inMemoryDatabase);
//
//		System.out.println("Done loading data");
	}

	public static Database createDatabase(String name,String schemafile,String datafile) throws IOException, InterruptedException{
    	Database database = new Database(name,schemafile,datafile);
    	return database;

    }

	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException, SQLException {
		
		UploadHandleServlet uploadHandler = new UploadHandleServlet();
		List<String> names = uploadHandler.upload(request, response);
		if (names.size() == 3) {
			System.out.println("successful upload! "+ names.get(0) +" "+names.get(2) + " "+  names.get(1));
			SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();

			/*insert zenvisage_metafilelocation*/
			
			String locationTupleSQL = "INSERT INTO zenvisage_metafilelocation (database, metafilelocation, csvfilelocation) VALUES "+
					"('" + names.get(0) +"', '"+ names.get(2)+"', '"+ names.get(1)+"');";
			if(sqlQueryExecutor.insert(locationTupleSQL, "zenvisage_metafilelocation", "database", names.get(0))){
				System.out.println("Metafilelocation Data successfully inserted into Postgres");
			} else {
				System.out.println("Metafilelocation already exists!");
			}
			
			/*insert zenvisage_metatable*/
			SchemeToMetatable schemeToMetatable = new SchemeToMetatable();
			if(sqlQueryExecutor.insert(schemeToMetatable.schemeFileToMetaSQLStream(names.get(2), names.get(0)), "zenvisage_metatable", "tablename",  names.get(0))){
				System.out.println("MetaType Data successfully inserted into Postgres");
			} else {
				System.out.println("MetaType already exists!");
			}
			
			/*create csv table*/
			if(!sqlQueryExecutor.isTableExists(names.get(0))){
				sqlQueryExecutor.createTable(schemeToMetatable.createTableSQL);
				sqlQueryExecutor.insertTable(names.get(0), names.get(1), schemeToMetatable.columns);
				System.out.println(names.get(0) + " not exists! Created " + names.get(0) + " from "+names.get(1));
			} else {
				System.out.println(names.get(0) + " exists! Can't create " + names.get(0) + " from "+names.get(1));
			}
			
			System.out.println("HERE:"+names.get(0) +" "+ names.get(2) + " "+ names.get(1));
			inMemoryDatabase = createDatabase(names.get(0), names.get(2), names.get(1));
			
			
//			inMemoryDatabases.put(names.get(0), inMemoryDatabase);
		}
	}

   public String runZQLCompleteQuery(String zqlQuery) throws IOException, InterruptedException, SQLException{
		  System.out.println(zqlQuery);
//	   	  inMemoryDatabase = inMemoryDatabases.get("real_estate");
		  executor = new Executor(inMemoryDatabase);
		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.executor=executor;
		  edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
     	  String result=new ObjectMapper().writeValueAsString(edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor.execute(zqlTable));
     	  System.out.println(result);
     	  return result;
//		  return new ObjectMapper().writeValueAsString(ZQLExecutor.execute(ZQLTest.createZQLTable()));

		}

   public String runQueryGraph(String zqlQuery) throws IOException, InterruptedException{
	   System.out.println(zqlQuery);
	   edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery, edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
	   ZQLParser parser = new ZQLParser();
	   QueryGraph graph = parser.processZQLTable(zqlTable);
	   VisualComponentList output = edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryGraphExecutor.execute(graph);
	   //convert it into front-end format.
	   String result = new ObjectMapper().writeValueAsString(convertVCListtoVisualOutput(output));
	   System.out.println(" Query Graph Execution Results Are:");
	   System.out.println(result);
	   return result;
   }

   
   public Result convertVCListtoVisualOutput(VisualComponentList vcList){
			Result finalOutput=new Result();
			int outputLength = 50;
			// List<ZQLRowVizResult> orig = zqlRowResult.getZqlRowVizResults() ;
			Normalization outputNormalization = new Original();
			 // reformat database data
			 DataReformation dataReformatter = new DataReformation(outputNormalization);
//			 // double[][] output  = dataReformatter.reformatData(orig);
//			
//			List<Iterator<Entry<String, LinkedHashMap<Float, Float>>>> iteratorList= new ArrayList<>();
//			List<String> xs= new ArrayList<>();
//			List<String> ys= new ArrayList<>();
//			List<String> zs= new ArrayList<>();
//			
//			
//			for (ZQLRowVizResult output  : orig) {
//				xs.add(output.getX());
//				ys.add(output.getY());
//				zs.add(output.getZ());
//				Set<Entry<String, LinkedHashMap<Float, Float>>> vizentryset = output.getVizData().entrySet();
//				Iterator<Entry<String, LinkedHashMap<Float, Float>>> it = vizentryset.iterator();
//				iteratorList.add(it);
//			}
//					
//				
//			for(int i = 0; i < Math.min(orig.get(0).getVizData().size(), outputLength); i++) {
//					// initialize a new chart
//				int j = 0;
//				
//			
//				for(Iterator<Entry<String, LinkedHashMap<Float, Float>>> it:iteratorList){
//					Entry<String, LinkedHashMap<Float, Float>> entry = it.next();
//					String zvalue=entry.getKey();
//					Set<Entry<Float, Float>> innerkeyset = entry.getValue().entrySet();
//					if(innerkeyset.size()<0)
//						continue;
//					Iterator<Entry<Float, Float>> innerit = innerkeyset.iterator();
//					Chart chartOutput = new Chart();
//					chartOutput.setxType((i+1)+" : "+zvalue);
//					chartOutput.setyType("avg"+"("+ys.get(j)+")");
//					while(innerit.hasNext()){
//						Entry<Float, Float> innerentry = innerit.next();
//						Float xvalue=innerentry.getKey();		
//						Float yvalue=innerentry.getValue();		
//						chartOutput.xData.add(Float.toString(xvalue));
//						chartOutput.yData.add(Float.toString(yvalue));
//					}
//
//					j++;
//					finalOutput.outputCharts.add(chartOutput);
//					
//				}
//			}
		//VisualComponentList -> Result. Only care about the outputcharts
		int i = 0;
	    for(VisualComponent viz : vcList.getVisualComponentList()) {
	    	Chart outputChart = new Chart();
	    	outputChart.setxType((++i) + " : " + viz.getZValue().getStrValue());
	    	outputChart.setyType("avg" + "(" + vcList.getYtype() + ")");
	    	outputChart.title = "From Query Graph";
	    	for(WrapperType xValue : viz.getPoints().getXList()) {
	    		outputChart.xData.add(xValue.getStrValue());
	    	}
	    	for(WrapperType yValue : viz.getPoints().getYList()) {
	    		outputChart.yData.add(yValue.getStrValue());
	    	}
	    	finalOutput.outputCharts.add(outputChart);
	    }
		return finalOutput;
		
	 }

   
   
   public String runZQLQuery(String zqlQuery) throws IOException, InterruptedException{
//		  inMemoryDatabase = inMemoryDatabases.get("real_estate");
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


	public String runDragnDropInterfaceQuerySeparated(String query, String method) throws InterruptedException, IOException, SQLException{
		// get data from database
//		System.out.println(query);

		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
		 
		 Query q = new Query("query").setGrouby(args.groupBy+","+args.xAxis).setAggregationFunc(args.aggrFunc).setAggregationVaribale(args.aggrVar);
		 if (method.equals("SimilaritySearch"))
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
		 SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
		 //sqlQueryExecutor.ZQLQuery(Z, X, Y, table, whereCondition);
		 sqlQueryExecutor.ZQLQueryEnhanced(q.getZQLRow(), this.databaseName);
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output =  sqlQueryExecutor.getVisualComponentList().toInMemoryHashmap();
		 
		 

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
		 if (args.distanceNormalized) {
//			 normalization = new LinearNormalization();
			 normalization = new Zscore();
//			 normalization = new Original();
		 }
		 else {
			 normalization = new Zscore();
//			 normalization = new Original();
		 }
		 // generate the corresponding output normalization

		 outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(normalization);
		 double[][] normalizedgroups;
		 
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> temp = new LinkedHashMap<String, LinkedHashMap<Float, Float>>();
		 for (String s: output.keySet()) {
			 if (output.get(s).size() >= 2) {
				 temp.put(s, output.get(s));
			 }
		 }
//		 for (String s: output.keySet()) {
//			 if (s.equals("class-08775001") || s.equals("class-15750001")) {
//				 temp.put(s, output.get(s));
//			 }
//		 }
		 output = temp;
		 
		 // generate the corresponding analysis method
		 if (method.equals("Outlier")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Outlier(executor,inMemoryDatabase,chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("RepresentativeTrends")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Representative(executor,inMemoryDatabase,chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("SimilaritySearch")) {
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 
			 if (args.considerRange) {
				 double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args);
				 normalizedgroups = overlappedDataAndQueries[0];
				 double[][] overlappedQuery = overlappedDataAndQueries[1]; 
				 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter, overlappedQuery);
			 }
			 else {
				 normalizedgroups = dataReformatter.reformatData(output);
				 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length);			 
				 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 }
			 
			 ((Similarity) analysis).setDescending(false);
		 }
		 else { //(method.equals("DissimilaritySearch"))
			 paa = new PiecewiseAggregation(normalization, args, inMemoryDatabase);
			 
			 if (args.considerRange) {
				 double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args);
				 normalizedgroups = overlappedDataAndQueries[0];
				 double[][] overlappedQuery = overlappedDataAndQueries[1]; 
				 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter, overlappedQuery);				 
			 }
			 else {
				 normalizedgroups = dataReformatter.reformatData(output);
				 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length);			 
				 analysis = new Similarity(executor,inMemoryDatabase,chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 }
			 ((Similarity) analysis).setDescending(true);
		 }
		 
		 analysis.compute(output, normalizedgroups, args);
		 ObjectMapper mapper = new ObjectMapper();

		 return mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
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


//	public String getDatabaseNames() throws JsonGenerationException, JsonMappingException, IOException{
//		return new ObjectMapper().writeValueAsString(inMemoryDatabases.keySet());
//	}


	public String getInterfaceFomData(String query) throws IOException, InterruptedException, SQLException{
		FormQuery fq = new ObjectMapper().readValue(query,FormQuery.class);
		this.databaseName = fq.getDatabasename();
		//inMemoryDatabase = inMemoryDatabases.get(this.databaseName);
		executor = new Executor(inMemoryDatabase);
		String locations[] = new SQLQueryExecutor().getMetaFileLocation(databaseName);
		System.out.println(locations[0]+"\n"+locations[1]);
		inMemoryDatabase = createDatabase(this.databaseName, locations[0], locations[1]);
		buffer = new ObjectMapper().writeValueAsString(inMemoryDatabase.getFormMetdaData());
		System.out.println(buffer);
//		System.out.println( new ObjectMapper().writeValueAsString(inMemoryDatabases.get(fq.getDatabasename()).getFormMetdaData()) );
		return buffer;
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
