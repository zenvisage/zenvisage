package edu.uiuc.zenvisage.zqlcomplete.querygraph;

public class VisualComponentNode extends QueryNode{

	private VisualComponent vc;
	// private vc output
	
	@Override
	public void execute() {
		this.state = State.RUNNING;
	}

}
