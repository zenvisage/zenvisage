package edu.uiuc.zenvisage.model;

public class Point {
	float xval;
	float yval;
	
	public Point() {
		// for jackson
	}
	
	public Point(float x, float y) {
		this.xval = x;
		this.yval = y;
	}

	public float getXval() {
		return xval;
	}

	public void setXval(float xval) {
		this.xval = xval;
	}

	public float getYval() {
		return yval;
	}

	public void setYval(float yval) {
		this.yval = yval;
	}
	
	
	

}
