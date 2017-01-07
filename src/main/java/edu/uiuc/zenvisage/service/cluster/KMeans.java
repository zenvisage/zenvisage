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
 * @author tarique
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
	public DummyCluster calculateClusters(double eps, int k, double[][] normalizedgroups) {
		// TODO Auto-generated method stub
		// eps and k are not used. eps is not required for kmeans and a separate logic is used to derive k
//		KMeansPlusPlusClusterer<DoublePoint> kmeans = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.getOutlierCount()+1, normalizedgroups.length), 15);
		KMeansPlusPlusClusterer<DoublePoint> kmeans1 = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.kMeansClusterSize * 3, normalizedgroups.length), 15);
		List<DoublePoint> dataset1 = new ArrayList<DoublePoint>();
		for(int i = 0; i < normalizedgroups.length; i++) {
			dataset1.add(new DoublePoint(normalizedgroups[i]));
		}		
		List<CentroidCluster<DoublePoint>> clusters1 = kmeans1.cluster(dataset1);
		
		KMeansPlusPlusClusterer<DoublePoint> kmeans2 = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.kMeansClusterSize, clusters1.size()), 15);
		List<DoublePoint> dataset2 = new ArrayList<DoublePoint>();
		for (int i = 0; i < clusters1.size(); i++) {	    
	    	DoublePoint point = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters1.get(i)).getCenter();    
	  	  	double[] p = point.getPoint();
	  	  	int min = 0;
	  	  	double mindist = distance.calculateDistance(p, normalizedgroups[0]);
	  	  	for (int j = 1; j < normalizedgroups.length; j++) {
	  	  		double d = distance.calculateDistance(p, normalizedgroups[j]);
	  	  		if (d < mindist ) {
	  	  			min = j;
	  	  		 	mindist = d;
	  	  		}
	  	  	}
	  	  	dataset2.add(new DoublePoint(normalizedgroups[min]));
	    }
		List<CentroidCluster<DoublePoint>> clusters2 = kmeans2.cluster(dataset2);
		
		int clusters2Sizes[] = new int[clusters2.size()];		
		int clusters2RealSizes[] = new int[clusters2.size()];
		for (int i = 0; i < clusters2.size(); i++) {
			clusters2Sizes[i] = clusters2.get(i).getPoints().size();
			clusters2RealSizes[i] = 0;
		}
		
		List<CentroidCluster<DoublePoint>> clustersFinal = new ArrayList<CentroidCluster<DoublePoint>>();
		for (int i = 0; i < clusters2.size(); i++) {
			clustersFinal.add(new CentroidCluster<DoublePoint>(null));
		}
		
		for (int i = 0; i < clusters1.size(); i++) {
			double minDistance = Double.MAX_VALUE;
			int minDistanceIndex = 0;
			for (int j = 0; j < clusters2.size(); j++) {
				double d = distance.calculateDistance(clusters1.get(i).getCenter().getPoint(), clusters2.get(j).getCenter().getPoint());
				if (d < minDistance) {
					minDistance = d;
					minDistanceIndex = j;
				}
			}
			clusters2RealSizes[minDistanceIndex] += clusters1.get(i).getPoints().size();
		}
		
		return new DummyCluster(clusters2, clusters2RealSizes);
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
