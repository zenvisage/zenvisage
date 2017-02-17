/**
 * 
 */
package edu.uiuc.zenvisage.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tarique
 *
 */
public class VisualClass {
	String X;
	String Y;
	ArrayList<Double> xPoints= new ArrayList<Double>();
	ArrayList<Double> yPoints= new ArrayList<Double>();
	HashMap<String,String> constraints = new HashMap<String, String>();
	
	public String constraintToString(){
		String output="";
		for(String k:constraints.keySet()){
			output=output+"AND "+constraints.get(k);
		}
		return output.substring(4);
	}

}
