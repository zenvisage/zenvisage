/**
 * 
 */
package edu.uiuc.zenvisage.data.roaringdb.executor;

import java.util.LinkedHashMap;

import com.google.common.collect.BiMap;

/**
 * @author xiaofo
 *
 */
public class ExecutorResult {
	public LinkedHashMap<String, LinkedHashMap<Float, Float>> output;
	public BiMap<String, Float> xMap;
	
	/**
	 * @param output
	 * @param xMap
	 */
	public ExecutorResult(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, BiMap<String, Float> xMap) {
		this.output = output;
		this.xMap = xMap;
	}

}
