/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author tarique
 *
 */
public class Processe {
	private List<String> variables;
	private String method;
	private List<String> axisList1;
	private List<String> axisList2;
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
		axisList1 = new ArrayList<String>();
		axisList2 = new ArrayList<String>();
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
	
	public List<String> getAxisList1() {
		return axisList1;
	}

	public void setAxisList1(List<String> axisList1) {
		this.axisList1 = axisList1;
	}

	public List<String> getAxisList2() {
		return axisList2;
	}

	public void setAxisList2(List<String> axisList2) {
		this.axisList2 = axisList2;
	}


}
