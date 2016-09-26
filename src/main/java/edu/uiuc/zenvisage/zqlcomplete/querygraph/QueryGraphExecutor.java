package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

/**
 * @author Edward Xue
 * Takes in a query graph and executes it
 */
public class QueryGraphExecutor {

	public VisualComponentList execute(QueryGraph queryGraph) {
		// TODO: design decision: casting and instanceof?
		ResultGraph resultGraph = new ResultGraph();
		
		VisualComponentList outputList = new VisualComponentList();
		
		Queue<Node> nodeQueue = new ArrayDeque<Node>();
		for (Node entryNode : queryGraph.entryNodes) {
			QueryNode currNode = (QueryNode) entryNode;
			
			nodeQueue.add(currNode);
			
			while(!nodeQueue.isEmpty()) {
				currNode = (QueryNode) nodeQueue.remove();
				currNode.execute();
				if (currNode.state == State.FINISHED) {
					// Add result node to contain the executed data
					// add children to queue
					nodeQueue.addAll(currNode.getChildren());
					
					// if this node should be outputted, update outputList
						// currently will only support last row with a *
				}
				else if (currNode.state == State.BLOCKED) {
					// Don't add children
				}
			}
			// from every entry node, keep traveling down to children.
			// each children keeps going until we are all finished, or we are blocked (waiting on another path)
		}
		
		return outputList;
	}
}
