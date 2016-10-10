package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.sql.SQLException;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

/**
 * @author Edward Xue
 * The visual component node to be executed
 */
public class VisualComponentNode extends QueryNode{

	private VisualComponentQuery vc;
	// private vc output
	//TODO: build separate result node
	// call QueryGraphResults
	// VisualComponentResultNode
		// should not have the visual componentquery
	// ProcessResultNode
		//should have top 5 visualcomponents from list (for now)
	
	public VisualComponentNode(VisualComponentQuery vc) {
		this.vc = vc;
	}
	
	@Override
	public Node execute(SQLQueryExecutor sqlQueryExecutor) {
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return null;
		}
		this.state = State.RUNNING;
		
		// call SQL backend
		ZQLRow row = buildRowFromNode();
		try {
			sqlQueryExecutor.ZQLQueryEnhanced(row, "real_estate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.state = State.FINISHED;
		// place results in a resultNode
		VisualComponentResultNode results = new VisualComponentResultNode();
		results.setVcList(sqlQueryExecutor.getVisualComponentList());
		return results;
	}

	public VisualComponentQuery getVc() {
		return vc;
	}
	
	public void setVc(VisualComponentQuery vc) {
		this.vc = vc;
	}
	
	@Override
	public ZQLRow buildRowFromNode() {
		ZQLRow result = new ZQLRow(vc.getX(), vc.getY(), vc.getZ(), vc.getConstraints(), vc.getViz());
		// null processe and sketchPoints (for now)
		return result;
	}
	
}
