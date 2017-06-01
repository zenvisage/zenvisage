package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

public class ZColumn {
	
	private String variable;
	private String attribute; //put z here
	private List<String> values;
	private String expression;
	private boolean aggregate;
	// after executing expression, implement after set operation is implemented
	// private List<String> parsedValues;
	
	public ZColumn(String attribute) {
		this.attribute = attribute;
	}
	
	public ZColumn() {
		variable = "";
		attribute = "";
		values = new ArrayList<String>();
		expression = "";
		aggregate = false;
		// parsedValues = new ArrayList<String>();
	}
	
	public String getVariable() {
		return variable;
	}
	
	public void setVariable(String source) {
		variable = source;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String source) {
		expression = source;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public void setAttribute(String source) {
		attribute = source;
	}
	
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> source) {
		values = source;
	}

	public boolean isAggregate() {
		return aggregate;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}
	
	
}
