/**
 * 
 */
package edu.uiuc.zenvisage.zql.functions;

import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zql.AxisVariable;
import edu.uiuc.zenvisage.zql.AxisVariableScores;

/**
 * @author tarique
 *
 */
public interface T {
	public AxisVariableScores execute(VisualComponentList f1, List<AxisVariable> axisVariables);
}
