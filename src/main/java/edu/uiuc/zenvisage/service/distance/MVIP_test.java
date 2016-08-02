package edu.uiuc.zenvisage.service.distance;

import java.io.*;
import java.util.*;
import java.lang.*;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import net.sf.javaml.distance.fastdtw.dtw.*;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;
import edu.uiuc.zenvisage.service.distance.MVIP.VIPinfo;
import edu.uiuc.zenvisage.service.utility.Zscore;

public class MVIP_test {
	
	public static double[][] read2DArray(String filePath) throws FileNotFoundException {
		ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
		Scanner input = new Scanner(new File(filePath));
		while(input.hasNextLine())
		{
		    Scanner colReader = new Scanner(input.nextLine());
		    ArrayList<Double> col = new ArrayList<Double>();
		    while(colReader.hasNextDouble())
		    {
		        col.add(colReader.nextDouble());
		    }
		    list.add(col);
		    colReader.close();
		}
		input.close();
		
		final int listSize = list.size();
		double[][] darr = new double[listSize][];
		for(int i = 0; i < listSize; i++) {
		    ArrayList<Double> sublist = list.get(i);
		    final int sublistSize = sublist.size();
		    darr[i] = new double[sublistSize];
		    for(int j = 0; j < sublistSize; j++) {
		        darr[i][j] = sublist.get(j).doubleValue();
		    }
		}
		
		return darr;
	}
	
	public static class tsSearch implements Comparable<tsSearch> {
		public int whichClass;
		public Double dist;
		public int index;
		
		public tsSearch(int WHICHCLASS, double DIST, int INDEX) {
			whichClass = WHICHCLASS;
			dist = DIST;
			index = INDEX;
		}
		
		public void setValue(int WHICHCLASS, double DIST, int INDEX) {
			whichClass = WHICHCLASS;
			dist = DIST;
			index = INDEX;
		}
		
		public int compareTo(tsSearch arg0) {
	        return this.dist.compareTo(arg0.dist);
	    }
	}
	
	public static double simSearch(int query, double[][]dataset, int topN, Distance distance) {
		int queryClass = query / 100 + 1;
		tsSearch[] simRank = new tsSearch[dataset.length];
		int correct = 0;
		
		for (int i = 0; i < dataset.length; ++i) {
			simRank[i] = new tsSearch(i / 100 + 1, distance.calculateDistance(dataset[query], dataset[i]), i);
		}
		
		Arrays.sort(simRank);
		
		for (int i = 0; i < topN; ++i) {
			if (simRank[i].whichClass == queryClass)
				correct++;
		}
		
		return (double)(correct) / topN;
	}
	
	public static void main(String[] args) throws IOException{
		
		final String filePath = "/Users/Steven/Academic/SR@Aditya/Zenvisage/datasets/synthetic_control.data.txt";
		double[][] dataset = read2DArray(filePath);
		//MVIP distance = new MVIP();
		//SegmentationDistance distance = new SegmentationDistance();
		DTWDistance distance = new DTWDistance();
		
		Zscore zscore = new Zscore();
		for (int i = 0; i < dataset.length; ++i) {
			zscore.normalize(dataset[i]);
		}
		
		/*
		final int query = 202 - 1;
		final int topN = 100;
		double accuracy;
		accuracy = simSearch(query, dataset, topN);
		System.out.println(accuracy);
		*/
		/*
		final int topN = 100;
		double[] accuGroup = {0,0,0,0,0,0};
		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 100; ++j) {
				//System.out.println(i*100+j);
				accuGroup[i] += simSearch(i*100+j, dataset, topN, distance);
			}
			accuGroup[i] /= topN;
		}
		for (int i = 0; i < 6; ++i) {
			System.out.println(accuGroup[i]);
		}
		*/
		double degrees = 45.0;
	     double radians = Math.toRadians(degrees);

	     System.out.format("The value of pi is %.4f%n", Math.PI);
	     System.out.format("The arctangent of %.4f is %.4f degrees %n", Math.cos(radians), Math.toDegrees(Math.atan(1)));
	}
}
