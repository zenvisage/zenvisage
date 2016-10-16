package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLExecutor;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRowResult;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTableResult;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

/**
 * @author Edward Xue
 * ProcessNodes are nodes that contains the process/task component
 */
public class ProcessNode extends QueryNode {

	private Processe process;
	
	public Processe getProcess() {
		return process;
	}

	public void setProcess(Processe process) {
		this.process = process;
	}

	public ProcessNode(Processe process_) {
		super();
		process = process_;
	}
	
	public ProcessNode(Processe process_, LookUpTable table) {
		super(table);
		this.process = process_;
	}
	
	@Override
	public void execute() {
		// MAKE this argument something for VCNode only! in constructor!
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		
		this.state = State.RUNNING;

		if(process.getMethod().equals("D")) {
			DEuclidean d = new DEuclidean();
			
			// Cannot process D on this incorrect input
			if (process.getArguments().size() != 2) {
				// TODO Just returning for now
				return;
			}
			String name1 = process.getArguments().get(0);
			String name2 = process.getArguments().get(1);
			// Basic user error checking
			if(name1.equals("") || name2.equals("")){
				return;
			}
			Object object1 = lookuptable.get(name1);
			if (! (object1 instanceof VisualComponentList)) {
				return;
			}
			Object object2 = lookuptable.get(name2);
			if (! (object2 instanceof VisualComponentList)) {
				return;
			}
			VisualComponentList f1 = (VisualComponentList) lookuptable.get(process.getArguments().get(0));
			VisualComponentList f2 = (VisualComponentList) lookuptable.get(process.getArguments().get(1));
			
			AxisVariableScores axisVariableScores = d.execute(f1, f2, process.getAxis());
			
			if (process.getMetric().equals("argmax")) {
				ArgMaxSortFilterPrimitive argmaxFilter = new ArgMaxSortFilterPrimitive();
				// TODO: count is stored as? "10"? "k=10"?
				axisVariableScores = argmaxFilter.execute(axisVariableScores, Integer.parseInt(process.getCount()));
			}
		}
		
		/**
		 * update with all process variables
		//String name = rowResult.getZqlProcessResult().getzType();
		if(!name.equals("")) {
			AxisVariable axisVar = new AxisVariable(name, rowResult.getZqlProcessResult().getzValues());
			this.getLookUpTable().put(name, axisVar);
		}
		**/
		// mock 
	}
	
	@Override
	public ZQLRow buildRowFromNode() {
		ZQLRow result = new ZQLRow(null, null, null, null, null);
		result.setProcesse(process);
		return result;
	}

}
