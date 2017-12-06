package edu.uiuc.zenvisage.model;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.zql.Polygon;

public class Sketch {
	private ArrayList<Point> points=new ArrayList<Point>();
	private String xAxis;
	private String yAxis;
	private String groupBy;
	private String aggrFunc;
	private String aggrVar;
	private int minX;
	private int maxX;
	private int minY;	
	private int maxY;
	private List<Polygon> polygons = new ArrayList<Polygon>();
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getAggrFunc() {
		return aggrFunc;
	}
	public void setAggrFunc(String aggrFunc) {
		this.aggrFunc = aggrFunc;
	}
	public String getAggrVar() {
		return aggrVar;
	}
	public void setAggrVar(String aggrVar) {
		this.aggrVar = aggrVar;
	}
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
	public int getMaxY() {
		return maxY;
	}
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}
	public List<Polygon> getPolygons() {
		return polygons;
	}
	public void setPolygons(List<Polygon> polygons) {
		this.polygons = polygons;
	} 
	
	

}
