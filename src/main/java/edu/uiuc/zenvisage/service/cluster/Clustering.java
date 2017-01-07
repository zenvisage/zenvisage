/**
 * 
 */
package edu.uiuc.zenvisage.service.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uiuc.zenvisage.service.utility.Normalization;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.distance.Distance;
/**
 * @author tarique
 *
 */
public abstract class Clustering {
	
	public Distance distance;
	public Normalization normalization;
	public ZvQuery args;
	
	/**
	 * Constructor of Cluster class.
	 * @param distance
	 * @param normalization
	 * @param args
	 */
	public Clustering(Distance distance,
			Normalization normalization, ZvQuery args) {
		super();
		this.distance = distance;
		this.normalization = normalization;
		this.args = args;
	}
	/**
	 * Method to calculate eps param for clustering.
	 * @param normalizedgroups
	 * @param k
	 * @return eps
	 */
	public double calculateEpsDistance(double[][] normalizedgroups, int k) {
		double[][] distValues = new double[normalizedgroups.length][];
		double[] kthDistances = new double[normalizedgroups.length];
		for (int row = 0; row < normalizedgroups.length; row++) {
			distValues[row] = new double[normalizedgroups.length];
			for (int col = 0; col < normalizedgroups.length; col++)
				distValues[row][col] = 0.0;
		}
		
		for (int row = 0; row < normalizedgroups.length; row++) {    
			distValues[row][row] = 0.0;
		    for (int col = row + 1; col < normalizedgroups.length; col++) {
		      
		    	
		      distValues[row][col] = distance.calculateDistance(normalizedgroups[row],normalizedgroups[col]);
		      distValues[col][row] = distValues[row][col];
		    }
		    Arrays.sort(distValues[row]);
		    kthDistances[row] = distValues[row][k-1];
		}
		Median median = new Median();
		return median.evaluate(kthDistances);
	}
	
	/**
	 * Method of clustering
	 * @param eps
	 * @param k
	 * @param normalizedgroups
	 * @return list of clusters
	 */
	@SuppressWarnings("rawtypes")
	public abstract DummyCluster calculateClusters(double eps, int k, double[][] normalizedgroups);
	
	/**
	 * @param clusters
	 * @return representativeTrends
	 */
	@SuppressWarnings("rawtypes")
	public abstract double[][] computeRepresentativeTrends(List clusters);
	
	public abstract List<RepresentativeTrend> computeRepresentativeTrends(List clusters,ArrayList<String> mappings,double[][] normalizedGroups);
	
	public abstract double[][] getCenters(@SuppressWarnings("rawtypes") List clusters);
}