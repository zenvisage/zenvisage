package edu.uiuc.zenvisage.zqlcomplete.executor;

public class VisualComponentNode extends ExecutableNode{

	@Override
	public void execute() {
		this.state = State.RUNNING;
	}

}
