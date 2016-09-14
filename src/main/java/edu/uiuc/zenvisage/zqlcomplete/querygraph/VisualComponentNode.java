package edu.uiuc.zenvisage.zqlcomplete.querygraph;

public class VisualComponentNode extends ExecutableNode{

	private VisualComponent vc;
	// private vc output
	
	@Override
	public void execute() {
		this.state = State.RUNNING;
	}

}
