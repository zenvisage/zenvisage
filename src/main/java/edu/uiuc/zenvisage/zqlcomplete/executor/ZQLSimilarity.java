/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.Normalization;

/**
 *
 */
public class ZQLSimilarity extends ZQLAnalysis {
	/* Whether rank trends in descending order */
	public boolean descending = true;
	public ZQLPiecewiseAggregation paa;
	public int k=20;

	public ZQLSimilarity( Database inMemoryDatabase, Distance distance, Normalization normalization, ZQLPiecewiseAggregation paa) {
		super(inMemoryDatabase, distance, normalization);
		// TODO Auto-generated constructor stub
		this.paa = paa;
	}


	//little clunky, we need to clean this up, only works for single xyz type visualization for a row
	public ZQLRowProcessResult generateAnalysis(ZQLRow zqlRow,ZQLTableResult zqlTableResult) throws JsonProcessingException {
		
		// get the process column specified by the user
		Processe processe = zqlRow.getProcesse();
		List<String> parameters = processe.getArguments();
		String v1=parameters.get(0);
		String v2=parameters.get(1);
		//if(parameters.containsKey("viz2") && parameters.get("viz2")!=null)
			//v2=parameters.get("viz2");
		int k=10;
		
		// get the number of top results desired
		if(!zqlRow.getProcesse().getCount().equals(""))
			k=Integer.parseInt(zqlRow.getProcesse().getCount().replace("'",""));
		
		ZQLRowVizResult queryViz=null;
		ZQLRowVizResult dataViz=null;
		Sketch sketchPoints=null;
		
		
		// get the first visualization type xyz from the corresponding row
		if(zqlTableResult.getZqlRowResults().get(v1).isSketch()){
			sketchPoints=zqlTableResult.getZqlRowResults().get(v1).getSketchPoints();
			dataViz=zqlTableResult.getZqlRowResults().get(v2).getZqlRowVizResults().get(0);
		}
		else if(zqlTableResult.getZqlRowResults().get(v2).isSketch()){
			sketchPoints=zqlTableResult.getZqlRowResults().get(v1).getSketchPoints();
			dataViz=zqlTableResult.getZqlRowResults().get(v1).getZqlRowVizResults().get(0);
		}
		else if(zqlTableResult.getZqlRowResults().get(v1).getZqlRowVizResults().get(0).getVizData().size()>zqlTableResult.getZqlRowResults().get(v2).getZqlRowVizResults().get(0).getVizData().size())
		{
			queryViz=zqlTableResult.getZqlRowResults().get(v2).getZqlRowVizResults().get(0);
			dataViz=zqlTableResult.getZqlRowResults().get(v1).getZqlRowVizResults().get(0);
		}
		else{
			queryViz=zqlTableResult.getZqlRowResults().get(v1).getZqlRowVizResults().get(0);
			dataViz=zqlTableResult.getZqlRowResults().get(v2).getZqlRowVizResults().get(0);
		}
	
		// Retrieve the data points from dataviz
		LinkedHashMap<String, LinkedHashMap<Float, Float>> output = dataViz.getVizData();
		ArrayList<String> mappings = new ArrayList<String>();
		
		// Retrieve a list of z values from the original viz
		for(String key : output.keySet()) {
			 mappings.add(key);
		}
		
		
		Set<Float> ignore = new HashSet<Float>();
		paa.setPAAwidth(output,dataViz.getX());
		double[][] normalizedgroup = paa.applyPAAonData(output,ignore,dataViz.getX());
		List<Integer> orders=new ArrayList<>();
		if(sketchPoints!=null){
			double[] queryTrend = paa.applyPAAonQuery(ignore,sketchPoints);
			double [][] queryTrends = new double[1][];
			queryTrends[0]=queryTrend;
		    orders = computeOrders(normalizedgroup,queryTrends,mappings);
		}else{
			double[][] queryTrends = paa.applyPAAonData(queryViz.getVizData(),ignore,queryViz.getX());
			 orders = computeOrders(normalizedgroup,queryTrends,mappings);
		}
			
		ZQLRowProcessResult zQLRowProcessResult= new ZQLRowProcessResult();
		zQLRowProcessResult.setzType(dataViz.getZ());
	    for(int i=0;i<k;i++){
	    	if(orders.size()>i)
	    	zQLRowProcessResult.getzValues().add(mappings.get(orders.get(i)));
	    }
		
		return zQLRowProcessResult;
	}
	
	/**
	 * @param normalizedgroups
	 * @param queryTrend
	 * @param mappings
	 * @return
	 */
	public List<Integer> computeOrders(double[][] normalizedgroups, double[][] queryTrend, ArrayList<String> mappings) {
		List<Integer> orders = new ArrayList<Integer>();
		MultiValueMap indexOrder =new MultiValueMap();
    	List<Double> distances = new ArrayList<Double>(); 
    	
    	
    	if(queryTrend.length<normalizedgroups.length){
	    	for(int i = 0;i < normalizedgroups.length;i++) {
	    		double dist=0;
	    		for(int j = 0;j < queryTrend.length;j++) {
	    		dist += distance.calculateDistance(normalizedgroups[i], queryTrend[j]);
	    		}
	    		distances.add(dist);	
	    		indexOrder.put(dist,i);
	    		System.out.println(mappings.get(i)+"\t"+dist);
	    	}   
    	}
    	else if(queryTrend.length==normalizedgroups.length){
    		double dist=0;
    		for(int i = 0;i < normalizedgroups.length;i++) {
    			dist = distance.calculateDistance(normalizedgroups[i], queryTrend[i]);
    			distances.add(dist);	
        		indexOrder.put(dist,i);
    		}
    	}
	  
    	Collections.sort(distances);
    	if (descending)
    		Collections.reverse(distances);
    	for(Double d : distances){
    		@SuppressWarnings("rawtypes")
			ArrayList values = (ArrayList)indexOrder.get(d);
		    Integer val = (Integer) values.get(0);
			orders.add((val));
			indexOrder.remove(d,val);
			 
		 }
    	return orders;		
	}

	/**
	 * @return the descending
	 */
	public boolean isDescending() {
		return descending;
	}

	/**
	 * @param descending the descending to set
	 */
	public void setDescending(boolean descending) {
		this.descending = descending;
	}

}