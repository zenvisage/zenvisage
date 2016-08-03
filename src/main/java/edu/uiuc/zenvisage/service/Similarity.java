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
	public PiecewiseAggregation paa;
	public DataReformation dataReformatter;

	public Similarity(Executor executor, Database inMemoryDatabase,
			ChartOutputUtil chartOutput, Distance distance, Normalization normalization, PiecewiseAggregation paa, ZvQuery args, DataReformation dataReformatter) {
		super(executor, inMemoryDatabase, chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		this.paa = paa;
		this.dataReformatter = dataReformatter;
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
//			paa.setPAAwidth(output,sketchPoints[i]);
//			double[][] normalizedgroup = paa.applyPAAonData(output,ignore,sketchPoints[i]);
			data.add(normalizedgroups);
//			double[] queryTrend = paa.applyPAAonQuery(ignore,sketchPoints[i]);

			double[][][] overlappedDataAndQueries = getOverlappedData(output, args);
			
			ListPair lp = computeOrders(overlappedDataAndQueries[0], overlappedDataAndQueries[1], mappings, args);
			orders.add(lp.order);
			orderedDistances.add(lp.distances);
		}

		ListPair lp = computeWeightedRanks(orders, orderedDistances);
		chartOutput.chartOutput(data, outputs, lp.order, lp.distances, mappings, xMaps, chartOutput.args, chartOutput.finalOutput);
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
	
	public double[][][] getOverlappedData(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, ZvQuery args) {		
		double[][][] overlappedDataAndQueries = new double[2][output.size()][];
		double[] xRange = args.xRange;
		List<Double> overlappedQueryXValues = new ArrayList<Double>();
		List<Double> overlappedQueryYValues = new ArrayList<Double>();
		List<Point> queryPoints = args.sketchPoints[0].points;
		
		for (int i = 0; i < queryPoints.size(); i++) {
			if (queryPoints.get(i).getX() >= xRange[0] && queryPoints.get(i).getX() <= xRange[1]) {
				overlappedQueryXValues.add((double) queryPoints.get(i).getX());
				overlappedQueryYValues.add((double) queryPoints.get(i).getY());
			}
		}
		
		int i = 0;
		for (String s: output.keySet()) {
			Map<Float,Float> originalData = output.get(s);
			List<Double> overlappedXValues = new ArrayList<Double>();
			List<Double> overlappedYValues = new ArrayList<Double>();
			for (float dataX : originalData.keySet()) {
				if (xRange[0] <= dataX && dataX <= xRange[1]) {
					overlappedXValues.add((double) dataX);
					overlappedYValues.add((double) originalData.get(dataX));
				}
			}
//			System.out.println(overlappedXValues.size());
			if (overlappedXValues.size() <= 1) {
				overlappedDataAndQueries[0][i] = new double[0];
				overlappedDataAndQueries[1][i] = new double[0];
			}
			else {
				double[] overlappedRange = {overlappedXValues.get(0), overlappedXValues.get(overlappedXValues.size()-1)};
				double[] overlappedDataInterpolated = getInterpolatedData(overlappedXValues, overlappedYValues, overlappedRange);
				double[] overlappedQueryInterpolated = getInterpolatedData(overlappedQueryXValues, overlappedQueryYValues, overlappedRange);
				overlappedDataAndQueries[0][i] = overlappedDataInterpolated;
				overlappedDataAndQueries[1][i] = overlappedQueryInterpolated;				
			}
			i++;
		}
			
		return overlappedDataAndQueries;
	}
	
	public double[] getInterpolatedData(List<Double> overlappedXValues, List<Double> overlappedYValues, double[] overlappedRange) {
		int n = 100;
		double[] interpolatedXValues = new double[n];
		double[] interpolatedYValues = new double[n];
		double granularity = (overlappedXValues.get(overlappedXValues.size()-1) - overlappedXValues.get(0)) / 100;
		
//		interpolatedXValues[0] = overlappedXValues.get(0);
//		interpolatedYValues[0] = overlappedYValues.get(0);
//		interpolatedXValues[n-1] = overlappedXValues.get(overlappedXValues.size()-1);
//		interpolatedXValues[n-1] = overlappedYValues.get(overlappedXValues.size()-1);
		
		int count = 0;
		for (int i = 0; i < n; i++) {
			double interpolatedX = overlappedXValues.get(0) + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(overlappedXValues.get(count) < interpolatedX) {
				count++;
			}
			if (overlappedXValues.get(count) == interpolatedX) {
				interpolatedYValues[i] = overlappedYValues.get(count);
			}
			else {
				double xDifference = overlappedXValues.get(count) - overlappedXValues.get(count-1);
				double yDifference = overlappedYValues.get(count) - overlappedYValues.get(count-1);
				interpolatedYValues[i] = overlappedYValues.get(count - 1) + (interpolatedX - overlappedXValues.get(count-1)) / xDifference * yDifference;				
			}
		}
		return interpolatedYValues;
	}
	
	/**
	 * @param normalizedgroups
	 * @param queryTrend
	 * @param mappings
	 * @return
	 */
	
	public ListPair computeOrders(double[][] overlappedDataInterpolated, double[][] overlappedQueryInterpolated, ArrayList<String> mappings, ZvQuery args) {
		List<Integer> orders = new ArrayList<Integer>();		
		List<Double> orderedDistances = new ArrayList<Double>();
				
		MultiValueMap indexOrder =new MultiValueMap();
    	List<Double> distances = new ArrayList<Double>();
		
    	for(int i = 0;i < overlappedDataInterpolated.length;i++) {
    		double dist;
    		if (overlappedDataInterpolated[i].length == 0) {
    			dist = Double.MAX_VALUE;
    		}
    		else {
//    			for (int j = 0; j < overlappedDataInterpolated[i].length; j++) {
//    				System.out.println(overlappedDataInterpolated[i][j] + "\t" + overlappedQueryInterpolated[i][j]);
//    			}
//    			System.out.println();
        		dist = distance.calculateDistance(overlappedDataInterpolated[i], overlappedQueryInterpolated[i]);
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
