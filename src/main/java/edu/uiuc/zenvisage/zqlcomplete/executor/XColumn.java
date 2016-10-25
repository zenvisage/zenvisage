package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

public class XColumn {
	
	private String variable;
	private String attribute;
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	private List<String> values;
	
	public XColumn(String variable) {
		this.variable = variable;
	}
	public XColumn() {
		variable = "";
		values = new ArrayList<String>();
	}
	
	public String getVariable() {
		return variable;
	}
	public void setVariable(String source) {
		variable = source;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> source) {
		values = source;
	}
}
