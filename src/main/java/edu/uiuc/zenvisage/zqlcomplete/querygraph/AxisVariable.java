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


// for x and y, attribute is empty and values can contain attribute names
// for z, attribute is actual attribute and values contains values of the specified attribute.
public class AxisVariable {

	private String attribute;
	private List<String> values=new ArrayList<String>();
	
	
	public String getAttribute() {
		return attribute;
	}


	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}


	public List<String> getValues() {
		return values;
	}


	public void setValues(List<String> values) {
		this.values = values;
	}


	public AxisVariable(String attribute, List<String> values) {
		this.attribute = attribute;
		// by reference ok?
		this.values = values;
	}
}
