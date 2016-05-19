/**
 * 
 */
package edu.uiuc.zenvisage.service.distance;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 *
 */
public class Euclidean implements Distance {
	private EuclideanDistance ed;

	public Euclidean() {
		// TODO Auto-generated constructor stub
		ed = new EuclideanDistance();
	}

	/*
	 * (non-Javadoc)
	 * @see distance.Distance#calculateDistance(double[], double[])
	 */
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		return ed.compute(src, tar);
	}

}
