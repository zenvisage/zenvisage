package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.sql.SQLException;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

/**
 * @author Edward Xue
 * The visual component node to be executed
 */
public class VisualComponentNode extends QueryNode{

	private VisualComponentQuery vc;
	private SQLQueryExecutor sqlQueryExecutor;
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
	
	public VisualComponentNode(VisualComponentQuery vc, LookUpTable table, SQLQueryExecutor sqlQueryExecutor) {
		super(table);
		this.vc = vc;
		this.sqlQueryExecutor = sqlQueryExecutor;
	}
	
	@Override
	public void execute() {
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		this.state = State.RUNNING;
		
		LookUpTable lookuptable = this.getLookUpTable();
		// update lookup table with axisvariables
		XColumn x = this.getVc().getX();
		YColumn y = this.getVc().getY();
		ZColumn z = this.getVc().getZ();
		
		// e.g., x1 <- 'year'
		if (!x.getVariable().equals("")) {
			AxisVariable axisVar = new AxisVariable(x.getVariable(), x.getValues());
			lookuptable.put(x.getVariable(), axisVar);
		}
		if (!y.getVariable().equals("")) {
			AxisVariable axisVar = new AxisVariable(y.getVariable(), y.getValues());
			lookuptable.put(y.getVariable(), axisVar);
		}
		if (!z.getVariable().equals("")) {
			AxisVariable axisVar = new AxisVariable(z.getVariable(), z.getValues());
			lookuptable.put(z.getVariable(), axisVar);
		}
		// call SQL backend
		ZQLRow row = buildRowFromNode();
		
		
		try {
			sqlQueryExecutor.ZQLQueryEnhanced(row, "real_estate");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.state = State.FINISHED;
		//update the look table with name variable, e.g, f1)
		String name = this.getVc().getName().getName();
		this.getLookUpTable().put(name, sqlQueryExecutor.getVisualComponentList());
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
	
	public void updateAxisVaribles(){
		//TODO
	}
	
}
