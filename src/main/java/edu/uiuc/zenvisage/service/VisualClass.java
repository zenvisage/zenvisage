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
	ArrayList<Float> xPoints= new ArrayList<Float>();
	ArrayList<Float> yPoints= new ArrayList<Float>();
	HashMap<String,String> constraints = new HashMap<String, String>();
	
	public String constraintToString(){
		String output="";
		for(String k:constraints.keySet()){
			output=output+"AND "+constraints.get(k);
		}
		return output.substring(4);
	}

}
