/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.DDTW.VCComparator;

/**
 * @author tarique
 *
 */
public class DEuclidean implements D {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.D#execute(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	
	//Tarique: I have changes the type of axisvariables to a class instead of a string, so that we can also see attrubute type, i.e, x,y, or z.
	
	@Override
	public AxisVariableScores execute(VisualComponentList f1, VisualComponentList f2,List<List<AxisVariable>> axisVariables) {
		// TODO Auto-generated method stub

		List<VisualComponent> f1List = f1.getVisualComponentList();
		List<VisualComponent> f2List = f2.getVisualComponentList();
		
		//Tarique: currently we are sorting only by z, we should be able to sort it by x, y or z, I have now provided 
		// the attribute type details in the Axisvariable as well
		VCComparator VC1 = new VCComparator(axisVariables.get(0));	
		VCComparator VC2 = null;
		f1List.sort(VC1);
		if(axisVariables.size()<2) {
			f2List.sort(VC1);
			VC2 = new VCComparator(null);
		} else {
			VC2 = new VCComparator(axisVariables.get(1));
			f2List.sort(VC2);
		}
	
		ArrayList<ArrayList<String>> axisvars = new ArrayList<ArrayList<String>>();;
		List<Double> scores = new ArrayList<Double>();
		AxisVariableScores axisVariableScores;
		
		// one vs many or many vs one case (comparing f1 visual component vs f2 list of visual components)
		if(axisVariables.size() == 1 && (f1List.size() == 1 || f2List.size() == 1)) {
			// allows use to interpret v2<-argmax_{z2} instead of v1,v2<-argmax_{z1}x{z2}
			ArrayList<String> firstAxisvarsList = new ArrayList<String>();
			for (int i = 0; i < f1List.size(); i++) {
				for (int j = 0; j < f2List.size(); j++) {
					scores.add(calculateNormalizedDistance(f1List.get(i), f2List.get(j)));
					if(f1List.size() > f2List.size()) {
						firstAxisvarsList.add(VC1.extractAttribute(f1List.get(i),0));
						
					}
					//eg f1<-{CA_VC} f2<-{CA_VC, NY_VC, etc..} grab the attribute from f2
					if(f1List.size() < f2List.size()) {
						firstAxisvarsList.add(VC1.extractAttribute(f2List.get(j),0));
					}
				}
			}
		
			axisvars.add(firstAxisvarsList);
			axisVariableScores = new AxisVariableScores(axisvars, scores);
			return axisVariableScores;			
		}
		// one to one case (pairwise, compare pairs of (sorted) visual components of f1 with vcs of f2)
		else if (axisVariables.size() == 1) {
			
			//TOFIXL: instead of two,make this generic 
			ArrayList<String> singleAxisvarsList = new ArrayList<String>();
			ArrayList<String> secondAxisvarsList = new ArrayList<String>();
		
			for (int i = 0, j = 0; i < f1List.size() && j < f2List.size(); ) {
				int zCompare = VC1.compare(f1List.get(i),f2List.get(i));
				if (zCompare == 0) {
					scores.add(calculateNormalizedDistance(f1List.get(i), f2List.get(j)));
					singleAxisvarsList.add(VC1.extractAttribute(f1List.get(i),0));
					if(axisVariables.get(0).size()==2)
					{
						secondAxisvarsList.add(VC1.extractAttribute(f1List.get(i),1));
					}
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
			if(axisVariables.get(0).size()==2)
			{
			 axisvars.add(secondAxisvarsList);
			}
//			Double[] scoreArray = scores.toArray(new Double[scores.size()]);
			axisVariableScores = new AxisVariableScores(axisvars, scores);
			return axisVariableScores;
			
		}
		// explicit crossproduct, many vs many case
		else if (axisVariables.size() == 2) {
			ArrayList<String> firstAxisvarsList = new ArrayList<String>();
			ArrayList<String> secondAxisvarsList = new ArrayList<String>();	
			for (int i = 0; i < f1List.size(); i++) {
				for (int j = 0; j < f2List.size(); j++) {
					scores.add(calculateNormalizedDistance(f1List.get(i), f2List.get(j)));
					firstAxisvarsList.add(VC1.extractAttribute(f1List.get(i),0));
					secondAxisvarsList.add(VC1.extractAttribute(f2List.get(j),0));
				}
			}
		
			axisvars.add(firstAxisvarsList);
			axisvars.add(secondAxisvarsList);
			axisVariableScores = new AxisVariableScores(axisvars, scores);
			return axisVariableScores;
		}
		
		return null;
	}
	
	
	
	
	public  class VCComparator implements Comparator<VisualComponent> {
		List<AxisVariable> axisVariables;
		public VCComparator(List<AxisVariable>  axisVariables)
		{
			this.axisVariables=axisVariables;
		}
		
		public int compare(VisualComponent v1, VisualComponent v2) {
			int cmp=0;
			for(AxisVariable axisVariabe: axisVariables)
			{
				if(axisVariabe.getAttributeType().equals("Z"))
				 cmp=comparealongZ(v1, v2);
				else if(axisVariabe.getAttributeType().equals("Y"))
				{
				cmp=comparealongY(v1, v2);
				}
				else{
					cmp=comparealongX(v1, v2);
				}
				if(cmp!=0)
					break;
					
			}
			return cmp;
		}
		
		public int comparealongX(VisualComponent v1, VisualComponent v2){
			return v1.getxAttribute().compareToIgnoreCase(v2.getxAttribute());
		}
		
		public int comparealongY(VisualComponent v1, VisualComponent v2){
		    return v1.getyAttribute().compareToIgnoreCase(v2.getyAttribute());
		}
		
		public int comparealongZ(VisualComponent v1, VisualComponent v2){
			return v1.getZValue().getStrValue().compareToIgnoreCase(v2.getZValue().getStrValue());
		}
		
		public String extractAttribute(VisualComponent v1,int order){
			  if(axisVariables.get(order).getAttributeType().equals("Z"))
				 return v1.getZValue().toString();
			  else if(axisVariables.get(order).getAttributeType().equals("Y"))
				 return v1.getyAttribute();
			  else {
				return v1.getxAttribute();
				}		
		}

	}
	
	
	public double calculateDistance(VisualComponent v1, VisualComponent v2) {
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

	public double calculateNormalizedDistance(VisualComponent v1, VisualComponent v2) {
		ArrayList<WrapperType> y1 = v1.getPoints().getYList();
		ArrayList<WrapperType> y2 = v2.getPoints().getYList();
		
		if (y1.size() == y2.size()) {
			
			// normalize!
			double[] y1Normalized = normalize(y1);
			double[] y2Normalized = normalize(y2);
			
			double distance = 0.0;
			for (int i = 0; i < y1Normalized.length; i++) {
				distance += Math.sqrt(Math.pow(y1Normalized[i] - y2Normalized[i], 2));
			}
			return distance;
		}
		else
			return Double.MAX_VALUE;		
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
