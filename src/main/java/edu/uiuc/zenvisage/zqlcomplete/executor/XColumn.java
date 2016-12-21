package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

public class XColumn {
	
	private String variable;

	private List<String> attributes;
		
	
	
	/**
	 * @return the attributes
	 */
	public List<String> getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public XColumn(String variable) {
		//this.variable = variable;
		this.attributes = new ArrayList<String>();
		attributes.add(variable);
	}
	
	public XColumn(List<String> attributes) {
		//this.variable = variable;
		this.attributes = attributes;
	}
	
	public XColumn() {
		variable = "";
		attributes = new ArrayList<String>();
	}
	
	public String getVariable() {
		return variable;
	}
	
	public void setVariable(String source) {
		variable = source;
	}
	
}
