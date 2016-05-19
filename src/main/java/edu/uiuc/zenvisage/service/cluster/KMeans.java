/**
 * 
 */
package edu.uiuc.zenvisage.service.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.service.distance.Distance;

/**
 *
 */
/**
 * @author xiaofo
 *
 */
/**
 * @author xiaofo
 *
 */
public class KMeans extends Clustering {

	/**
	 * @param distance
	 * @param normalization
	 * @param args
	 */
	public KMeans(Distance distance,
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
		KMeansPlusPlusClusterer<DoublePoint> kmeans = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.getOutlierCount()+1, normalizedgroups.length), 15);
		List<DoublePoint> dataset = new ArrayList<DoublePoint>();
		for(int i = 0;i < normalizedgroups.length; i++) {
			dataset.add(new DoublePoint(normalizedgroups[i]));
		}		
		List<CentroidCluster<DoublePoint>> clusters = kmeans.cluster(dataset);
		return clusters;
	}
	
	/**
	 * @param clusters
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public double[][] getCenters(List clusters) {
		double[][] centers = new double[clusters.size()][];
		for (int i = 0; i < clusters.size(); i++) {
			DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(i)).getCenter();
			centers[i] = point.getPoint();
		}
		return centers;
	}

	/* (non-Javadoc)
	 * @see cluster.Clustering#computeRepresentativeTrends(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<RepresentativeTrend> computeRepresentativeTrends(List clusters,ArrayList<String> mappings,double[][] normalizedGroups) {
		// TODO Auto-generated method stub
	//	double[][] representativeTrends = new double[clusters.size()][];
		List<RepresentativeTrend> representativeTrends = new ArrayList<RepresentativeTrend>();
	    for (int k = 0; k < clusters.size(); k++) {	    
	    	RepresentativeTrend repTrend = new RepresentativeTrend();
	    	DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(k)).getCenter();    
	  	  	double[] p = point.getPoint();
	  	  	int min = 0;
	  	  	double mindist = distance.calculateDistance(p, normalizedGroups[0]);
	  	  	for (int l = 1; l < normalizedGroups.length; l++) {
	  	  		double d = distance.calculateDistance(p, normalizedGroups[l]);
	  	  		if (d < mindist ) {
	  	  			min = l;
	  	  		 	mindist = d;
	  	  		 	
	  	  		}
	  	  	}
	  	  	repTrend.setP(normalizedGroups[min]);
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
	    
	    return representativeTrends;
	
	}

	/* (non-Javadoc)
	 * @see cluster.Clustering#computeRepresentativeTrends(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public double[][] computeRepresentativeTrends(List clusters) {
		// TODO Auto-generated method stub
		double[][] representativeTrends = new double[clusters.size()][];
		for (int i = 0; i < clusters.size(); i++) {
			DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(i)).getCenter();
			representativeTrends[i] = point.getPoint();
		}
		return representativeTrends;
	}
}
