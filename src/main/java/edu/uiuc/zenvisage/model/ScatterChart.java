package edu.uiuc.zenvisage.model;

import java.util.ArrayList;

public class ScatterChart {
	public String xAttribute;
	public String yAttribute;
	public String zval;
	public ArrayList<Point> points = new ArrayList<Point>();
	
	public ScatterChart() {
		
	}
	
	public String getxAttribute() {
		return xAttribute;
	}

	public void setxAttribute(String xAttribute) {
		this.xAttribute = xAttribute;
	}

	public String getyAttribute() {
		return yAttribute;
	}

	public void setyAttribute(String yAttribute) {
		this.yAttribute = yAttribute;
	}

	public String getZval() {
		return zval;
	}

	public void setZval(String zval) {
		this.zval = zval;
	}
}
