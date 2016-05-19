/**
 * 
 */
package edu.uiuc.zenvisage.service.distance;
/**
 * @author tarique
 *
 */
public interface Distance {
	/**
	 * Calculate distance from source to target, src and tar should
	 * have same length.
	 * @param src double array
	 * @param tar double array
	 * @return distance
	 */
	public double calculateDistance(double[] src, double[] tar);
}
