package edu.uiuc.zenvisage.service.distance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;


/*
 * @author Changfeng
 */

public class MVIP implements Distance {
	
	public static class VIPinfo implements Comparable<VIPinfo> {
		public Integer index; //the index in time series
		public double dist;//Normalized Dist of each VIP
		public int importance;//the order of being added to VIP set
		
		public VIPinfo setValue(int VIPINDEX, double VIPDIST, int PIPIMPORTANCE) {
			index = VIPINDEX;
			dist = VIPDIST;
			importance = PIPIMPORTANCE;
			return this;
		}
		
		public int compareTo(VIPinfo arg0) {
	        return this.index.compareTo(arg0.index);
	    }
	}
	
	public static class possibleVIP implements Comparable<possibleVIP> {
		public int index;
		public Double dist;
		public int leftVIPindex;
		public int rightVIPindex;
		
		public void setValue(int INDEX, double DIST, int LEFTVIPINDEX, int RIGHTVIPINDEX) {
			index = INDEX;
			dist = DIST;
			leftVIPindex = LEFTVIPINDEX;
			rightVIPindex = RIGHTVIPINDEX;
		}
		
		public int compareTo(possibleVIP arg0) {
	        return this.dist.compareTo(arg0.dist);
	    }
	}
	
	/*
	public static class Indicator { //VIP Indicator
		//position
		public double X;
		public double Y;
		
		//nearby shape
		public double diffY_L2;
		public double diffY_L1;
		public double diffY_R1;
		public double diffY_R2;
		
		//nearby pattern
		public double normDiffVIP_L;
		public double normDiffVIP_R;
	}
	*/
	
	//preprocessing - Redundant preprocessings could happen during similarity search and especially clustering
	public static double[] preprocessing(double[] array) {
		double[] smooth;
		double[] result;
		
		//Zcore - already done by service.utility.Zscore.java
		
		//smoothing [1/4, 1/2, 1/4]
		if (array.length > 5) {
			smooth = new double[array.length-2];
			for (int i = 0; i < smooth.length; ++i){
				smooth[i] = array[i] / 4 + array[i+1] / 2 + array[i+2] / 4;
			}
		}
		else {
			smooth = array;
		}
		
		//axis normalization
		double Yrange = StatUtils.max(smooth) - StatUtils.min(smooth);
		result = new double[smooth.length];
		for (int i = 0; i < smooth.length; ++i){
			if (Yrange != 0)
				result[i] = smooth[i] / Yrange;
		}
		
		return result;
	}
	
	public static double[] NormVDist(double[] array) {
		final double step = (array[array.length-1] - array[0]) / (array.length - 1);
		double current = array[0];
		double[] Dist = new double[array.length];
		
		for (int i = 0; i < array.length; ++i) {
			Dist[i] = Math.abs((array[i] - current));
			current += step;
		}
		return Dist;
	}
	
	//get VIPs' info
	public static List<VIPinfo> getVIPs(double[] ts) {
		
		final double threshold = 0.05; //5% of Y-axis range
		List<VIPinfo> VIPlist = new ArrayList<VIPinfo>();//VIPlist=PIPinfo - delete
		VIPinfo newVIP = new VIPinfo();
		double[] Dist;
		int possVIPindex;
		double possVIPdist;
		List<possibleVIP> waitinglist = new ArrayList<possibleVIP>();
		possibleVIP possVIP = new possibleVIP();
		int dewlIndex; //the index of dewaitinglist in waitinglist
		double tmpdist;
		
		//add start point and tail point into VIP set
		newVIP.setValue(0, 0, 0);
		VIPlist.add(newVIP);
		if (ts.length > 1) {
			newVIP.setValue(ts.length-1, 0, 1);
			VIPlist.add(newVIP);
		}
		
		if (ts.length > 2) {
			Dist = NormVDist(ts);
			possVIPindex = 1;
			possVIPdist = Dist[1];
			for (int i = 2; i < (ts.length-1); ++i) {
				if (Dist[i] > possVIPdist) {
					possVIPdist = Dist[i];
					possVIPindex = i;
				}
			}
			if (possVIPdist > threshold) {
				possVIP.setValue(possVIPindex, possVIPdist, 0, ts.length-1);
				waitinglist.add(possVIP);
				dewlIndex = 0;
			}
			else
				dewlIndex = -1;
			
			while(dewlIndex >= 0) {
				possVIP = waitinglist.get(dewlIndex);
				newVIP.setValue(possVIP.index, possVIP.dist, VIPlist.size());
				VIPlist.add(newVIP);
				waitinglist.remove(dewlIndex);
				
				int startIndex = possVIP.leftVIPindex;
				int endIndex = possVIP.rightVIPindex;
				int middleIndex = possVIP.index;
				
				if (middleIndex > (startIndex+1)) {
					Dist = NormVDist(Arrays.copyOfRange(ts,startIndex,middleIndex+1));
					possVIPindex = startIndex+1;
					possVIPdist = Dist[1];
					for (int i = startIndex+2; i < middleIndex; ++i) {
						if (Dist[i-startIndex] > possVIPdist) {
							possVIPdist = Dist[i-startIndex];
							possVIPindex = i;
						}
					}
					if (possVIPdist > threshold) {
						possVIP.setValue(possVIPindex, possVIPdist, startIndex, middleIndex);
						waitinglist.add(possVIP);
					}
				}
				
				if (endIndex > (middleIndex+1)) {
					Dist = NormVDist(Arrays.copyOfRange(ts,middleIndex,endIndex+1));
					possVIPindex = middleIndex+1;
					possVIPdist = Dist[1];
					for (int i = middleIndex+2; i < endIndex; ++i) {
						if (Dist[i-middleIndex] > possVIPdist) {
							possVIPdist = Dist[i-middleIndex];
							possVIPindex = i;
						}
					}
					if (possVIPdist > threshold) {
						possVIP.setValue(possVIPindex, possVIPdist, middleIndex, endIndex);
						waitinglist.add(possVIP);
					}
				}
				
				if (waitinglist.size() == 0)
					dewlIndex = -1;
				else {
					dewlIndex = 0;
					tmpdist = waitinglist.get(dewlIndex).dist;
					for (int i = 1; i < waitinglist.size(); ++i) {
						if (waitinglist.get(i).dist > tmpdist) {
							tmpdist = waitinglist.get(i).dist;
							dewlIndex = i;
						}
					}
				}	
			}
		}
		VIPlist.sort(null);;
		return VIPlist;
	}
	
	//only 2 dimensions (x, y) for now 
	public static double[][] getIndicators(double[] ts, List<VIPinfo> VIPlist) {
		final int dimension = 2;
		double[][] indicatorArray = new double[VIPlist.size()][dimension];
		double Xrange = ts.length - 1;
		
		for (int i = 0; i < VIPlist.size(); ++i) {
			//X
			if (Xrange > 0)
				indicatorArray[i][0] = VIPlist.get(i).index / Xrange;
			
			//Y
			indicatorArray[i][1] = ts(VIPlist.get(i).index);
		}
	}
	
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		
		List<VIPinfo> srcVIPlist;
		List<VIPinfo> tarVIPlist;
		
		src = preprocessing(src);
		tar = preprocessing(tar);
		
		srcVIPlist = getVIPs(src);
		tarVIPlist = getVIPs(tar);
		
		
	}

}
