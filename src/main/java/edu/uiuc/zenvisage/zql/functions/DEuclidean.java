/**
 * 
 */
package edu.uiuc.zenvisage.zql.functions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.zql.AxisVariable;
import edu.uiuc.zenvisage.zql.AxisVariableScores;
import edu.uiuc.zenvisage.zql.functions.DDTW.VCComparator;

/**
 * @author tarique
 *
 */
public class DEuclidean implements D {

	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.D#execute(edu.uiuc.zenvisage.data.remotedb.VisualComponentList, edu.uiuc.zenvisage.data.remotedb.VisualComponentList, java.util.List)
	 */
	
	//Tarique: I have changes the type of axisvariables to a class instead of a string, so that we can also see attrubute type, i.e, x,y, or z.
	
	private void updateHashMap(Map<String, List<VisualComponent>> map, String key, VisualComponent vc) {
		List<VisualComponent> res = map.get(key);
		if (res == null) {
			res = new ArrayList<VisualComponent>();
			map.put(key, res);
		}
		res.add(vc);	// updating the referenced List in the hashmap	
	}
	
	// idea is to update the AxisVariableScores I am passing by reference in the parameter list
	private void process(VisualComponentList f1, VisualComponentList f2, List<AxisVariable> axisVars, int index, List<String> attributeConstraints, ArrayList<ArrayList<String>> outputAxisVars, List<Double> scores) {
		if (index >= axisVars.size()) {
			// base case: start processing for the visual components that satisfy all the constraints
			List<VisualComponent> f1List = new ArrayList<VisualComponent>();
			List<VisualComponent> f2List = new ArrayList<VisualComponent>();
			for (VisualComponent vc : f1.getVisualComponentList()) {
				for (String attributeConstraint : attributeConstraints) {
					boolean satisfied = false;
					if (attributeConstraint.equals(vc.getxAttribute())) satisfied = true;
					if (attributeConstraint.equals(vc.getyAttribute())) satisfied = true;
					if (attributeConstraint.equals(vc.getZValue().getStrValue())) satisfied = true;
					if (satisfied) {
						f1List.add(vc);
					}
				}
			}
			for (VisualComponent vc : f2.getVisualComponentList()) {
				for (String attributeConstraint : attributeConstraints) {
					boolean satisfied = false;
					if (attributeConstraint.equals(vc.getxAttribute())) satisfied = true;
					if (attributeConstraint.equals(vc.getyAttribute())) satisfied = true;
					if (attributeConstraint.equals(vc.getZValue().getStrValue())) satisfied = true;
					if (satisfied) {
						f2List.add(vc);
					}
				}
			}
			
			// these if statements are used if currently processing one vs many (eg one state vs many states), and using axisvar = state.*
			// when the constraint is on say NY, f2 will have a component, but f1 will have nothing. Just take f1 (CA) compare it to (NY)
			if (f1List.isEmpty()) {
				f1List = f1.getVisualComponentList();
			}
			if (f2List.isEmpty()) {
				f2List = f2.getVisualComponentList();
			}
			if(f1List.size() < f2List.size()) {
				List<VisualComponent> temp = f1List; // point to what f1List is pointing to
				f1List = f2List; // point f1List to what f2List is pointing to
				f2List = temp;
			}
			
			for (VisualComponent vc1 : f1List) {
				for (VisualComponent vc2 : f2List) {
					
					//scores.add(calculateNormalizedDistance(f1List.get(i), f2List.get(j)));
					scores.add(calculateNormalizedDistance(vc1, vc2));

					//for (int i = 0; i < axisVars.size(); i++) {
					//	outputAxisVars.get(i).add(extractAttribute(vc1, i, axisVars));
					//}
					outputAxisVars.get(0).add(extractAttribute(vc1, 0, axisVars));
					outputAxisVars.get(1).add(extractAttribute(vc2, 1, axisVars));
				}
			}
			return;
		}
		// For z, the values are like [CA, NY] for state
		// For x and y, the values are the list of attributes, like ['year','month']
		for(String value : axisVars.get(index).getValues()) {
			attributeConstraints.add(value);
			process(f1, f2, axisVars, index+1, attributeConstraints, outputAxisVars, scores);
			attributeConstraints.remove(value);
		}
	}
	
	public AxisVariableScores executeNewer(VisualComponentList f1, VisualComponentList f2, List<AxisVariable> axisVariables) {
		List<Double> scores = new ArrayList<Double>();
		AxisVariableScores axisVariableScores;
		ArrayList<ArrayList<String>> outputAxisVars = new ArrayList<ArrayList<String>>();;

		// there should be one outputAxisVar for each axisVariable
		// assumption: outputAxisVars of size 2
		for (int i = 0; i < 2; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			outputAxisVars.add(temp);
		}
		process(f1, f2, axisVariables, 0, new ArrayList<String>(), outputAxisVars, scores);
		axisVariableScores = new AxisVariableScores(outputAxisVars, scores);
		return axisVariableScores;
	}
	
	public AxisVariableScores executeNew(VisualComponentList f1, VisualComponentList f2, List<AxisVariable> axisVariables) {
		// eg key = month, value = visual components that have xAttribute as month
		//Map<String, List<VisualComponent>> xHashMap = new HashMap<String, List<VisualComponent>>();
		//Map<String, List<VisualComponent>> yHashMap = new HashMap<String, List<VisualComponent>>();
		//Map<String, List<VisualComponent>> zHashMap = new HashMap<String, List<VisualComponent>>();

		Map<String, List<VisualComponent>> f1HashMap = new HashMap<String, List<VisualComponent>>();
		Map<String, List<VisualComponent>> f2HashMap = new HashMap<String, List<VisualComponent>>();
	
		for (VisualComponent vc : f1.getVisualComponentList()) {
			String xAttribute = vc.getxAttribute();
			updateHashMap(f1HashMap, xAttribute, vc);
			updateHashMap(f1HashMap, vc.getyAttribute(), vc);
			updateHashMap(f1HashMap, vc.getzAttribute(), vc);
		}
		
		for (VisualComponent vc : f2.getVisualComponentList()) {
			updateHashMap(f2HashMap, vc.getxAttribute(), vc);
			updateHashMap(f2HashMap, vc.getyAttribute(), vc);
			updateHashMap(f2HashMap, vc.getzAttribute(), vc);			
		}
		List<VisualComponent> f1Candidates = new ArrayList<VisualComponent>(f1.getVisualComponentList());
		List<VisualComponent> f2Candidates = new ArrayList<VisualComponent>(f2.getVisualComponentList());
		
		for (AxisVariable axisVar : axisVariables) {
			
			// For z, the values are like [CA, NY] for state
			// For x and y, the values are the list of attributes, like ['year','month']
			for (String value : axisVar.getValues()) {
				for (Iterator<VisualComponent> it = f1Candidates.iterator(); it.hasNext();) {
					VisualComponent vc = it.next();
					List<VisualComponent> vcListHasAttribute = f1HashMap.get(axisVar.getAttribute());
					if (!vcListHasAttribute.contains(vc)) {
						it.remove();
					}
				}
				
				for (Iterator<VisualComponent> it = f2Candidates.iterator(); it.hasNext();) {
					VisualComponent vc = it.next();
					List<VisualComponent> vcListHasAttribute = f1HashMap.get(axisVar.getAttribute());
					if (!vcListHasAttribute.contains(vc)) {
						it.remove();
					}
				} 
			}
		}
		if (f1Candidates.isEmpty() || f2Candidates.isEmpty()) {
			return null;
		}
/*		// java 8 woo
		xHashMap.forEach((xKey, xValue) -> {
			yHashMap.forEach((yKey, yValue) -> {
				
			});
		});
*/
		return null;
	}
	
	@Override
	public AxisVariableScores execute(VisualComponentList f1, VisualComponentList f2,List<List<AxisVariable>> axisVariables) {
		// TODO Auto-generated method stub
		return executeNewer(f1, f2, axisVariables.get(0));
/*		
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
		*/
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

	public String extractAttribute(VisualComponent v1,int order, List<AxisVariable> axisVariables) {
		
		// small fix so when we have a shared axisVariable for two VCLists (eg both are state, so only provide 1 axisVar)
		if (order >= axisVariables.size()) {
			order = 0;
		}
		if(axisVariables.get(order).getAttributeType().equals("Z"))
			return v1.getZValue().toString();
		else if(axisVariables.get(order).getAttributeType().equals("Y"))
			return v1.getyAttribute();
		else {
			return v1.getxAttribute();
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

	// Currently only works for calculating distance between floats and integers, undefined for string value comparison
	public double calculateNormalizedDistance(VisualComponent v1, VisualComponent v2) {
		ArrayList<WrapperType> y1 = v1.getPoints().getYList();
		ArrayList<WrapperType> y2 = v2.getPoints().getYList();

		double[] y1Normalized;
		double[] y2Normalized;
		
		if (y1.size() == y2.size()) {
			
			// normalize!
			y1Normalized = normalize(y1);
			y2Normalized = normalize(y2);

		}
		else if (y1.size() > y2.size()) {
			// need to make y2 the same size as y1 for the euclidean metric, do this with interpolation
			double[] interpolatedY2Values = getInterpolatedData(v2.getPoints().getXList(), y2, y1.size());	
			y1Normalized = normalize(y1);
			y2Normalized = normalize(interpolatedY2Values);			
		}
		else {
			double[] interpolatedY1Values = getInterpolatedData(v1.getPoints().getXList(), y1, y2.size());	
			y1Normalized = normalize(interpolatedY1Values);
			y2Normalized = normalize(y2);				
		}
		
		double distance = 0.0;
		for (int i = 0; i < y1Normalized.length; i++) {
			distance += Math.sqrt(Math.pow(y1Normalized[i] - y2Normalized[i], 2));
		}
		return distance;
	}
	
	public double[] getInterpolatedData(List<WrapperType> inputXValues, List<WrapperType> inputYValues, int length) {		
		int n = length;
		float[] interpolatedXValues = new float[n];
		double[] interpolatedYValues = new double[n];

		interpolatedXValues[0] = inputXValues.get(0).getNumberValue();
		interpolatedYValues[0] = inputYValues.get(0).getNumberValue();
		interpolatedXValues[n-1] = inputXValues.get(inputXValues.size()-1).getNumberValue();
		interpolatedYValues[n-1] = inputYValues.get(inputYValues.size()-1).getNumberValue();
		
		float granularity = (inputXValues.get(inputXValues.size()-1).getNumberValue() - inputXValues.get(0).getNumberValue()) / (n - 1);
		
		int count = 1;
		for (int i = 1; i < n ; i++) {
			float interpolatedX = inputXValues.get(0).getNumberValue() + i * granularity;
			interpolatedXValues[i] = interpolatedX;
			
			while(inputXValues.get(count).getNumberValue() < interpolatedX) {
				if (count < inputXValues.size())
					count++;
			}
			if (inputXValues.get(count).getNumberValue() == interpolatedX) {
				interpolatedYValues[i] = inputYValues.get(count).getNumberValue();
			}
			else {
				float xDifference = inputXValues.get(count).getNumberValue() - inputXValues.get(count-1).getNumberValue();
				double yDifference = inputYValues.get(count).getNumberValue() - inputYValues.get(count-1).getNumberValue();
				interpolatedYValues[i] = inputYValues.get(count - 1).getNumberValue() + (interpolatedX - inputXValues.get(count-1).getNumberValue()) / xDifference * yDifference;				
			}
		}
		return interpolatedYValues;
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
	
	public double[] normalize(double[] y) {
		double[] values = new double[y.length];
		for(int i = 0; i < y.length; i++){
			values[i] = y[i];
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
