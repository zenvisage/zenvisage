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
	
	public void main(String[] args) throws IOException{
		
		final String filePath = "/Users/Steven/Academic/SR@Aditya/Zenvisage/datasets/synthetic_control.data.txt";
		double[][] dataset = read2DArray(filePath);
		int index1 = 1 - 1;
		int index2 = 101 - 1;
		double dist;
		MVIP distance = new MVIP();
		Zscore zscore = new Zscore();
		
		for (int i = 0; i < dataset.length; ++i) {
			zscore.normalize(dataset[i]);
		}
		
		dist = distance.calculateDistance(dataset[index1], dataset[index2]);
		System.out.println(dist);
	}
}
