package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

public class VizColumn {
	private String variable;
	private List<String> type;
	private List<String> parameters;
	
	public VizColumn() {
		variable = "";
		type = new ArrayList<String>();
		parameters = new ArrayList<String>();
	}
	
	public String getVariable() {
		return variable;
	}
	public void setVariable(String source) {
		variable = source;
	}
	public List<String> getType() {
		return type;
	}
	public void setType(List<String> source) {
		type = source;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<String> source) {
		parameters = source;
	}
}
