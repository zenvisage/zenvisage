/**
 * 
 */
package edu.uiuc.zenvisage.zql.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.zql.AxisVariable;
import edu.uiuc.zenvisage.zql.AxisVariableScores;

/**
 * @author tarique
 *
 */
public class TIncreasingness implements T {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.T#T(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	@Override
	public AxisVariableScores execute(VisualComponentList f1, List<AxisVariable> axisVariables) {
		// TODO Auto-generated method stub
		
		List<VisualComponent> f1List = f1.getVisualComponentList();
		ArrayList<ArrayList<String>> axisvars = new ArrayList<ArrayList<String>>();;
		List<Double> scores = new ArrayList<Double>();

		ArrayList<String> singleAxisvarsList = new ArrayList<String>();		
		for (int i = 0; i < f1List.size(); i++) {
			singleAxisvarsList.add(f1List.get(i).getZValue().getStrValue());
			scores.add(getTValue(f1List.get(i)));
		}
		axisvars.add(singleAxisvarsList);
		AxisVariableScores axisVariableScores = new AxisVariableScores(axisvars, scores);
		
		return axisVariableScores;
	}
	
	public double getTValue(VisualComponent v) {
		ArrayList <WrapperType> xValues = v.getPoints().getXList();
		ArrayList <WrapperType> yValues = v.getPoints().getYList();
		
		if (xValues.size() == 0 || xValues.size() != yValues.size()) {
			return Double.MIN_VALUE;
		}

		double[] xNormalized = normalize(xValues);
		double[] yNormalized = normalize(yValues);
		
		double sumXY = 0.0;
		double sumX = 0.0;
		double sumY = 0.0;
		double sumXSquare = 0.0;
		for (int i = 0; i < xNormalized.length; i++) {
			double x = xNormalized[i];
			double y = yNormalized[i];
			sumX += x;
			sumY += y;
			sumXSquare += Math.pow(x, 2);
			sumXY += x * y;
		}
		
		double slope = (xNormalized.length*1.0 * sumXY - sumX*sumY) / (xNormalized.length*1.0*sumXSquare - Math.pow(sumX, 2));
		return slope;
	}
	
	public double getTestValue(VisualComponent v) {
		ArrayList <WrapperType> xValues = v.getPoints().getXList();
		ArrayList <WrapperType> yValues = v.getPoints().getYList();
		
		if (xValues.size() == 0 || xValues.size() != yValues.size()) {
			return Double.MIN_VALUE;
		}
		
		double[] xNormalized = normalize(xValues);
		double[] yNormalized = normalize(yValues);
		
		double sumSlope = 0;
		for (int i = 0; i < xValues.size() - 1; i += 1) {
			double x = xNormalized[i];
			double y = yNormalized[i];

			double x2 = xNormalized[i+1];
			double y2 = yNormalized[i+1];
			
			sumSlope += (y2 - y) / (x2 - x);
		}
		
		double temp = sumSlope / xValues.size();
//		System.out.println(v.getZValue());
//		System.out.println("TStuff");
//		System.out.println(xValues.toString());
//		System.out.println(Arrays.toString(xNormalized));
//		System.out.println(yValues.toString());
//		System.out.println(Arrays.toString(yNormalized));
//		System.out.println(temp);
		return temp;
	}
	
	// Basic ZScore normalization
	public double[] normalize(ArrayList<WrapperType> y) {
		double[] values = new double[y.size()];
		for(int i = 0; i < y.size(); i++){
			values[i] = y.get(i).getNumberValue();
		}
		
		double mean = StatUtils.mean(values);
		double std = FastMath.sqrt(StatUtils.variance(values));
		
		for(int i = 0; i < values.length; i++) {
			if (std == 0) {
				values[i] = 0;
			}
			else {
				values[i] = (values[i] - mean) / std;
			}
		}
		return values;
	}
}
