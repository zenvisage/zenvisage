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
		double[][] interpolatedData = new double[data.size()][maxLength];
		
		int i = 0;
		for (String s: data.keySet()) {
			List<Float> overlappedXValues = new ArrayList<Float>(data.get(s).keySet());
			List<Float> overlappedYValues = new ArrayList<Float>(data.get(s).values());
			
			double[] temp = getInterpolatedData(overlappedXValues, overlappedYValues, maxLength);
			normalization.normalize(temp);
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
		return interpolatedYValues;
	}
	
	public double[] getInterpolatedData(List<Float> inputXValues, List<Float> inputYValues, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n+1];
		double[] interpolatedYValues = new double[n+1];
		
		float granularity = (inputXValues.get(inputXValues.size()-1) - inputXValues.get(0)) / n;
		
		int count = 0;
		for (int i = 0; i <= n; i++) {
			float interpolatedX = inputXValues.get(0) + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(inputXValues.get(count) < interpolatedX) {
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
		return interpolatedYValues;
	}
}
