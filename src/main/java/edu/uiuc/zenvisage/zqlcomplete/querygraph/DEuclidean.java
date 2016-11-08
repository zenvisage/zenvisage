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
	
	//Tarique: I have changes the type of axisvariables to a class instead of a string, so that we can also see attrubute type, i.e, x,y, or z.
	
	@Override
	public AxisVariableScores execute(VisualComponentList f1, VisualComponentList f2,List<List<AxisVariable>> axisVariables) {
		// TODO Auto-generated method stub

		List<VisualComponent> f1List = f1.getVisualComponentList();
		List<VisualComponent> f2List = f2.getVisualComponentList();
		
		//Tarique: currently we are sorting only by z, we should be able to sort it by x, y or z, I have now provided 
		// the attribute type details in the Axisvariable as well
		VCComparator VC1 = new VCComparator(axisVariables.get(0));	
		VCComparator VC2= new VCComparator(axisVariables.get(1));
		f1List.sort(VC1);
		if(axisVariables.size()<2)
		f2List.sort(VC1);
		else
		f2List.sort(VC2);
	
		ArrayList<ArrayList<String>> axisvars = new ArrayList<ArrayList<String>>();;
		List<Double> scores = new ArrayList<Double>();
		AxisVariableScores axisVariableScores;
		
		if (axisVariables.size() == 1) {
			
			//TOFIXL: instead of two,make this generic 
			ArrayList<String> singleAxisvarsList = new ArrayList<String>();
			ArrayList<String> secondAxisvarsList = new ArrayList<String>();
		
			for (int i = 0, j = 0; i < f1List.size() && j < f2List.size(); ) {
				int zCompare = VC1.compare(f1List.get(i),f2List.get(i));
				if (zCompare == 0) {
					scores.add(calculateDistance(f1List.get(i), f2List.get(j)));
					singleAxisvarsList.add(VC1.extractAttribute(f1List.get(i),0));
					if(axisVariables.get(0).size()==2)
					{
						secondAxisvarsList.add(VC1.extractAttribute(f1List.get(i),1));
					}
					i++;
					j++;
				}
			}
			else if (f2List.size() == 1) {
				for (int i = 0; i < f1List.size(); i++) {
					scores.add(calculateEuclideanDistance(f1List.get(i), f2List.get(0)));
					singleAxisvarsList.add(f1List.get(i).getZValue().getStrValue());					
				}
			}
			else {
				// Want to compare the same Z, like 'CA' vs 'CA', but different X and Y for each
				for (int i = 0, j = 0; i < f1List.size() && j < f2List.size(); ) {
					int zCompare = f1List.get(i).getZValue().getStrValue().compareTo(f2List.get(j).getZValue().getStrValue());
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
		else if (axisVariables.size() == 2) {
			ArrayList<String> firstAxisvarsList = new ArrayList<String>();
			ArrayList<String> secondAxisvarsList = new ArrayList<String>();	
			for (int i = 0; i < f1List.size(); i++) {
				for (int j = 0; j < f2List.size(); j++) {
					scores.add(calculateDistance(f1List.get(i), f2List.get(j)));
					firstAxisvarsList.add(VC1.extractAttribute(f1List.get(i),0));
					secondAxisvarsList.add(VC1.extractAttribute(f2List.get(i),0));
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

	
}
