/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.HashMap;

/**
 * @author tarique
 *
 */
public class ZQLTableResult {
	HashMap<String,ZQLRowResult> zqlRowResults;
	
	/**
	 * global variables in the table that are already resolved
	 */
	private HashMap<String, ZQLVariable> variables;
	
	
	public ZQLTableResult() {
		zqlRowResults=new HashMap<String,ZQLRowResult>();
		variables = new HashMap<String,ZQLVariable>();
	}
	
	
	/**
	 * Use these to get the values specified in other rows by variable name
	 * @return
	 */
	public ZQLVariable getVariable(String variableName) {	
		return variables.get(variableName);
	}
	
	public void setVariable(String variabelName, ZQLVariable values) {
		variables.put(variabelName, values);
	}
	

	public HashMap<String, ZQLRowResult> getZqlRowResults() {
		return zqlRowResults;
	}
	public void setZqlRowResults(HashMap<String, ZQLRowResult> zqlRowResults) {
		this.zqlRowResults = zqlRowResults;
	}
	
}
