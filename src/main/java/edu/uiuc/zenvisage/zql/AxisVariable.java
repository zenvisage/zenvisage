/**
 * 
 */
package edu.uiuc.zenvisage.zql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarique
 *
 */


// for x and y, attribute is empty and values can contain attribute names
// for z, attribute is actual attribute and values contains values of the specified attribute.
public class AxisVariable {
	private String attributeType;  // either X, Y or Z
	private String attribute;
	private List<String> values = new ArrayList<String>();
	private double[] scores;
	
	public double[] getScores() {
		return scores;
	}

	public void setScores(double[] scores) {
		this.scores = scores;
	}

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
	
	public AxisVariable(String attributeType,String attribute, List<String> values) {
		this.attributeType=attributeType;
		this.attribute = attribute;
		// by reference ok?
		this.values = values;
	}

	public AxisVariable(String attributeType,String attribute, List<String> values, double[] scores) {
		this.attributeType=attributeType;
		this.attribute = attribute;
		this.values = values;
		this.scores = scores;
	}
	/**
	 * @return the attributeType
	 */
	public String getAttributeType() {
		return attributeType;
	}


	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}


}
