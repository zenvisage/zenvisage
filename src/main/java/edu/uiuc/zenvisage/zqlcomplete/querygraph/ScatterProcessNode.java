package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.service.ScatterRep;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

public class ScatterProcessNode extends ProcessNode {

	public ScatterProcessNode(Processe process_, LookUpTable table) {
		super(process_, table);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void execute() {
		if (isBlocked()) {
			this.state = State.BLOCKED;
			return;
		}
		
		this.state = State.RUNNING;
		//
		Result finalOutput = new Result();
		if(process.getMethod().equals("Rep")) {
			scatterRepExecution();
		} else if (process.getMethod().equals("Rank")) {
			scatterRankExecution();
		}
		this.state = State.FINISHED;
	}
	
	// currently data transform + task processing
	private void scatterRepExecution() {
		// lookup table the VCNode we depend on
	}
	
	private void scatterRankExecution() {
		// lookup table the VCNode we depend on	
	}
}
