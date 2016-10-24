/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;

/**
 * @author tarique
 *
 */
public class TIncreasingness implements T {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.T#T(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	@Override
	public AxisVariableScores execute(VisualComponentList f1, List<String> axisVariables) {
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
		
		double sumXY = 0.0;
		double sumX = 0.0;
		double sumY = 0.0;
		double sumXSquare = 0.0;
		for (int i = 0; i < xValues.size(); i++) {
			double x = xValues.get(i).getNumberValue();
			double y = yValues.get(i).getNumberValue();
			sumX += x;
			sumY += y;
			sumXSquare += Math.pow(x, 2);
			sumXY += x * y;
		}
		
		double slope = (xValues.size()*1.0 * sumXY - sumX*sumY) / (xValues.size()*1.0*sumXSquare - Math.pow(sumX, 2));
		return slope;
	}
}
