/**
 * 
 */
package edu.uiuc.zenvisage.service.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
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
		List<List<Double>> clusteredTrends = new ArrayList();
		List<Double> clusterDist = new ArrayList();
		for (int i = 0; i < clusters1.size(); i++) {
			double minDistance = Double.MAX_VALUE;
			int minDistanceIndex = 0;
			for (int j = 0; j < clusters2.size(); j++) {
				double d = distance.calculateDistance(clusters1.get(i).getCenter().getPoint(), clusters2.get(j).getCenter().getPoint());
				clusterDist.add(d);
				if (d < minDistance) {
					minDistance = d;
					minDistanceIndex = j;
				}
			}
			List <DoublePoint> c = clusters1.get(i).getPoints();
			System.out.println("Looping through time series in cluster1:");
			for (int z=0 ; z< c.size();z++){
//				System.out.print(c.get(z).getPoint());
//				System.out.print(c.get(z).getPoint().length);
//				System.out.print(c.get(z).getPoint().toString());
				System.out.print("[");
				for (int zi =0 ; zi < c.get(z).getPoint().length;zi++){
					System.out.print(c.get(z).getPoint()[zi]);
					System.out.print(',');
				}
				System.out.println("]");
//				System.out.println("toString:");
//				System.out.println(c.get(z).getPoint().toString());
			}
				
//			System.out.println("clusters1.get(i).getPoints():   ");
//			System.out.println(clusters1.get(i).getPoints());
//			System.out.println((double[]) clusters1.get(i));
			
			clusters2RealSizes[minDistanceIndex] += clusters1.get(i).getPoints().size();
		}
		System.out.println("clusterDist:");
		System.out.println(clusterDist);
//		System.out.println(Collections.sort(clusterDist));
		Collections.sort(clusterDist);
		List<Double> topk = new ArrayList<Double>(clusterDist.subList(0,clusters2RealSizes[0]));
		//Argsort and insert topk time sereies for each cluster into clusterTrends
//		clusteredTrends[]
		System.out.println("topk:");
		System.out.println(topk);
		System.out.println("clusters2RealSizes:");
		System.out.println(clusters2RealSizes);
		for (int r = 0; r<clusters2RealSizes.length;r++){
			System.out.println(clusters2RealSizes[r]);
		}
		System.out.println("clusters2:");
		System.out.println(clusters2);
		return new DummyCluster(clusters2, clusters2RealSizes);
	}
	
	/* (non-Javadoc)
	 * @see cluster.Clustering#calculateClusters(double, int, double[][])
	 * An alternative second-step clustering method - merge the closest 2 clusters until the number of clusters meets the requirement.
	 */
//	@SuppressWarnings("rawtypes")
//	@Override
	public DummyCluster calculateClustersAlternative(double eps, int k, double[][] normalizedgroups) {
		// TODO Auto-generated method stub
		// eps and k are not used. eps is not required for kmeans and a separate logic is used to derive k
//		KMeansPlusPlusClusterer<DoublePoint> kmeans = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.getOutlierCount()+1, normalizedgroups.length), 15);
		KMeansPlusPlusClusterer<DoublePoint> kmeans = new KMeansPlusPlusClusterer<DoublePoint>(Math.min(this.args.kMeansClusterSize * 5, normalizedgroups.length), 15);
		List<DoublePoint> dataset = new ArrayList<DoublePoint>();
		for(int i = 0; i < normalizedgroups.length; i++) {
			dataset.add(new DoublePoint(normalizedgroups[i]));
		}		
		List<CentroidCluster<DoublePoint>> clusters = kmeans.cluster(dataset);
		
		//second-step clustering
		int reducedClusterNumber = clusters.size() - Math.min(this.args.kMeansClusterSize, clusters.size());
		for (int i = 0; i < reducedClusterNumber; ++i) {
			int jmin = -1;
			int lmin = -1;
			double mindist = Double.POSITIVE_INFINITY;
			for (int j = 0; j < clusters.size(); ++j) {
				DoublePoint pointj = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(j)).getCenter();    
		  	  	double[] pj = pointj.getPoint();
				for (int l = 0; l < j; ++l) {
					DoublePoint pointl = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(l)).getCenter();    
			  	  	double[] pl = pointl.getPoint();
					double d = distance.calculateDistance(pj, pl);
					if (d < mindist) {
						jmin = j;
						lmin = l;
						mindist = d;
					}
				}
			}
			
			DoublePoint pointjmin = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(jmin)).getCenter();    
	  	  	double[] pjmin = pointjmin.getPoint();
	  	  	DoublePoint pointlmin = (DoublePoint) ((CentroidCluster<DoublePoint>) clusters.get(lmin)).getCenter();    
	  	  	double[] plmin = pointlmin.getPoint();
	  	  	
	  	  	int jminSize = clusters.get(jmin).getPoints().size();
	  	  	int lminSize = clusters.get(lmin).getPoints().size();
	  	  	
	  	  	double[] newCenter = new double[pjmin.length];
	  	  	for (int j = 0; j < pjmin.length; ++j) {
	  	  		newCenter[j] = ( pjmin[j] * jminSize + plmin[j] * lminSize ) / (jminSize + lminSize);
	  	  	}
	  	  	
			CentroidCluster<DoublePoint> mergedCluster = new CentroidCluster<DoublePoint>(new DoublePoint(newCenter));
			for (int j = 0; j < jminSize; ++j) {
				mergedCluster.addPoint(clusters.get(jmin).getPoints().get(j));
			}
			for (int l = 0; l < lminSize; ++l) {
				mergedCluster.addPoint(clusters.get(lmin).getPoints().get(l));
			}
			//merge cluster_jmin and cluster_lmin
			clusters.remove(jmin);
			clusters.remove(lmin);
			clusters.add(mergedCluster);
		}
		
		int clustersSizes[] = new int[clusters.size()];
		for (int i = 0; i < clusters.size(); i++) {
			clustersSizes[i] = clusters.get(i).getPoints().size();
		}
		
		return new DummyCluster(clusters, clustersSizes);
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
		System.out.println("computeRepresentativeTrends");
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
		  	  	System.out.println("normalizedGroups[l]:");
				System.out.println(normalizedGroups[l]);
	  	  		if (d < mindist ) {
	  	  			min = l;
	  	  		 	mindist = d;
	  	  		 	
	  	  		}
	  	  	}
	  	  	System.out.println("mappings:");
	  	    System.out.println(mappings);
	  	    
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
