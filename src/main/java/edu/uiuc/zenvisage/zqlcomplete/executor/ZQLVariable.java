package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * variables in the ZQL table in columns X, Y, Z or Process
 * 
 * @author kaluo
 *
 */
public class ZQLVariable {
	
	private String name; // use as column name for now
	private String expression;
	private List<String> values;
	
	public ZQLVariable() {
		name = "";
		expression = "";
		values = new ArrayList<String>();
	}
	
	public ZQLVariable(String variableName, String variableExpression) {
		name = variableName;
		expression = variableExpression;
		values = new ArrayList<String>();
	}
	
	public ZQLVariable(String variableName, String variableExpression, List<String> variableValues) {
		name = variableName;
		expression = variableExpression;
		values = variableValues;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> source) {
		values = source;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String source) {
		name = source;
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String source) {
		expression = source;
	}
	
}
