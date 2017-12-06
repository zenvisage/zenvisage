/**
 * 
 */
package edu.uiuc.zenvisage.zql.filters;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.zql.AxisVariableScores;

/**
 * @author tarique
 *
 */
public class ArgMaxSortFilterPrimitive implements SortFilterPrimitive {

	// with this as -1, we sort in decreasing order
	int maxOrMin = -1;
	
	/* (non-Javadoc)
	 * @see edu.uiuc.zenvisage.zqlcomplete.querygraph.SortFilterPrimitive#execute(edu.uiuc.zenvisage.zqlcomplete.querygraph.FunctionalPrimitiveOutput)
	 */
	// select max k (count)
	public AxisVariableScores execute(AxisVariableScores fpo, int count)
	{
		// TODO Auto-generated method stub
		ArrayList<ArrayList<String>> axisvars = fpo.getAxisvars();
		double[] scores = fpo.getScore();
		if (count > scores.length) {
			count = scores.length;
		}
		
		List<ScoreAndIndex> si = new ArrayList<ScoreAndIndex>();
		for (int i = 0; i < scores.length; i++) {
			si.add(new ScoreAndIndex(scores[i], i));
		}
		
		si.sort(new SIComparator(maxOrMin));
		
		ArrayList<ArrayList<String>> filteredAxisvars = new ArrayList<ArrayList<String>>();
		double[] filteredScores = new double[count];
		
		if (axisvars.size() == 1) {
			ArrayList<String> singleAxisvars = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				singleAxisvars.add(axisvars.get(0).get(si.get(i).index));
				filteredScores[i] = si.get(i).score;
			}
			filteredAxisvars.add(singleAxisvars);
		}
		else if (axisvars.size() == 2) {
			ArrayList<String> firstAxisvars = new ArrayList<String>();
			ArrayList<String> secondAxisvars = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				firstAxisvars.add(axisvars.get(0).get(si.get(i).index));
				secondAxisvars.add(axisvars.get(1).get(si.get(i).index));
				filteredScores[i] = si.get(i).score;
			}
			filteredAxisvars.add(firstAxisvars);
			filteredAxisvars.add(secondAxisvars);
		}
		else {
			return null;
		}
		
		AxisVariableScores filteredAVS = new AxisVariableScores(filteredAxisvars, filteredScores);
		
		return filteredAVS;
	}
	

}
