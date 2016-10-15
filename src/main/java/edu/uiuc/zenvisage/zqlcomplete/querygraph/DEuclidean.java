/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;

/**
 * @author tarique
 *
 */
public class DEuclidean implements D {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.D#execute(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	@Override
	public AxisVariableScores execute(VisualComponentList f1, VisualComponentList f2, List<String> axisVariables) {
		// TODO Auto-generated method stub

		List<VisualComponent> f1List = f1.getVisualComponentList();
		List<VisualComponent> f2List = f2.getVisualComponentList();
		
		f1List.sort(new VCComparator());
		f2List.sort(new VCComparator());
		
		ArrayList<ArrayList<String>> axisvars = new ArrayList<ArrayList<String>>();;
		List<Double> scores = new ArrayList<Double>();
		AxisVariableScores axisVariableScores;
		
		if (axisVariables.size() == 1) {
			ArrayList<String> singleAxisvarsList = new ArrayList<String>();
			for (int i = 0, j = 0; i < f1List.size() && j < f2List.size(); ) {
				int zCompare = f1List.get(i).getZValue().getStrValue().compareTo(f2List.get(i).getZValue().getStrValue());
				
				if (zCompare == 0) {
					scores.add(calculateEuclideanDistance(f1List.get(i), f2List.get(j)));
					singleAxisvarsList.add(f1List.get(i).getZValue().getStrValue());
					i++;
					j++;
				}
				else if (zCompare < 0) {
					i++;
				}
				else {
					j++;
				}
			}
			
			axisvars.add(singleAxisvarsList);
//			Double[] scoreArray = scores.toArray(new Double[scores.size()]);
			axisVariableScores = new AxisVariableScores(axisvars, scores);
			return axisVariableScores;
			
		}
		else if (axisVariables.size() == 2) {
			ArrayList<String> firstAxisvarsList = new ArrayList<String>();
			ArrayList<String> secondAxisvarsList = new ArrayList<String>();	
			for (int i = 0; i < f1List.size(); i++) {
				for (int j = 0; j < f2List.size(); j++) {
					scores.add(calculateEuclideanDistance(f1List.get(i), f2List.get(j)));
					firstAxisvarsList.add(f1List.get(i).getZValue().getStrValue());
					secondAxisvarsList.add(f2List.get(j).getZValue().getStrValue());
				}
			}
			
			axisvars.add(firstAxisvarsList);
			axisvars.add(secondAxisvarsList);
			axisVariableScores = new AxisVariableScores(axisvars, scores);
			return axisVariableScores;
		}
		
		return null;
	}
	
	public static class VCComparator implements Comparator<VisualComponent> {
		public int compare(VisualComponent v1, VisualComponent v2) {
			return v1.getZValue().getStrValue().compareToIgnoreCase(v2.getZValue().getStrValue());
		}
	}
	
	public double calculateEuclideanDistance(VisualComponent v1, VisualComponent v2) {
		ArrayList<WrapperType> y1 = v1.getPoints().getYList();
		ArrayList<WrapperType> y2 = v2.getPoints().getYList();
		
		if (y1.size() == y2.size()) {
			double distance = 0.0;
			for (int i = 0; i < y1.size(); i++) {
				distance += Math.sqrt(Math.pow(y1.get(i).getNumberValue() - y2.get(i).getNumberValue(), 2));
			}
			return distance;
		}
		else
			return Double.MAX_VALUE;
	}

	
}
