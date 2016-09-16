package edu.uiuc.zenvisage.zqlcomplete.querygraph;

public class VisualComponentNode extends QueryNode{

	private VisualComponent vc;
	// private vc output
	private boolean resultNode = false;
	
	public VisualComponentNode(VisualComponent vc, boolean resultNode) {
		this.vc = vc;
		this.resultNode = resultNode;
	}
	
	@Override
	public void execute() {
		this.state = State.RUNNING;
	}

}
