/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

/**
 * @category Normalization interface for data normalization
 */
public interface Normalization {
	
	/**
	 * Perform normalization in place of given array.
	 * @param normalizedValues
	 */
	public void normalize(double[] normalizedValues);
}
