package edu.uiuc.zenvisage.model;

import java.util.ArrayList;

public class Sketch {
	public ArrayList<Point> points=new ArrayList<Point>();
	public String xAxis;
	public String yAxis;
	public String groupBy;
	public String aggrFunc;
	public String aggrVar;
	public int minX;
	public int maxX;
	public int minY;	
	public int maxY;

	
	public int getMinX() {
		return minX;
	}
	public void setMinX(int minX) {
		this.minX = minX;
	}
	public int getMaxX() {
		return maxX;
	}
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	public int getMinY() {
		return minY;
	}
	public void setMinY(int minY) {
		this.minY = minY;
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	public int getMin() {
		return minX;
	}
	public void setMin(int min) {
		this.minX = min;
	}
	public int getMaxY() {
		return maxY;
	}
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}
	

}
