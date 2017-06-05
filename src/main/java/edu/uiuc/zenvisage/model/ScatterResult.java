package edu.uiuc.zenvisage.model;

import java.util.List;

@Deprecated
public class ScatterResult {

	public List<Point> points;
	public int count = 0;
	public String name;
	/**
	 * @param points
	 * @param count
	 * @param name
	 */
	public ScatterResult(List<Point> points, int count, String name) {
		this.points = points;
		this.count = count;
		this.name = name;
	}
}
