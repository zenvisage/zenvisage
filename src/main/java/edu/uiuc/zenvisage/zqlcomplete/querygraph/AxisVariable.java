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
	
	
	public AxisVariable(String type, List<String> values) {
		this.type = type;
		// by reference ok?
		this.values = values;
	}
}
