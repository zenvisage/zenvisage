package edu.uiuc.zenvisage.service.distance;

import java.io.*;
import edu.uiuc.zenvisage.service.utility.Zscore;

public class MVIP_test {
	public static void main(String[] args) throws IOException{
		
		MVIP test = new MVIP();
		
		//two time series
		double[] a = {1,2,3};
		//double[] b = {4,5,6};
		
		Zscore zscore = new Zscore();
		zscore.normalize(a);
		double[] result = a;
				
		//print distance
		//double result = test.calculateDistance(a,b);
		//System.out.print(result);
		
		double[][] aa = {{1,2},{3,4},{5,6}};
		System.out.println(aa.length);
		
		for (int i = 0; i < result.length; ++i){
			System.out.println(result[i]);
		}
	}
}
