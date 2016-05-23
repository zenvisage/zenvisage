/**
 * 
 */
package org.vde.zql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tarique
 *
 */
public class Processe {
	private List<String> variables;
	private String method;
	private List<String> axis;
	private String count;
	private String metric;
	private List<String> arguments;
	
	private HashMap<String,String> parameters;
	/**
	 * 
	 */
	public Processe() {
		variables = new ArrayList<String>();
		method = "";
		axis = new ArrayList<String>();
		count = "";
		metric = "";
		arguments = new ArrayList<String>();
		parameters=new HashMap<>();
	}
	
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> source) {
		this.variables = source;
	}
	public List<String> getAxis() {
		return axis;
	}
	public void setAxis(List<String> source) {
		axis = source;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String source) {
		method = source;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String source) {
		count = source;
	}
	public String getMetric() {
		return metric;
	}
	public void setMetric(String source) {
		metric = source;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> source) {
		arguments = source;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}


}
