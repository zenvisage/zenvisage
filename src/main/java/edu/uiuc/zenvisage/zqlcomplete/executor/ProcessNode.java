package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward Xue
 * ProcessNodes are nodes that contains the process/task component
 */
public class ProcessNode extends ExecutableNode {

	private Processe process;
	private List<Integer> data;
	
	public ProcessNode(Processe process_, boolean resultNode) {
		super();
		process = process_;
		if (resultNode) {
			data = new ArrayList<Integer>();
		}
	}
	
	@Override
	public void execute() {
		this.state = State.RUNNING;
		
	}

}
