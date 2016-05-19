/**
 * 
 */
package edu.uiuc.zenvisage.zql.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author tarique
 *
 */
public class ZQLTableResult {
	HashMap<String,ZQLRowResult> zqlRowResults;
	/**
	 * 
	 */
	public ZQLTableResult() {
		zqlRowResults=new HashMap<String,ZQLRowResult>();
	}
	
	public HashMap<String, ZQLRowResult> getZqlRowResults() {
		return zqlRowResults;
	}
	public void setZqlRowResults(HashMap<String, ZQLRowResult> zqlRowResults) {
		this.zqlRowResults = zqlRowResults;
	}
	
}
