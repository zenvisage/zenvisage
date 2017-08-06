/**
 * 
 */
package edu.uiuc.zenvisage.service.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
/**
 * @author tarique
 *
 */
public class Euclidean implements Distance, DistanceMeasure {
	private EuclideanDistance ed;

	public Euclidean() {
		// TODO Auto-generated constructor stub
		ed = new EuclideanDistance();
	}

	/*
	 * (non-Javadoc)
	 * @see distance.Distance#calculateDistance(double[], double[])
	 * Bug fix
	There could be arrayIndexOutOfBound error wehn src length is bigger than tar because
    public static double distance(double[] p1, double[] p2) {
		double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
		}*/
	@Override
	public double calculateDistance(double[] src, double[] tar) {		
		// TODO Auto-generated method stub
		if(src.length>tar.length){
			return ed.compute(tar, src);
		} else {
			return ed.compute(src, tar);
		}
	}
	
	@Override
	public double compute(double[] a, double[] b) {
		// TODO Auto-generated method stub
		return calculateDistance(a,b);
	}

}
