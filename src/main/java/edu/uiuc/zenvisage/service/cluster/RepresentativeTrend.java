package edu.uiuc.zenvisage.service.cluster;
/**
 * @author tarique
 *
 */
public class RepresentativeTrend {
	private double[] p;
	private String key;
	private int similarTrendsCount;
	
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
	
	

}
