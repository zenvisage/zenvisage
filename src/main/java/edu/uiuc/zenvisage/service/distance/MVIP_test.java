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

import net.sf.javaml.distance.fastdtw.dtw.*;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;
import edu.uiuc.zenvisage.service.utility.Zscore;

public class MVIP_test {
	public static void main(String[] args) throws IOException{
		
		//MVIP test = new MVIP();
		
		//two time series
		//List<MVIP.possibleVIP> list = new ArrayList<MVIP.possibleVIP>();
		//double[] a = {1,2,3};
		//double[] b = {4,5,6};
		
		//Zscore zscore = new Zscore();
		//zscore.normalize(a);
		//double[] result = a;
				
		//print distance
		//double result = test.calculateDistance(a,b);
		//System.out.print(result);
		
		//double[][] aa = {{1,2},{3,4},{5,6}};
		//System.out.println(list.size());
		
		//for (int i = 0; i < result.length; ++i){
		//	System.out.println(result[i]);
		//}
		
		/*
		double[] a = {1,2,3,4};
		double[] b = {7,89,3,1};
		Euclidean Dist = new Euclidean();
		double dist = Dist.calculateDistance(a, b);
		System.out.println(dist);
		*/
		//double[] point = new double[3];
		/*
		TimeSeries ts1 = new TimeSeries(3);
		TimeSeries ts2 = new TimeSeries(3);
		double[][] point1 = {{1,2,3},{2,3,4},{5,6,7},{9,7,8}};
		double[][] point2 = {{2,1,3},{5,2,7},{6,1,8}};
		ts1.addLast(0, new TimeSeriesPoint(point1[0]));
		ts1.addLast(1, new TimeSeriesPoint(point1[1]));
		ts1.addLast(2, new TimeSeriesPoint(point1[2]));
		ts1.addLast(3, new TimeSeriesPoint(point1[3]));
				
		ts2.addLast(0, new TimeSeriesPoint(point2[0]));
		ts2.addLast(1, new TimeSeriesPoint(point2[1]));
		ts2.addLast(2, new TimeSeriesPoint(point2[2]));
				
		double dtwdist = DTW.getWarpInfoBetween(ts1, ts2).getDistance();	
		System.out.println(dtwdist);
		*/
		TimeSeries ts1 = new TimeSeries(1);
		TimeSeries ts2 = new TimeSeries(1);
		double[][] point1 = {{1},{1},{5},{2},{3},{3}};
		double[][] point2 = {{1},{2},{3}};
		ts1.addLast(0, new TimeSeriesPoint(point1[0]));
		ts1.addLast(1, new TimeSeriesPoint(point1[1]));
		ts1.addLast(2, new TimeSeriesPoint(point1[2]));
		ts1.addLast(3, new TimeSeriesPoint(point1[3]));
		ts1.addLast(4, new TimeSeriesPoint(point1[4]));
		ts1.addLast(5, new TimeSeriesPoint(point1[5]));
		
		ts2.addLast(0, new TimeSeriesPoint(point2[0]));
		ts2.addLast(1, new TimeSeriesPoint(point2[1]));
		ts2.addLast(2, new TimeSeriesPoint(point2[2]));
		
		double dtwdist = DTW.getWarpInfoBetween(ts1, ts2).getDistance();	
		System.out.println(dtwdist);
		
		//WarpPath path = DTW.getWarpInfoBetween(ts1, ts2).getPath();
		//path.getMatchingIndexesForI(i)
	}
}
