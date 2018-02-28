package edu.uiuc.zenvisage.zql;

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
		float minX = firstPoint.getXval();
		float maxX = firstPoint.getXval();
		float minY = firstPoint.getYval();
		float maxY = firstPoint.getYval();
		
		for (Point p : points) {
			float x = p.getXval();
			float y = p.getYval();
			
			if (x < minX) minX = x;
			else if (x > maxX) maxX = x;
			
			if (y < minY) minY = y;
			else if (y > maxY) maxY = y;
		}
		
		float x = (float) point.getXval();
		float y = (float) point.getYval();
		
		
		// check if point is outside bounding box
		if (x < minX || x > maxX || y < minY || y > maxY) {
			return false;
		}
		
		return true;
	}
}
