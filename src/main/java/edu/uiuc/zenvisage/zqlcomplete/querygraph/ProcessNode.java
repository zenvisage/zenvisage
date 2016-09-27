package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;

/**
 * @author Edward Xue
 * ProcessNodes are nodes that contains the process/task component
 */
public class ProcessNode extends QueryNode {

	private Processe process;
	
	public ProcessNode(Processe process_) {
		super();
		process = process_;
	}
	
	@Override
	public Node execute(SQLQueryExecutor sqlQueryExecutor) {
		this.state = State.RUNNING;
		ProcessResultNode results = new ProcessResultNode();
		return results;
	}

}
