/**
 * 
 */
package edu.uiuc.zenvisage.data.roaringdb.executor;

/**
 * @author xiaofo
 *
 */
public class GroupByData {
	
	private String name;
	private double[] xData;
	private double[] yData;
	/**
	 * @param name - name of the product group by z-axis
	 * @param xData - array of values in x-axis
	 * @param yData - array of values in y-axis
	 */
	public GroupByData(String name, double[] xData, double[] yData) {
		this.name = name;
		this.xData = xData;
		this.yData = yData;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the xData
	 */
	public double[] getxData() {
		return xData;
	}
	/**
	 * @param xData the xData to set
	 */
	public void setxData(double[] xData) {
		this.xData = xData;
	}
	/**
	 * @return the yData
	 */
	public double[] getyData() {
		return yData;
	}
	/**
	 * @param yData the yData to set
	 */
	public void setyData(double[] yData) {
		this.yData = yData;
	}
}
