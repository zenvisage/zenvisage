package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;

/**
 * @author Edward Xue
 * Abstraction of a node in our query language
 * Either this is a "plan" node, or a "state/result" node
 */
public abstract class QueryNode extends Node {
	
	public enum State {
		READY, RUNNING, BLOCKED, FINISHED;
	}
	
	protected State state; // "ready" "blocked" "finished"
	
	public QueryNode() {
		super();
		state = State.READY;
	}
	
	abstract public Node execute(SQLQueryExecutor sqlQueryExecutor);

	public State getState() {
		return state;
	}

}
