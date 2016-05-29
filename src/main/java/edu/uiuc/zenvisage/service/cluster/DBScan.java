/**
 * 
 */
package edu.uiuc.zenvisage.service.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.distance.Distance;
/**
 * @author tarique
 *
 */
public class DBScan extends Clustering {

	/**
	 * @param distance
	 * @param normalization
	 * @param args
	 */
	public DBScan(Distance distance,
			Normalization normalization, ZvQuery args) {
		super(distance, normalization,
				args);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cluster.Clustering#calculateClusters(double, int, double[][])
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List calculateClusters(double eps, int k, double[][] normalizedgroups) {
		// TODO Auto-generated method stub
		DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(eps, k);
		
		List<DoublePoint> dataset = new ArrayList<DoublePoint>();
		for(int i = 0; i < normalizedgroups.length; i++){
			dataset.add(new DoublePoint(normalizedgroups[i]));
		}
		List<Cluster<DoublePoint>> clusters = dbscan.cluster(dataset);
		
		return clusters;
	}

	/* (non-Javadoc)
	 * @see cluster.Clustering#computeRepresentativeTrends(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public double[][] computeRepresentativeTrends(List clusters) {
		// TODO Auto-generated method stub
		double[][] representativeTrends = new double[clusters.size()][];
	    Median median = new Median();
	    for(int k = 0; k < clusters.size(); k++) {	    	  
	    	List<DoublePoint> points = ((Cluster<DoublePoint>) clusters.get(k)).getPoints();
	    	int dimension = points.get(0).getPoint().length;
	    	double[] representativeTrend = new double[dimension];	 
	    	for(int i = 0; i < dimension; i++) {
	    		double[] temp = new double[points.size()];
	    		for(int j = 0; j < points.size(); j++) {
	    			double[] point = points.get(j).getPoint();
	    			temp[j] = point[i];	    			  
	    		}
	    		representativeTrend[i] = median.evaluate(temp);	  
	    	}
	    	representativeTrends[k] = representativeTrend; 	  
	    }
	    return representativeTrends;
	}

	@Override
	public List<RepresentativeTrend> computeRepresentativeTrends(List clusters,
			ArrayList<String> mappings, double[][] normalizedGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[][] getCenters(List clusters) {
		// TODO Auto-generated method stub
		return null;
	}

}
