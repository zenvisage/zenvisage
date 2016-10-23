/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author tarique
 *
 */
public interface SortFilterPrimitive {

	public AxisVariableScores execute(AxisVariableScores fpo, int count);
	
	public class ScoreAndIndex {
		double score;
		int index;
		ScoreAndIndex(double s, int i) {
			this.score = s;
			this.index = i;
		}
	}
	
	public class SIComparator implements Comparator<ScoreAndIndex> {
		double maxOrMin;
		SIComparator(int maxOrMin) {
			this.maxOrMin = maxOrMin;
		}
		public int compare(ScoreAndIndex si1, ScoreAndIndex si2) {
			return (int) ((maxOrMin ) * (si1.score - si2.score));
		}
	}
}
