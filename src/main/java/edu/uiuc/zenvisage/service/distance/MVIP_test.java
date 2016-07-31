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
		
		double[] a = {1,2,3,4};
		double[] b = {7,89,3,1};
		Euclidean Dist = new Euclidean();
		double dist = Dist.calculateDistance(a, b);
		System.out.println(dist);
		
	}
}
