package edu.uiuc.zenvisage.zql;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.zql.QueryNode.State;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

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
	
	protected LookUpTable lookuptable;
 	
	public QueryNode() {
		super();
		state = State.READY;
	}
	
	public QueryNode(LookUpTable table) {
		super();
		state = State.READY;
		// add a reference to look-up table, which is created once and linked to all the nodes during parsing or graph building.
		lookuptable = table;
	}
	
	abstract public void execute();

	public State getState() {
		return state;
	}

	abstract protected ZQLRow buildRowFromNode();
	
	/**
	 * If one parent has not finished, we are still blocked
	 * @return
	 */
	protected boolean isBlocked() {
		boolean blocked = false;
		for (Node parent : this.getParents()) {
			if ( ((QueryNode)parent).state != State.FINISHED ) {
				blocked = true;
			}
		}
		return blocked;
	}
	
	public LookUpTable getLookUpTable() {
		return this.lookuptable;
	}
}
