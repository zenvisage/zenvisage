package edu.uiuc.zenvisage.model;

import java.util.List;

public class ScatterResult {
	public static class Tuple {
		public double x;
		public double y;
		public Tuple(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	public List<Tuple> points;
	public int count = 0;
	public String name;
	/**
	 * @param points
	 * @param count
	 * @param name
	 */
	public ScatterResult(List<Tuple> points, int count, String name) {
		this.points = points;
		this.count = count;
		this.name = name;
	}
}
