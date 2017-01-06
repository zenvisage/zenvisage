package edu.uiuc.zenvisage.service.cluster;
/**
 * @author tarique
 *
 */
public class OutlierTrend {
	private double[] p;
	private String key;
	private int similarTrendsCount;
	private double weightedDistance;
	
	public double[] getP() {
		return p;
	}
	public void setP(double[] p) {
		this.p = p;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getSimilarTrends() {
		return similarTrendsCount;
	}
	public void setSimilarTrends(int similarTrends) {
		this.similarTrendsCount = similarTrends;
	}
	
	public void setWeightedDistance(double weightedDistance) {
		this.weightedDistance = weightedDistance;
	}
	
	public double getWeightedDistance() {
		return weightedDistance;
	}
	
	

}
