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
	 * Cluster class for clustering.
	 */
	public Clustering cluster;
	
	public Outlier(Executor executor, Database inMemoryDatabase,
			ChartOutputUtil chartOutput, Distance distance, Normalization normalization, Clustering cluster, ZvQuery args) {
		super(executor, inMemoryDatabase, chartOutput, distance, normalization, args);
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
		double eps = cluster.calculateEpsDistance(normalizedgroups, 2);
		@SuppressWarnings("rawtypes")
		List clusters = cluster.calculateClusters(eps*2, 2, normalizedgroups);
		//double[][] representativeTrends = cluster.computeRepresentativeTrends(clusters);
		//List<Integer> orders = computeOrders(normalizedgroups,representativeTrends);
		//chartOutput.chartOutput(normalizedgroups,output,orders,mappings,chartOutput.args,chartOutput.finalOutput);
		/*
		List<RepresentativeTrend> representativeTrends = new ArrayList<RepresentativeTrend>();
	    for (int k = 0; k < clusters.size(); k++) {	    
	    	RepresentativeTrend repTrend = new RepresentativeTrend();
	    	DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();    
	  	  	double[] p = point.getPoint();
	  	  	int max = 0;
	  	  	double maxdist = distance.calculateDistance(p, normalizedgroups[0]);
	  	  	for (int l = 1; l < normalizedgroups.length; l++) {
	  	  		double d = distance.calculateDistance(p, normalizedgroups[l]);
	  	  		if (d > maxdist ) {
	  	  			max = l;
	  	  		 	maxdist = d;
	  	  		 	
	  	  		}
	  	  	}
	  	  	repTrend.setP(normalizedgroups[max]);
	  	  	repTrend.setKey(mappings.get(max));
	  	  	repTrend.setSimilarTrends( ((CentroidCluster<DoublePoint>) clusters.get(k)).getPoints().size());
	  	    representativeTrends.add(repTrend);
	    }*/
		List<RepresentativeTrend> representativeTrends = computeOutliers(clusters,normalizedgroups,mappings);
		Collections.sort(representativeTrends, new Comparator<RepresentativeTrend>() {

	        public int compare(RepresentativeTrend o1, RepresentativeTrend o2) {
	            if(o1.getSimilarTrends() < o2.getSimilarTrends())
	            		return -1;
	            else if (o1.getSimilarTrends() > o2.getSimilarTrends())
	            	    return 1;
	            else
	            	    return 0;
	        }
	    });
		chartOutput.chartOutput(representativeTrends,output,chartOutput.args,chartOutput.finalOutput);
	}

	
	/**
	 * @param clusters
	 * @param normalizedgroups
	 * @param mappings
	 * @return outliers
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<RepresentativeTrend> computeOutliers(List clusters, double[][] normalizedgroups, ArrayList<String> mappings) {
		List<RepresentativeTrend> outliers = new ArrayList<RepresentativeTrend>();
		double[][] centerPoints = new double[clusters.size()][];
		int[] centerPointsSize = new int[clusters.size()];
		for (int k = 0; k < clusters.size(); k++) {
			DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();
			centerPoints[k] = point.getPoint();
			centerPointsSize[k] = ((CentroidCluster<DoublePoint>) clusters.get(k)).getPoints().size();
		}
		// cluster's outlier and its index in normalizedgroups
		double[] maxDistance = new double[clusters.size()];
		Arrays.fill(maxDistance, 0);
		int[] maxIndex = new int[clusters.size()];
		// now compute which cluster every data point belongs to
		for (int i = 0; i < normalizedgroups.length; i++) {
			double min = Double.MAX_VALUE;
			int minIndex = -1;
			for (int j = 0; j < centerPoints.length; j++) {
				double dist = distance.calculateDistance(normalizedgroups[i], centerPoints[j]);
				if (dist <= min) {
					min = dist;
					minIndex = j;
				}
			}
			if (min >= maxDistance[minIndex]) {
				maxIndex[minIndex] = i;
				maxDistance[minIndex] = min;
			}
		}
		// add the outliers into result
		for (int i = 0; i < clusters.size(); i++) {
			RepresentativeTrend repTrend = new RepresentativeTrend();
			repTrend.setP(normalizedgroups[maxIndex[i]]);
			repTrend.setKey(mappings.get(maxIndex[i]));
			repTrend.setSimilarTrends(centerPointsSize[i]);
			outliers.add(repTrend);
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
