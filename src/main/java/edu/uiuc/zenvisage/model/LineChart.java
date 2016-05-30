package edu.uiuc.zenvisage.model;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class LineChart {
	private String x_type;
	private String y_type;
	ArrayList<ArrayList<Float>> data = new ArrayList<ArrayList<Float>>();
	
	public LineChart(){
		
	}
	
	public String getX_type() {
		return x_type;
	}

	public void setX_type(String x_type) {
		this.x_type = x_type;
	}

	public String getY_type() {
		return y_type;
	}

	public void setY_type(String y_type) {
		this.y_type = y_type;
	}

	public ArrayList<ArrayList<Float>> getData() {
		return data;
	}

	public void setData(ArrayList<ArrayList<Float>> data) {
		this.data = data;
	}

}
	
	

