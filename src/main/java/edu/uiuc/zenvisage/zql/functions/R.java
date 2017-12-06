package edu.uiuc.zenvisage.zql.functions;

import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zql.AxisVariableScores;

public interface R {
	public AxisVariableScores execute(VisualComponentList f1, int k);
}
