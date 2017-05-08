package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.List;

import edu.uiuc.zenvisage.model.Point;

public class Polygon {
	
	private List<Point> points;

	/**
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public Polygon(List<Point> points) {
		this.points = points;
	}
	
	public Polygon() {
		
	}
	
	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	/**
	 * Checks if a tuple is within the polygon
	 * @param tuple
	 * @return
	 */
	public boolean inArea(Point point) {
		if (points.isEmpty()) {
			return false;
		}
		Point firstPoint = points.get(0);
		float minX = firstPoint.getX();
		float maxX = firstPoint.getX();
		float minY = firstPoint.getY();
		float maxY = firstPoint.getY();
		
		for (Point p : points) {
			float x = p.getX();
			float y = p.getY();
			
			if (x < minX) minX = x;
			else if (x > maxX) maxX = x;
			
			if (y < minY) minY = y;
			else if (y > maxY) maxY = y;
		}
		
		float x = (float) point.getX();
		float y = (float) point.getY();
		
		
		// check if point is outside bounding box
		if (x < minX || x > maxX || y < minY || y > maxY) {
			return false;
		}
		
		return true;
	}
}
