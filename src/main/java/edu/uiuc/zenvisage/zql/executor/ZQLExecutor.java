package edu.uiuc.zenvisage.zql.executor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.data.roaringdb.executor.ExecutorResult;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.Normalization;

import edu.uiuc.zenvisage.service.utility.Zscore;

/**
 * @author tarique
 *
 */
public class ZQLExecutor {
	public static Executor executor;
	
	public static Result execute(ZQLTable zQLTable) throws InterruptedException, JsonProcessingException{
    ZQLTableResult zQLTableResult = new ZQLTableResult();
    int rowCount=0;
    for(ZQLRow zQLRow :zQLTable.getZqlRows()){
    	    rowCount++;
	    	ZQLRowResult zqlRowResult = new ZQLRowResult();
	    	zQLTableResult.getZqlRowResults().put(zQLRow.getName(),zqlRowResult);

	    	getVisualisations(zQLRow,zqlRowResult,zQLTableResult);
	    	
	    	if(zQLRow.getProcesse()!=null)
	    		executeProcess(zQLRow,zqlRowResult,zQLTableResult);
	    
	    	if(zQLRow.getProcesse()!=null && rowCount==zQLTable.getZqlRows().size()){
	    		zQLRow.getZ().clear();
	    		zQLRow.getZ().add("_"+zQLRow.getName()+"p");
	    		zQLRow.setName("x");
	    		ZQLRowResult zqlRowResultT = new ZQLRowResult();
		    	zQLTableResult.getZqlRowResults().put(zQLRow.getName(),zqlRowResultT);
		    	getVisualisations(zQLRow,zqlRowResultT,zQLTableResult);
	    		return executeOutput(zqlRowResultT,zQLTableResult);   
	    		
	    	}
	    	
	    	
	    	if(rowCount==zQLTable.getZqlRows().size() || zQLRow.isOutput())
	    		return executeOutput(zqlRowResult,zQLTableResult);       
		}
	
       return null; 		
	}
		
	
	
	
	public static void getVisualisations(ZQLRow zqlRow,ZQLRowResult zqlRowResult,ZQLTableResult zQLTableResult) throws InterruptedException{
	 // I assume X has always one value which will be true for the user study. Need to fix this later.
	   

	   String z = zqlRow.getZ().get(0);
	 
	   if("sketch".equals(z)){
		   System.out.println(z+"inside");
		   zqlRowResult.setSketchPoints(zqlRow.getSketchPoints());
		   zqlRowResult.setSketch(true);
		   return;
	   }
	   List<ZQLRowVizResult> zqlRowVizResults = new ArrayList<ZQLRowVizResult>();
	   zqlRowResult.setZqlRowVizResults(zqlRowVizResults);
	   
	   List<String> zvalues= new ArrayList<>();
	   if(z.startsWith("_")){
		   zvalues.addAll(getZValueFromProcessesAbove(zQLTableResult,z.substring(1,z.length()-1)));
		   z=getZTypeFromProcessesAbove(zQLTableResult,z.substring(1,z.length()-1));
	   }

		for (String x : zqlRow.getX())
			for (String y : zqlRow.getY()) {
				ZQLRowVizResult zQLRowVizResult = new ZQLRowVizResult();
				Query q = new Query("query").setGrouby(z + "," + x).setAggregationFunc("avg").setAggregationVaribale(y);
				setFilter(q, zqlRow.getConstraints());
				ExecutorResult executorResult = executor.getData(q);
				if (executorResult == null)
					return;

				LinkedHashMap<String, LinkedHashMap<Float, Float>> output = executorResult.output;
				if (zvalues.size() > 0) {
					   LinkedHashMap<String, LinkedHashMap<Float, Float>> newoutput = new  LinkedHashMap<String, LinkedHashMap<Float, Float>>();
					   for(String zvalue:zvalues){
						   LinkedHashMap<Float, Float> results = output.get(zvalue);
						   newoutput.put(zvalue, results);
					   }
					   output=newoutput;
				   }
				   
				   zQLRowVizResult.setVizData(output);
				   zQLRowVizResult.setX(x);
				   zQLRowVizResult.setY(y);
				   zQLRowVizResult.setZ(z);
				   zqlRowVizResults.add(zQLRowVizResult);		
			}
	 		
		   return;
	}

	private static List<String> getZValueFromProcessesAbove(ZQLTableResult zQLTableResult, String rowID) {
		return zQLTableResult.getZqlRowResults().get(rowID).getZqlProcessResult().getzValues(); 
	
	}
	
	private static String getZTypeFromProcessesAbove(ZQLTableResult zQLTableResult, String rowID) {
		return zQLTableResult.getZqlRowResults().get(rowID).getZqlProcessResult().getzType(); 
	
	}

	
	public static void executeProcess(ZQLRow zQLRow,ZQLRowResult zqlRowResult, ZQLTableResult zQLTableResult) throws JsonProcessingException{
		Processe processe = zQLRow.getProcesse();
		String function=processe.getFunction();		
		ZQLAnalysis analysis=null;;
		Distance distance=new Euclidean();
		Normalization normalization= new Zscore();;
		ZQLPiecewiseAggregation paa;
		Database inMemoryDatabase=executor.getDatabase();
		 // reformat database data
	  
		// generate the corresponding analysis method
        if (function.equals("similar")) {
			 paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			 analysis = new ZQLSimilarity(inMemoryDatabase,distance,normalization,paa);
			 ((ZQLSimilarity) analysis).setDescending(false);
		 }
		 else if (function.equals("dissimilar")) {
			 paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			 analysis = new  ZQLSimilarity(inMemoryDatabase,distance,normalization,paa);
			 ((ZQLSimilarity) analysis).setDescending(true);
		 }
		 else if (function.equals("incTrends")) {
			 paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			 analysis = new  ZQLIncreasingTrends(inMemoryDatabase,distance,normalization,paa);
			 ((ZQLIncreasingTrends) analysis).setDescending(true);
			
		 }
		 else if (function.equals("decTrends")) {
			 paa = new ZQLPiecewiseAggregation(normalization, inMemoryDatabase);
			 analysis = new  ZQLIncreasingTrends(inMemoryDatabase,distance,normalization,paa);
			 ((ZQLIncreasingTrends) analysis).setDescending(false);
		 }
		 else{
			 System.out.println("Wrong Processing Function: "+function);
		 }
       zqlRowResult.setZqlProcessResult(analysis.generateAnalysis(zQLRow,zQLTableResult));
			
	}
	  
	public static Result executeOutput(ZQLRowResult zQLRowResult, ZQLTableResult zQLTableResult) throws JsonProcessingException{
		return ZQLChartOutput.chartOutput(zQLRowResult);
		
	}
	
	private static void setFilter(Query q, List<Constraints> constraints) {
		if(constraints.size()>0){
			Constraints constraint = constraints.get(0);
			Query.Filter filter = new Query.FilterPredicate(constraint.getKey(),Query.FilterOperator.fromString(constraint.getOperator()),constraint.getValue());
			q.setFilter(filter);
		}
		
	}
	
}
