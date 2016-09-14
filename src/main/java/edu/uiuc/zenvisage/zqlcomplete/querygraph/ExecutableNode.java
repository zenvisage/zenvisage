package edu.uiuc.zenvisage.zqlcomplete.querygraph;


/**
 * @author Edward Xue
 * Abstraction of a node that is executable in our query language
 */
public abstract class ExecutableNode extends Node {
	
	public enum State {
		READY, RUNNING, BLOCKED, FINISHED;
	}
	
	protected State state; // "ready" "blocked" "finished"
	boolean resultNode;
	
	public ExecutableNode() {
		super();
		state = State.READY;
	}
	
	abstract public void execute();

	public State getState() {
		return state;
	}

}
