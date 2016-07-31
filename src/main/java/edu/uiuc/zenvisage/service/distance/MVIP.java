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
		public Integer VIPindex; //the index in time series
		public double VIPDist;//Normalized Dist of each VIP
		public int VIPimportance;//the order of being added to VIP set
		
		public VIPinfo() {}
		
		public VIPinfo(int VIPINDEX, double VIPDIST, int PIPIMPORTANCE) {
			VIPindex = VIPINDEX;
			VIPDist = VIPDIST;
			VIPimportance = PIPIMPORTANCE;
		}
		
		public VIPinfo setValue(int VIPINDEX, double VIPDIST, int PIPIMPORTANCE) {
			VIPindex = VIPINDEX;
			VIPDist = VIPDIST;
			VIPimportance = PIPIMPORTANCE;
			return this;
		}
		
		public int compareTo(VIPinfo arg0) {
	        return this.VIPindex.compareTo(arg0.VIPindex);
	    }
	}
	
	public static class Indicator { //PIP Indicator
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
	
	//preprocessing - Redundant preprocessings could happen during similarity search and especially clustering
	public static double[][] preprocessing(double[] array) {
		double[] smooth;
		double[][] result;
		
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
		double Xrange = array.length-1;
		double Yrange = StatUtils.max(smooth) - StatUtils.min(smooth);
		result = new double[smooth.length][2];
		for (int i = 0; i < smooth.length; ++i){
			if (Xrange != 0)
				result[i][0] = i / Xrange;
			if (Yrange != 0)
				result[i][1] = smooth[i] / Yrange;
		}
		
		return result;
	}
	
	public static double[] NormVDist()
	
	//get VIPs' info
	public static List<VIPinfo> getVIPs(double[][]ts) {
		final double threshold = 0.05;
		int tslength = ts.length;
		List<VIPinfo> VIPlist = new ArrayList<VIPinfo>();//VIPlist=PIPinfo - delete
		VIPinfo newVIP = new VIPinfo();
		double[] Dist;
		
		//add start point and tail point into VIP set
		newVIP.setValue(0, 0, 0);
		VIPlist.add(newVIP);
		newVIP.setValue(tslength-1, 0, 1);
		VIPlist.add(newVIP);
		
		//Dist=NormVDist(ts,yrange);
		
		
		
	}
	
	@Override
	public double calculateDistance(double[] src, double[] tar) {
		// TODO Auto-generated method stub
		assert src.length == tar.length;
		
		return 22;
	}

}
