/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.BiMap;

import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.data.roaringdb.executor.ExecutorResult;
import edu.uiuc.zenvisage.model.Sketch;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.utility.ChartOutput;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;


/**
 *
 */
public class Similarity extends Analysis {
	/* Whether rank trends in descending order */
	public boolean descending = true;
	public PiecewiseAggregation paa;
	public DataReformation dataReformatter;

	public Similarity(Executor executor, Database inMemoryDatabase,
			ChartOutput chartOutput, Distance distance, Normalization normalization, PiecewiseAggregation paa, ZvQuery args, DataReformation dataReformatter) {
		super(executor, inMemoryDatabase, chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		this.paa = paa;
		this.dataReformatter = dataReformatter;
	}

	/* (non-Javadoc)
	 * @see analyze.Analysis#getAnalysis()
	 */
	@Override
	public void generateAnalysis(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups) throws JsonProcessingException {
		// TODO Auto-generated method stub
		Sketch[] sketchPoints = args.getSketchPoints();
		ArrayList<String> mappings = new ArrayList<String>();
		for(String key : output.keySet()) {
			mappings.add(key);
		}
		List<List<Integer>> orders = new ArrayList<List<Integer>>();
		List<double[][]> data = new ArrayList<double[][]>();
		List<LinkedHashMap<String, LinkedHashMap<Float, Float>>> outputs = new ArrayList<LinkedHashMap<String, LinkedHashMap<Float, Float>>>();
		List<BiMap<Float,String>> xMaps = new ArrayList<BiMap<Float,String>>();
		for (int i = 0; i < sketchPoints.length; i++) {
			if (sketchPoints[i].points.isEmpty()) {
				if (i < sketchPoints.length - 1) {
					sketchPoints[i] = sketchPoints[i+1];
					sketchPoints[i+1].points.clear();
				}
				else continue;
			}
			Query  q = new Query("query").setGrouby(args.groupBy+","+sketchPoints[i].xAxis).setAggregationFunc(sketchPoints[i].aggrFunc).setAggregationVaribale(sketchPoints[i].aggrVar);
			setFilter(q, args);
			try {
				ExecutorResult executorResult = executor.getData(q);
				output = executorResult.output;
				outputs.add(output);
				xMaps.add(executorResult.xMap.inverse());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Set<Float> ignore = new HashSet<Float>();
			paa.setPAAwidth(output,sketchPoints[i]);
			double[][] normalizedgroup = paa.applyPAAonData(output,ignore,sketchPoints[i]);
			double[][] normalgroup = this.dataReformatter.reformatData(output);
			data.add(normalgroup);
			double[] queryTrend = paa.applyPAAonQuery(ignore,sketchPoints[i]);
			List<Integer> order = computeOrders(normalizedgroup,queryTrend,mappings);
			orders.add(order);
		}
		List<Integer> ranks = computeWeightedRanks(orders);
		chartOutput.chartOutput(data, outputs, ranks, mappings, xMaps, chartOutput.args, chartOutput.finalOutput);
		return;
	}
	
	/**
	 * @param normalizedgroups
	 * @param queryTrend
	 * @param mappings
	 * @return
	 */
	public List<Integer> computeOrders(double[][] normalizedgroups, double[] queryTrend, ArrayList<String> mappings) {
		List<Integer> orders = new ArrayList<Integer>();
		MultiValueMap indexOrder =new MultiValueMap();
    	List<Double> distances = new ArrayList<Double>(); 
    	for(int i = 0;i < normalizedgroups.length;i++) {
    		double dist = distance.calculateDistance(normalizedgroups[i], queryTrend);
    	    distances.add(dist);	
    		indexOrder.put(dist,i);
    	}   
	  
    	Collections.sort(distances);
    	//System.out.println(distances.size());
    	if (descending)
    		Collections.reverse(distances);
    	for(Double d : distances){
    		@SuppressWarnings("rawtypes")
			ArrayList values = (ArrayList)indexOrder.get(d);
		    Integer val = (Integer) values.get(0);
			orders.add((val));
			indexOrder.remove(d,val);
			 
		 }
    	//System.out.println(orders.size());
    	return orders;		
	}
	
	/**
	 * @param orders
	 * @return
	 */
	private List<Integer> computeWeightedRanks(List<List<Integer>> orders) {
		if (orders.size() == 0) return null;

		HashMap<Integer, Integer> indexOrder = new HashMap<Integer, Integer>();
		List<Integer> totalOrder = new ArrayList<Integer>(orders.get(0));
		for (int i = 1; i < orders.size(); i++) {
			List<Integer> order = orders.get(i);
			for (int j = 0; j < order.size(); j++) {
				int c = totalOrder.get(j);
				c += order.get(j);
				totalOrder.set(j, c);
			}
		}
		List<Integer> ranks = new ArrayList<Integer>(totalOrder);
		Collections.sort(totalOrder);
		for (int i = 0; i < totalOrder.size(); i++) {
			indexOrder.put(totalOrder.get(i), i);
		}
		for (int i = 0; i < ranks.size(); i++) {
			int c = ranks.get(i);
			int val = indexOrder.get(c);
			ranks.set(i, val);
		}
		return ranks;
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
	
	/**
	 * @param q
	 * @param arg
	 */
	public void setFilter(Query q, ZvQuery arg) {
		if (arg.predicateValue.equals("")) return;
		Query.Filter filter = new Query.FilterPredicate(arg.predicateColumn,Query.FilterOperator.fromString(arg.predicateOperator),arg.predicateValue);
		q.setFilter(filter);
	}

}
