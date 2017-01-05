/**
 *
 */
package edu.uiuc.zenvisage.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
/**
 * @author tarique
 *
 */
public class Similarity extends Analysis {
	/* Whether rank trends in descending order */
	public boolean descending = true;
	//public PiecewiseAggregation paa;
	public DataReformation dataReformatter;
	double[] interpolatedQuery = null;
	double[][] overlappedAndInterpolatedQuery = null;
	boolean considerRange;

	public Similarity(ChartOutputUtil chartOutput, 
			Distance distance, Normalization normalization, PiecewiseAggregation paa, ZvQuery args, DataReformation dataReformatter, double[] interpolatedQuery) {
		super(chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		//this.paa = paa;
		this.dataReformatter = dataReformatter;
		this.interpolatedQuery = interpolatedQuery;
		this.considerRange = false;
	}
	
	public Similarity(ChartOutputUtil chartOutput, Distance distance, Normalization normalization, ZvQuery args, DataReformation dataReformatter, double[][] overlappedAndInterpolatedQuery) {
		super(chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		//this.paa = paa;
		this.dataReformatter = dataReformatter;
		this.overlappedAndInterpolatedQuery = overlappedAndInterpolatedQuery;
		this.considerRange = true;
	}

	/* (non-Javadoc)
	 * @see analyze.Analysis#getAnalysis()
	 */
	@Override
	public void compute(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups, ZvQuery args) throws JsonProcessingException {
		// TODO Auto-generated method stub
		Sketch[] sketchPoints = args.getSketchPoints();

		ArrayList<String> mappings = new ArrayList<String>();
		for(String key : output.keySet()) {
			mappings.add(key);
		}
		List<List<Integer>> orders = new ArrayList<List<Integer>>();
		List<List<Double>> orderedDistances = new ArrayList<List<Double>>();

		List<double[][]> data = new ArrayList<double[][]>();
		
		// O(N*P^2)
		//List<LinkedHashMap<String, LinkedHashMap<Float, Float>>> outputs = new ArrayList<LinkedHashMap<String, LinkedHashMap<Float, Float>>>();
		List<BiMap<Float,String>> xMaps = new ArrayList<BiMap<Float,String>>();

//		for (int i = 0; i < sketchPoints.length; i++) {
//			if (sketchPoints[i].points.isEmpty()) {
//				if (i < sketchPoints.length - 1) {
//					sketchPoints[i] = sketchPoints[i+1];
//					sketchPoints[i+1].points.clear();
//				}
//				else continue;
//			}
//			Query  q = new Query("query").setGrouby(args.groupBy+","+sketchPoints[i].xAxis).setAggregationFunc(sketchPoints[i].aggrFunc).setAggregationVaribale(sketchPoints[i].aggrVar);
//			setFilter(q, args);
//			try {
//				ExecutorResult executorResult = executor.getData(q);
//				output = executorResult.output;
//				outputs.add(output);
//				xMaps.add(executorResult.xMap.inverse());
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//outputs.add(output);

//			Set<Float> ignore = new HashSet<Float>();
//			paa.setPAAwidth(output,sketchPoints[i]);
//			double[][] normalizedgroup = paa.applyPAAonData(output,ignore,sketchPoints[i]);
//			data.add(normalizedgroups);
//			double[] queryTrend = paa.applyPAAonQuery(ignore,sketchPoints[i]);

//			double[][][] overlappedDataAndQueries = getOverlappedData(output, args);

//			ListPair lp = computeOrders(overlappedDataAndQueries[0], overlappedDataAndQueries[1], mappings, args);
			ListPair lp = computeOrders(normalizedgroups, mappings, args);
			//orders.add(lp.order);
			//orderedDistances.add(lp.distances);
//		}

		//ListPair lp = computeWeightedRanks(orders, orderedDistances);
		chartOutput.chartOutput(normalizedgroups, output, lp.order, lp.distances, mappings, xMaps, chartOutput.args, chartOutput.finalOutput);
		return;
	}

	public class ListPair {
		List<Integer> order;
		List<Double> distances;
		ListPair(List<Integer> order, List<Double> distances) {
			this.order = order;
			this.distances = distances;
		}
	}
	/**
	 * @param normalizedgroups
	 * @param queryTrend
	 * @param mappings
	 * @return
	 */
	
	public ListPair computeOrders(double[][] normalizedgroups, ArrayList<String> mappings, ZvQuery args) {
		List<Integer> orders = new ArrayList<Integer>();		
		List<Double> orderedDistances = new ArrayList<Double>();
				
		MultiValueMap indexOrder =new MultiValueMap();
    	List<Double> distances = new ArrayList<Double>();

//    	List<Float> queryXValues = new ArrayList<Float>();
//    	List<Float> queryYValues = new ArrayList<Float>();    	
//    	
//    	List<Point> queryPoints = args.sketchPoints[0].points;
//		
//		for (int i = 0; i < queryPoints.size(); i++) {
//			if (queryPoints.get(i).getX() >= args.xRange[0] && queryPoints.get(i).getX() <= args.xRange[1]) {
//				queryXValues.add((float) queryPoints.get(i).getX());
//				queryYValues.add((float) queryPoints.get(i).getY());
//			}
//		}
//		
//		double[] queryData = this.dataReformatter.getInterpolatedData(queryXValues, queryYValues, normalizedgroups[0].length);
		
    	for(int i = 0;i < normalizedgroups.length;i++) {
    		double dist;
//    			for (int j = 0; j < overlappedDataInterpolated[i].length; j++) {
//    				System.out.println(overlappedDataInterpolated[i][j] + "\t" + overlappedQueryInterpolated[i][j]);
//    			}
//    			System.out.println();
    		if (this.considerRange) {
    			if (normalizedgroups[i].length == 0 || this.overlappedAndInterpolatedQuery[i].length == 0) {
    				dist = Double.MAX_VALUE;
    			}
    			else {
        			dist = distance.calculateDistance(normalizedgroups[i], this.overlappedAndInterpolatedQuery[i]);    				
    			}
    		}
    		else {
    			if (normalizedgroups[i].length == 0) {
    				dist = Double.MAX_VALUE;
    			}
    			else {
    				dist = distance.calculateDistance(normalizedgroups[i], this.interpolatedQuery);
    			}
    		}
    		
    		
    	    distances.add(dist);	
    		indexOrder.put(dist,i);
    	}

    	Collections.sort(distances);
    	if (descending)
    		Collections.reverse(distances);
    	for(Double d : distances){
    		@SuppressWarnings("rawtypes")
			ArrayList values = (ArrayList)indexOrder.get(d);
		    Integer val = (Integer) values.get(0);
			orders.add((val));
			orderedDistances.add(d);
			indexOrder.remove(d,val);

		 }
    	ListPair lp = new ListPair(orders, orderedDistances);
    	return lp;
	}



	/**
	 * @param orders
	 * @return
	 */
	private ListPair computeWeightedRanks(List<List<Integer>> orders, List<List<Double>> orderedDistances) {
		if (orders.size() == 0) return null;

		HashMap<Integer, Integer> indexOrder = new HashMap<Integer, Integer>();
		List<Integer> totalOrder = new ArrayList<Integer>(orders.get(0));
		List<Double> totalDistances = new ArrayList<Double>(orderedDistances.get(0));
		for (int i = 1; i < orders.size(); i++) {
			List<Integer> order = orders.get(i);
			List<Double> totalD = orderedDistances.get(i);
			for (int j = 0; j < order.size(); j++) {
				int c = totalOrder.get(j);
				double d = totalDistances.get(j);
				c += order.get(j);
				d += totalD.get(j);
				totalOrder.set(j, c);
				totalDistances.set(j, d);
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
		ListPair lp = new ListPair(ranks, totalDistances);
		return lp;
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
