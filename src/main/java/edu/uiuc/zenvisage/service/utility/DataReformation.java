/**
 * 
 */
package edu.uiuc.zenvisage.service.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uiuc.zenvisage.model.Point;
import edu.uiuc.zenvisage.model.ZvQuery;

/**
 *
 */
public class DataReformation {
	public Normalization normalization;
	
	/**
	 * @param normlization
	 */
	public DataReformation(Normalization normalization) {
		this.normalization = normalization;
	}

	/**
	 * Reformat data into 2d array with choice of normalization.
	 * @param data
	 * @return normalizedgroups
	 */
	public double[][] reformatData(LinkedHashMap<String,LinkedHashMap<Float,Float>> data) {
		int maxLength = 0;
		for (String s: data.keySet()) {
			if (data.get(s).size() > maxLength) {
				maxLength = data.get(s).size();
			}
		}
		double[][] interpolatedData = new double[data.size()][];
		
		int i = 0;
		// O(2*V*P)
		for (String s: data.keySet()) { 
//			if (data.get(s).size() < 2) {
//				interpolatedData[i++] = new double[0];
//				continue;
//			}
			List<Float> overlappedXValues = new ArrayList<Float>(data.get(s).keySet());
			List<Float> overlappedYValues = new ArrayList<Float>(data.get(s).values());
			
			double[] temp = getInterpolatedData(overlappedXValues, overlappedYValues, maxLength); // O(2*P)
			//normalization.normalize(temp); // We normalize in getInterpolatedData already
			interpolatedData[i++] = temp;
		}
		
		return interpolatedData;
	}
	
	public double[] getInterpolatedData(float[] inputXValues, float[] inputYValues, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n+1];
		double[] interpolatedYValues = new double[n+1];
		
		float granularity = (inputXValues[inputXValues.length-1] - inputXValues[0]) / n;
		
		int count = 0;
		for (int i = 0; i <= n; i++) {
			float interpolatedX = inputXValues[0] + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(inputXValues[count] < interpolatedX) {
				if (count < inputXValues.length)
					count++;
			}
			if (inputXValues[count] == interpolatedX) {
				interpolatedYValues[i] = inputYValues[count];
			}
			else {
				float xDifference = inputXValues[count] - inputXValues[count-1];
				double yDifference = inputYValues[count] - inputYValues[count-1];
				interpolatedYValues[i] = inputYValues[count - 1] + (interpolatedX - inputXValues[count-1]) / xDifference * yDifference;				
			}
		}
		normalization.normalize(interpolatedYValues);
		return interpolatedYValues;
	}
	
	public double[] getInterpolatedData(List<Float> inputXValues, List<Float> inputYValues, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n+1];
		double[] interpolatedYValues = new double[n+1];

		interpolatedXValues[0] = inputXValues.get(0);
		interpolatedYValues[0] = inputYValues.get(0);
		interpolatedXValues[n] = inputXValues.get(inputXValues.size()-1);
		interpolatedYValues[n] = inputYValues.get(inputYValues.size()-1);
		
		float granularity = (inputXValues.get(inputXValues.size()-1) - inputXValues.get(0)) / n;
		
		int count = 0;
		for (int i = 1; i < n ; i++) {
			float interpolatedX = inputXValues.get(0) + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(inputXValues.get(count) < interpolatedX) {
				if (count < inputXValues.size())
					count++;
			}
			if (inputXValues.get(count) == interpolatedX) {
				interpolatedYValues[i] = inputYValues.get(count);
			}
			else {
				float xDifference = inputXValues.get(count) - inputXValues.get(count-1);
				double yDifference = inputYValues.get(count) - inputYValues.get(count-1);
				interpolatedYValues[i] = inputYValues.get(count - 1) + (interpolatedX - inputXValues.get(count-1)) / xDifference * yDifference;				
			}
		}
		normalization.normalize(interpolatedYValues);	
		return interpolatedYValues;
	}
	
	public double[] getInterpolatedData(float[] inputXValues, float[] inputYValues, float[] xRange, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n];
		double[] interpolatedYValues = new double[n];		
		float granularity = (xRange[1] - xRange[0]) / (n-1);
		
		int count = 0;
		for (int i = 0; i < n-1; i++) {
			float interpolatedX = xRange[0] + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(inputXValues[count] < interpolatedX) {
				if (count < inputXValues.length-1)
					count++;
			}
			if (inputXValues[count] == interpolatedX) {
				interpolatedYValues[i] = inputYValues[count];
			}
			else {
				float xDifference = inputXValues[count] - inputXValues[count-1];
				double yDifference = inputYValues[count] - inputYValues[count-1];
				interpolatedYValues[i] = inputYValues[count - 1] + (interpolatedX - inputXValues[count-1]) / xDifference * yDifference;				
			}
		}
		interpolatedXValues[n-1] = xRange[1];
		while(inputXValues[count] < interpolatedXValues[n-1]) {
			if (count < inputXValues.length-1)
				count++;
		}
		float xDifference = inputXValues[count] - inputXValues[count-1];
		double yDifference = inputYValues[count] - inputYValues[count-1];
		interpolatedYValues[n-1] = inputYValues[count - 1] + (interpolatedXValues[n-1] - inputXValues[count-1]) / xDifference * yDifference;	
		normalization.normalize(interpolatedYValues);	
		
		return interpolatedYValues;
	}
	
	public double[] getInterpolatedData(List<Float> xValues, List<Float> yValues, float[] xRange, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n];
		double[] interpolatedYValues = new double[n];		
		float granularity = (xRange[1] - xRange[0]) / (n-1);
		
		int count = 0;
		for (int i = 0; i < n-1; i++) {
			float interpolatedX = xRange[0] + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(xValues.get(count) < interpolatedX) {
				if (count < xValues.size()-1)
					count++;
			}
			if (xValues.get(count) == interpolatedX) {
				interpolatedYValues[i] = yValues.get(count);
			}
			else {
				float xDifference = xValues.get(count) - xValues.get(count-1);
				double yDifference = yValues.get(count) - yValues.get(count-1);
				interpolatedYValues[i] = yValues.get(count-1) + (interpolatedX - xValues.get(count-1)) / xDifference * yDifference;				
			}
		}
		interpolatedXValues[n-1] = xRange[1];
		while(xValues.get(count) < interpolatedXValues[n-1]) {
			if (count < xValues.size()-1)
				count++;
		}
		float xDifference = xValues.get(count) - xValues.get(count-1);
		double yDifference = yValues.get(count) - yValues.get(count-1);
		interpolatedYValues[n-1] = yValues.get(count-1) + (interpolatedXValues[n-1] - xValues.get(count-1)) / xDifference * yDifference;
		normalization.normalize(interpolatedYValues);		
		
		return interpolatedYValues;
	}

	// Triple Array. [overlappedData or overlappedQuery (0 or 1)][vizIndex][interpolatedYValues]
	// datInterpolated refers to the interpolated SQL query result
	// queryInterpolated refers to the interpolated "query" aka user drawn trend
	public double[][][] getOverlappedData(LinkedHashMap<String, LinkedHashMap<Float, Float>> output, ZvQuery args) {		
		double[][][] overlappedDataAndQueries = new double[2][output.size()][];
		float[] xRange = args.xRange;
		
		int i = 0;
		for (String s: output.keySet()) {
			if (xRange[0] >= xRange[1] || output.get(s).size() < 2) {
				overlappedDataAndQueries[0][i] = new double[0];
				overlappedDataAndQueries[1][i] = new double[0];
				i++;
				continue;
			}
			LinkedHashMap<Float,Float> originalData = output.get(s);
			List<Float> dataXValues = new ArrayList<Float>();
			List<Float> dataYValues = new ArrayList<Float>();
			for (float dataX : originalData.keySet()) {
				dataXValues.add( dataX);
				dataYValues.add( originalData.get(dataX));
			}
			
			float[] dataRange = {dataXValues.get(0), dataXValues.get(dataXValues.size() - 1)};
			float[] overlappedRange = {Math.max(dataRange[0], xRange[0]), Math.min(dataRange[1], xRange[1])};
			
//			System.out.println(overlappedXValues.size());
			if (overlappedRange[0] >= overlappedRange[1]) {
				overlappedDataAndQueries[0][i] = new double[0];
				overlappedDataAndQueries[1][i] = new double[0];
			}
			else {
				double[] overlappedDataInterpolated = getInterpolatedData(dataXValues, dataYValues, overlappedRange, 100);
				double[] overlappedQueryInterpolated = getInterpolatedData(args.dataX, args.dataY, overlappedRange, 100);
				overlappedDataAndQueries[0][i] = overlappedDataInterpolated;
				overlappedDataAndQueries[1][i] = overlappedQueryInterpolated;				
			}
			i++;
		}
			
		return overlappedDataAndQueries;
	}
	
}
