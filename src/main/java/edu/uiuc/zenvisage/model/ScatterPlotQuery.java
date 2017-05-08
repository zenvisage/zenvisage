/**
 * 
 */
package edu.uiuc.zenvisage.model;

import java.util.List;


/**
 * @author xiaofo
 *
 */
public class ScatterPlotQuery {
	
	public static class Rectangle {
		public double x1;
	    public double x2;
	    public double y1;
	    public double y2;
		/**
		 * @param x1
		 * @param x2
		 * @param y1
		 * @param y2
		 */
		public Rectangle(double x1, double x2, double y1, double y2) {
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
		
		public Rectangle() {
			
		}
		// obsolete
//		public boolean inArea(Tuple tuple) {
//			return this.x1 <= tuple.x && tuple.x <= this.x2 && this.y1 <= tuple.y && tuple.y <= this.y2;
//		}
		public boolean inArea(Point point) {
			return true;
		}
	}
	
	public String method; // now resides in ScatterProcess
	public List<Rectangle> rectangles; // now resides in VisualComponentQuery
    public String xAxis; // VisualComponentQuery vc.getX().getAttributes().get(0);
	public String yAxis;
	public String zAxis;
	public int numOfResults;

	
	/**
	 * @param method
	 * @param rectangles
	 * @param xAxis
	 * @param yAxis
	 * @param zAxis
	 * @param numOfResults
	 */
	public ScatterPlotQuery(String method, List<Rectangle> rectangles, String xAxis, String yAxis, String zAxis,
			int numOfResults) {
		this.method = method;
		this.rectangles = rectangles;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.zAxis = zAxis;
		this.numOfResults = numOfResults;
	}
	public ScatterPlotQuery(){}
	/**
	 * @return the xAxis
	 */
	public String getxAxis() {
		return xAxis;
	}

	/**
	 * @param xAxis the xAxis to set
	 */
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}

	/**
	 * @return the yAxis
	 */
	public String getyAxis() {
		return yAxis;
	}

	/**
	 * @param yAxis the yAxis to set
	 */
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

	/**
	 * @return the zAxis
	 */
	public String getzAxis() {
		return zAxis;
	}

	/**
	 * @param zAxis the zAxis to set
	 */
	public void setzAxis(String zAxis) {
		this.zAxis = zAxis;
	}

	/**
	 * @return the numOfResults
	 */
	public int getNumOfResults() {
		return numOfResults;
	}

	/**
	 * @param numOfResults the numOfResults to set
	 */
	public void setNumOfResults(int numOfResults) {
		this.numOfResults = numOfResults;
	}

}
