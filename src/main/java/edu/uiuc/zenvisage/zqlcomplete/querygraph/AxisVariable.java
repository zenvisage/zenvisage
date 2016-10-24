/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarique
 *
 */
public class AxisVariable {
	private String type;
	private List<String> values=new ArrayList<String>();
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public List<String> getValues() {
		return values;
	}


	public void setValues(List<String> values) {
		this.values = values;
	}


	public AxisVariable(String type, List<String> values) {
		this.type = type;
		// by reference ok?
		this.values = values;
	}
}
