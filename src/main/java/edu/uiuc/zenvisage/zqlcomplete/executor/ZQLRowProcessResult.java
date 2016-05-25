/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tarique
 *
 */
public class ZQLRowProcessResult {
	private List<String> zValues;
	private String zType;


	public ZQLRowProcessResult() {
		zValues= new ArrayList<String>();
	}

	
	public List<String> getzValues() {
		return zValues;
	}

	public void setzValues(List<String> zValues) {
		this.zValues = zValues;
	}


	public String getzType() {
		return zType;
	}


	public void setzType(String zType) {
		this.zType = zType;
	}
}
