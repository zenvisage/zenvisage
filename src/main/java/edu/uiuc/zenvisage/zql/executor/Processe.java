/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tarique
 *
 */
public class Processe {
	private String name;
	private String function;
	private HashMap<String,String> parameters;
	/**
	 * 
	 */
	public Processe() {
		parameters=new HashMap<>();
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public HashMap<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
