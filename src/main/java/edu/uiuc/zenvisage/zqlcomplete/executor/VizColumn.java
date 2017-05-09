package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.HashMap;
import java.util.Map;



public class VizColumn {
	
	public static final String aggregation = "aggregation";
	public static final String type = "type";
	public static final String scatter = "scatter";
	public static final String lineChart = "linechart";
	//private String variable;
	//private List<String> type;
	//private List<String> parameters;
	// This map can contain as may variables as needed:
	// eg type, aggregation, so on. We choose this format since VizColumn is very fluid between different types of queries
	private Map<String, Object> map;
	
	public VizColumn(Map<String, Object> map) {
		this.map = map;
	}
	
	public VizColumn() {
		map = new HashMap<String, Object>();
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
}
