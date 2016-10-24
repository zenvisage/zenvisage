package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.collections.MapUtils;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryNode.State;

/**
 * @author Edward Xue
 * Takes in a query graph and executes it
 */
public class QueryGraphExecutor {

	/**
	 * Taking in a queryGraph, execute it from entryNodes down to leaves.
	 * In the meantime, buildup the resultGraph to go along with it.
	 * @param queryGraph
	 * @return What to visualize
	 */
	public static VisualComponentList execute(QueryGraph queryGraph) {
		// TODO: design decision: casting and instanceof?
		ResultGraph resultGraph = new ResultGraph();
		// hash table representation of the nodes in resultGraph
		Map<String, Node> resultNodeMap = new HashMap<String, Node>();
		
		VisualComponentList outputList = new VisualComponentList();

		Queue<Node> nodeQueue = new ArrayDeque<Node>();
		for (Node entryNode : queryGraph.entryNodes) {
			QueryNode currNode = (QueryNode) entryNode;
			
			nodeQueue.add(currNode);
			
			while(!nodeQueue.isEmpty()) {
				currNode = (QueryNode) nodeQueue.remove();
				System.out.println("Processing Node: "+ currNode.toString());
				currNode.execute(); 
				if (currNode.state == State.FINISHED) {
					outputList = (VisualComponentList) (currNode).lookuptable.get("f1");
					System.out.println(" My map");
					MapUtils.debugPrint(System.out, "myMap", currNode.lookuptable.getVariables());
					System.out.println(" My map");
					System.out.println(Arrays.toString(currNode.lookuptable.getVariables().entrySet().toArray()));
					// Add result node to contain the executed data
					for (Node parent : currNode.getParents()) {
						if (parent instanceof VisualComponentNode) {
							// grab the corresponding resultgraph node
							
							// TODO: IS USING NAME AS ID OK? 
							String parentName = ((VisualComponentNode) parent).getVc().getName().getName();
							if(resultNodeMap.containsKey(parentName)) {
								Node resultNodeParent = resultNodeMap.get(parentName);
								resultNodeParent.addChild(currNode);
								currNode.addParent(resultNodeParent);
							}
						} else if (parent instanceof ProcessNode) {
							
						}
					}
					// add currNode to hashmap
					if (currNode instanceof VisualComponentNode) {
						String currName = ((VisualComponentNode) currNode).getVc().getName().getName();
						resultNodeMap.put(currName, currNode);
					} else if (currNode instanceof ProcessNode){
						
					}
					
					// add children to queue
					nodeQueue.addAll(currNode.getChildren());
					
					// TODO: if this node should be outputted, update outputList
						// currently will only support last row with a *
				} else if (currNode.state == State.BLOCKED) {
					// Don't add children
					
					// Add node back in end of queue!
					nodeQueue.add(currNode);
				}
			}
			// from every entry node, keep traveling down to children.
			// each children keeps going until we are all finished, or we are blocked (waiting on another path)
		}
		return outputList;
	}
}
