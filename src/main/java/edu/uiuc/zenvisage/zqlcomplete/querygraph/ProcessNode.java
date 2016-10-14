package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
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
		ZQLRow row = buildRowFromNode();
		ZQLRowResult rowResult = new ZQLRowResult();
		ZQLTableResult tableResult = new ZQLTableResult();
		try {
			ZQLExecutor.executeProcess(row, rowResult, tableResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// TODO: only supports 1 output z1 <- Similar
		String name = rowResult.getZqlProcessResult().getzType();
		if(!name.equals("")) {
			AxisVariable axisVar = new AxisVariable(name, rowResult.getZqlProcessResult().getzValues());
			this.getLookUpTable().put(name, axisVar);
		}
		
	}
	
	@Override
	public ZQLRow buildRowFromNode() {
		ZQLRow result = new ZQLRow(null, null, null, null, null);
		result.setProcesse(process);
		return result;
	}

}
