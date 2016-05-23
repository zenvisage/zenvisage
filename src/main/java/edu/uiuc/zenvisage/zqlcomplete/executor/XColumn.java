package org.vde.zql;

import java.util.ArrayList;
import java.util.List;

public class XColumn {
	
	private String variable;
	private List<String> values;
	
	public XColumn() {
		variable = "";
		values = new ArrayList<String>();
	}
	
	public String getVariable() {
		return variable;
	}
	public void setVariable(String source) {
		variable = source;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> source) {
		values = source;
	}
}
