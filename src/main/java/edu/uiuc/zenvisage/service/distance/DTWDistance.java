/**
 * 
 */
package edu.uiuc.zenvisage.service.distance;

import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;
/**
 * @author tarique
 *
 */
public class DTWDistance implements Distance {
	
	/* (non-Javadoc)
	 * @see distance.Distance#calculateDistance(double[], double[])
	 */
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		TimeSeries ts1 = new TimeSeries(1);
		TimeSeries ts2 = new TimeSeries(1);
		
		for (int i = 0; i < src.length; i++) {
			double [] point = new double[1];
			point[0] = src[i];
			TimeSeriesPoint tp = new TimeSeriesPoint(point);
			if (i == 0) {
				ts1.addLast(i, tp);
			}
			else {
				ts1.addLast(i, tp);
			}
		}
		
		for(int i = 0; i < tar.length; i++) {
			double[] point = new double[1];
			point[0] = tar[i];
			TimeSeriesPoint tp = new TimeSeriesPoint(point);
			if (i == 0) {
				ts2.addLast(i, tp);
			}
			else {
				ts2.addLast(i, tp);
			}
		}
		
		return DTW.getWarpInfoBetween(ts1, ts2).getDistance();
	}

}
