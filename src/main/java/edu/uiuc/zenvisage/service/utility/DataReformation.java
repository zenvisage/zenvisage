/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class DataReformation {
	public Normalization normalization;
	
	/**
	 * @param normlization
	 */
	public DataReformation(Normalization normalization) {
		this.normalization = normalization;
	}

	/**
	 * Reformat data into 2d array with choice of normalization.
	 * @param data
	 * @return normalizedgroups
	 */
	public double[][] reformatData(LinkedHashMap<String,LinkedHashMap<Float,Float>> data) {
		double[][] normalizedgroups = new double[data.size()][];
		int count = 0;
		for(String key: data.keySet()){
			Map<Float,Float> values = data.get(key);  
			Collection<Float> vs =  values.values();
			double[] normalizedValues = new double[values.size()];
			Iterator<Float> it = vs.iterator();
			int i = 0;
			while(it.hasNext()){
				normalizedValues[i++] = it.next();
			}
			normalization.normalize(normalizedValues);
			normalizedgroups[count] = normalizedValues;
			count++;		  
		}
		return normalizedgroups;
	}
}
