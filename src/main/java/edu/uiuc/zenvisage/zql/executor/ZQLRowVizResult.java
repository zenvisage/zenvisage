/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author tarique
 *
 */
public class ZQLRowVizResult {
	 private String x;
	 private String y;
	 private String z;
	 private LinkedHashMap<String, LinkedHashMap<Float, Float>> vizData;
		
			 
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	
	public LinkedHashMap<String, LinkedHashMap<Float, Float>> getVizData() {
		return vizData;
	}
	public void setVizData(LinkedHashMap<String, LinkedHashMap<Float, Float>> vizData) {
		this.vizData = vizData;
	}
	

}
