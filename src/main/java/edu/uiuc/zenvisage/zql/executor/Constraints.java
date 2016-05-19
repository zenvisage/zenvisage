/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

/**
 * @author tarique
 *
 */
public class Constraints {
	private String key;
	private String operator;
	private String value;
	
	/**
	 * 
	 */
	public Constraints() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}


}
