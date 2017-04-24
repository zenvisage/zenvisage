package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import edu.uiuc.zenvisage.model.ScatterResult.Tuple;

public class Polygon {
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
	public Polygon(double x1, double x2, double y1, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public Polygon() {
		
	}
	
	public boolean inArea(Tuple tuple) {
		return this.x1 <= tuple.x && tuple.x <= this.x2 && this.y1 <= tuple.y && tuple.y <= this.y2;
	}
}
