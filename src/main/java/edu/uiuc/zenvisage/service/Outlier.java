/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import edu.uiuc.zenvisage.service.utility.Normalization;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.DummyCluster;
import edu.uiuc.zenvisage.service.cluster.OutlierTrend;
import  edu.uiuc.zenvisage.service.cluster.RepresentativeTrend;
import  edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;

/**
 *
 */
/**
 * @author xiaofo
 *
 */
public class Outlier extends Analysis {
	/**
	  * Return the indexes correspond to the top-k smallest in an array.
	  */
	public static int[] minKIndex(List<Double> array, int top_k) {
	    double[] min = new double[top_k];
	    int[] minIndex = new int[top_k];
	    Arrays.fill(min, Double.POSITIVE_INFINITY);
	    Arrays.fill(minIndex, -1);

	    top: for(int i = 0; i < array.size(); i++) {
	        for(int j = 0; j < top_k; j++) {
	            if(array.get(i) < min[j]) {
	                for(int x = top_k - 1; x > j; x--) {
	                    minIndex[x] = minIndex[x-1]; min[x] = min[x-1];
	                }
	                minIndex[j] = i; min[j] = array.get(i);
	                continue top;
	            }
	        }
	    }
	    return minIndex;
	}
	/**
	 * Cluster class for clustering.
	 */
	public Clustering cluster;
	
	public Outlier(ChartOutputUtil chartOutput, 
			Distance distance, Normalization normalization, Clustering cluster, ZvQuery args) {
		super(chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		this.cluster = cluster;
	}

	/* (non-Javadoc)
	 * @see analyze.Analysis#getAnalysis()
	 */
	/* (non-Javadoc)
	 * @see analysis.Analysis#generateAnalysis(java.util.LinkedHashMap, double[][])
	 */
	@Override
	public void compute(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups, ZvQuery args) throws JsonProcessingException {
		// TODO Auto-generated method stub
		ArrayList<String> mappings = new ArrayList<String>();
		for(String key : output.keySet()) {
			 mappings.add(key);
		}
//		double eps = cluster.calculateEpsDistance(normalizedgroups, 2);
		double eps = 0; //unused in calculateClusters
		@SuppressWarnings("rawtypes")
		DummyCluster dc = cluster.calculateClusters(eps, 2, normalizedgroups);
		List clusters = dc.getClusters();
		
		List<OutlierTrend> outlierTrends = computeOutliers(clusters,normalizedgroups,mappings);
		
		Collections.sort(outlierTrends, new Comparator<OutlierTrend>() {

	        public int compare(OutlierTrend o1, OutlierTrend o2) {
	            if(o1.getWeightedDistance() < o2.getWeightedDistance())
	            		return 1;
	            else if (o1.getWeightedDistance() > o2.getWeightedDistance())
	            	    return -1;
	            else
	            	    return 0;
	        }
	    });
		///////
		double[] normalizedDistances= new double[normalizedgroups.length];
		Arrays.fill(normalizedDistances, 0);
		
		for (int k = 0; k < clusters.size(); k++) {
			DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();
	  	  	double[] p = point.getPoint();
			List<Double> clusterDist =  new ArrayList();
	  	  	for (int l = 1; l < normalizedgroups.length; l++) {
	  	  		double d = distance.calculateDistance(p, normalizedgroups[l]);
	  	  		clusterDist.add(d);
	  	  	}
	  	  	
			int [] minK_idx = minKIndex(clusterDist,dc.getRealSizes()[k]);
			double sumDist = 0;
			double min = Double.MAX_VALUE;
			// Looping through everything in this cluster
			for (int i =0 ; i<minK_idx.length ; i++){
				double dist = distance.calculateDistance(p, normalizedgroups[minK_idx[i]]);
				if (dist <= min) {
					min = dist;
				}
				sumDist+= dist;
			}
			double average = sumDist/dc.getRealSizes()[k];
			double normalizedDistance  = (average-min)/average;
			normalizedDistances[k] += normalizedDistance ;	
	    }
		for (int i=0;i<outlierTrends.size();i++){
			outlierTrends.get(i).setNormalizedDistance(normalizedDistances[i]);
		}
		outlierTrends = outlierTrends.subList(0, args.kmeansClusterSize);
		chartOutput.chartOutput(outlierTrends,output,chartOutput.args,chartOutput.finalOutput, 1);
	}

	
	/**
	 * @param clusters
	 * @param normalizedgroups
	 * @param mappings
	 * @return outliers
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<OutlierTrend> computeOutliers(List clusters, double[][] normalizedgroups, ArrayList<String> mappings) {
		List<OutlierTrend> outliers = new ArrayList<OutlierTrend>();
		double[][] centerPoints = new double[clusters.size()][];
		int[] centerPointsSize = new int[clusters.size()];
		for (int k = 0; k < clusters.size(); k++) {
			DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();
			centerPoints[k] = point.getPoint();
			centerPointsSize[k] = ((CentroidCluster<DoublePoint>) clusters.get(k)).getPoints().size();
		}
		// cluster's outlier and its index in normalizedgroups
		double[] weightedDistances = new double[normalizedgroups.length];
		Arrays.fill(weightedDistances, 0);
		
		double[] normalizedDistances= new double[normalizedgroups.length];
		Arrays.fill(normalizedDistances, 0);
		for (int i = 0; i < normalizedgroups.length; i++) {
			for (int j = 0; j < centerPoints.length; j++) {
				double dist = distance.calculateDistance(normalizedgroups[i], centerPoints[j]);
				weightedDistances[i] += dist * centerPointsSize[j];
			}
		}
		
		// add the outliers into result
		for (int i = 0; i < normalizedgroups.length; i++) {
			OutlierTrend outlierTrend = new OutlierTrend();
			outlierTrend.setP(normalizedgroups[i]);
			outlierTrend.setKey(mappings.get(i));
			outlierTrend.setWeightedDistance(weightedDistances[i]);
			outliers.add(outlierTrend);
		}
		return outliers;
	}

	
	/**
	 * @param normalizedgroups
	 * @param represetativeTrends
	 * @return orders
	 */
	public List<Integer> computeOrders(double [][] normalizedgroups,double[][] represetativeTrends) {
		List<Integer> orders = new ArrayList<Integer>();
		MultiValueMap indexOrder = new MultiValueMap();
	
	    List<Double> distances = new ArrayList<Double>(); 
		for(int i = 0; i < normalizedgroups.length; i++){
			double min = 1000000;
			for(int j = 0; j < represetativeTrends.length; j++){
				double dist = distance.calculateDistance(normalizedgroups[i], represetativeTrends[j]);
				if(dist < min){
					min = dist;
				}
			}
			distances.add(min);	
			indexOrder.put(min,i);
		}   
		  
		Collections.sort(distances);
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

}
