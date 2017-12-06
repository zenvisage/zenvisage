/**
 * 
 */
package edu.uiuc.zenvisage.zql;

import java.util.HashMap;

/**
 * @author tarique
 *
 */
public class LookUpTable {
	private HashMap<String, Object> variables= new HashMap<String,Object>();
	public LookUpTable(){	}
	public void put(String name, Object obj){
		variables.put(name,obj);
	}
	public Object get(String name){
		return variables.get(name);
	}
	public HashMap<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(HashMap<String, Object> variables) {
		this.variables = variables;
	}

}
