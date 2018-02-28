/**
 * 
 */
package edu.uiuc.zenvisage.zql.functions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.zql.AxisVariable;
import edu.uiuc.zenvisage.zql.AxisVariableScores;
import net.sf.javaml.distance.fastdtw.dtw.DTW;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;

/**
 * @author tarique
 *
 */
public class DDTW implements D {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.D#execute(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	@Override
	public AxisVariableScores execute(VisualComponentList f1, VisualComponentList f2, List<List<AxisVariable>> axisVariables) {
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
					scores.add(calculateDistance(f1List.get(i), f2List.get(j)));
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
					scores.add(calculateDistance(f1List.get(i), f2List.get(j)));
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
	
	public double calculateDistance(VisualComponent v1, VisualComponent v2) {
		ArrayList<WrapperType> y1 = v1.getPoints().getYList();
		ArrayList<WrapperType> y2 = v2.getPoints().getYList();
		
		if (y1.size() != y2.size() || y1.size() == 0) {
			return 0.0;
		}
		
		double[] src = new double[y1.size()];
		double[] tar = new double[y2.size()];
		
		for (int i = 0; i < y1.size(); i++) {
			src[i] = y1.get(i).getNumberValue();
			tar[i] = y2.get(i).getNumberValue();
		}
		
		TimeSeries ts1 = new TimeSeries(1);
		TimeSeries ts2 = new TimeSeries(1);
		
		for (int i = 0; i < src.length; i++) {
			double [] point = new double[1];
			point[0] = src[i];
			TimeSeriesPoint tp = new TimeSeriesPoint(point);
			if (i == 0) {
				ts1.addLast(i, tp);
			}
			else {
				ts1.addLast(i, tp);
			}
		}
		
		for(int i = 0; i < tar.length; i++) {
			double[] point = new double[1];
			point[0] = tar[i];
			TimeSeriesPoint tp = new TimeSeriesPoint(point);
			if (i == 0) {
				ts2.addLast(i, tp);
			}
			else {
				ts2.addLast(i, tp);
			}
		}
		
		return DTW.getWarpInfoBetween(ts1, ts2).getDistance();
	}

	
}
