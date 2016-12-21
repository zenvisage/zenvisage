/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;

/**
 * @author tarique
 *
 */
public interface T {
	public AxisVariableScores execute(VisualComponentList f1, List<AxisVariable> axisVariables);
}
