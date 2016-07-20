/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import edu.uiuc.zenvisage.service.utility.Normalization;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.data.roaringdb.executor.Executor;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.RepresentativeTrend;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.model.*;


/**
 *
 */
public class Representative extends Analysis {
	/**
	 * Cluster class for clustering.
	 */
	public Clustering cluster;

	/**
	 * @param executor
	 * @param inMemoryDatabase
	 * @param chartOutput
	 * @param distance
	 * @param normalization
	 * @param cluster
	 */
	public Representative(Executor executor, Database inMemoryDatabase,
			ChartOutputUtil chartOutput, Distance distance, Normalization normalization, Clustering cluster, ZvQuery args) {
		super(executor, inMemoryDatabase, chartOutput, distance, normalization, args);
		// TODO Auto-generated constructor stub
		this.cluster = cluster;
	}

	/* (non-Javadoc)
	 * @see analyze.Analysis#getAnalysis()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void compute(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, double[][] normalizedgroups, ZvQuery args) throws JsonProcessingException {
		// TODO Auto-generated method stub
		ArrayList<String> mappings = new ArrayList<String>();
		for(String key : output.keySet()) {
			 mappings.add(key);
		}
		double eps = cluster.calculateEpsDistance(normalizedgroups, 2);
		@SuppressWarnings({ "rawtypes"})
		List clusters = cluster.calculateClusters(eps, 0, normalizedgroups);
		//List<RepresentativeTrend> representativeTrends = cluster.computeRepresentativeTrends(clusters,mappings,normalizedgroups);
		//removeDuplicate(representativeTrends);
//double[][] centers = cluster.getCenters(clusters);
		
		List<RepresentativeTrend> representativeTrends = new ArrayList<RepresentativeTrend>();
	    for (int k = 0; k < clusters.size(); k++) {	    
	    	RepresentativeTrend repTrend = new RepresentativeTrend();
	    	DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();    
	  	  	double[] p = point.getPoint();
	  	  	int min = 0;
	  	  	double mindist = distance.calculateDistance(p, normalizedgroups[0]);
	  	  	for (int l = 1; l < normalizedgroups.length; l++) {
	  	  		double d = distance.calculateDistance(p, normalizedgroups[l]);
	  	  		if (d < mindist ) {
	  	  			min = l;
	  	  		 	mindist = d;
	  	  		 	
	  	  		}
	  	  	}
	  	  	repTrend.setP(normalizedgroups[min]);
	  	  	repTrend.setKey(mappings.get(min));
	  	  	repTrend.setSimilarTrends( ((CentroidCluster<DoublePoint>) clusters.get(k)).getPoints().size());
	  	    representativeTrends.add(repTrend);
	    }
	  	  	
	    Collections.sort(representativeTrends, new Comparator<RepresentativeTrend>() {

	        public int compare(RepresentativeTrend o1, RepresentativeTrend o2) {
	            if(o1.getSimilarTrends() < o2.getSimilarTrends())
	            	return 1;
	            else if (o1.getSimilarTrends() > o2.getSimilarTrends())
	            	return -1;
	            else
	            	return 0;
	        }
	    });	

	
		chartOutput.chartOutput(representativeTrends,output,chartOutput.args,chartOutput.finalOutput);
	}
	/*
	private void removeDuplicate(List<RepresentativeTrend> representativeTrends) {
		int count = 0;
		for (int i = representativeTrends.size() - 1; i >= 0; i--) {
			if (count > 1)
				break;
			RepresentativeTrend src = representativeTrends.get(i);
			for (int j = i - 1; j >= 0; j--) {
				RepresentativeTrend tar = representativeTrends.get(j);
				double dist = distance.calculateDistance(src.getP(), tar.getP());
				if (dist < 1000) {
					count++;
					representativeTrends.remove(i);
					break;
				}
			}
		}
	}*/

}
