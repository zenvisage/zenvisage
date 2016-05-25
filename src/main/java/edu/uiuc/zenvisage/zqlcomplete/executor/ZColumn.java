package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

public class ZColumn {
	
	private String variable;
	private String column;
	private List<String> values;
	private String expression;
	
	// after executing expression, implement after set operation is implemented
	// private List<String> parsedValues;
	
	public ZColumn() {
		variable = "";
		column = "";
		values = new ArrayList<String>();
		expression = "";
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
	
	public String getColumn() {
		return column;
	}
	public void setColumn(String source) {
		column = source;
	}
	
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> source) {
		values = source;
	}
	
}
