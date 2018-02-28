package edu.uiuc.zenvisage.zql;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.zql.QueryNode.State;
import edu.uiuc.zenvisage.zqlcomplete.executor.VizColumn;

/**
 * @author Edward Xue
 * Takes in a query graph and executes it
 */
public class QueryGraphExecutor {
	static final Logger logger = LoggerFactory.getLogger(QueryGraphExecutor.class);
	/**
	 * Taking in a queryGraph, execute it from entryNodes down to leaves.
	 * In the meantime, buildup the resultGraph to go along with it.
	 * @param queryGraph
	 * @return What to visualize
	 */
	public static VisualComponentList execute(QueryGraph queryGraph) {
		// TODO: design decision: casting and instanceof?		
		VisualComponentList outputList = new VisualComponentList();
		outputList.setVisualComponentList(new ArrayList<VisualComponent>());

		Queue<Node> nodeQueue = new ArrayDeque<Node>();
		QueryNode currNode;
		for (Node entryNode : queryGraph.entryNodes) {
			currNode = (QueryNode) entryNode;
			
			nodeQueue.add(currNode);
		}
			while(!nodeQueue.isEmpty()) {
				currNode = (QueryNode) nodeQueue.remove();
				if (currNode.state == State.FINISHED) {
					// this means we enqueued this node twice by accident
					// eg two parents have this as a child.
					// probably needs to be fixed with some "visited" state 
					continue;
				}
				logger.info("Processing Node: "+ currNode.toString());
				currNode.execute(); 
				if (currNode.state == State.FINISHED) {
					// TODO: currently always outputs result of final node!
					if (currNode instanceof VisualComponentNode) {
						VisualComponentNode temp = (VisualComponentNode) currNode;
						// gets the f1, f2, or so on...
						
						// If this node was selected as an output node (Eg *f2), update the execution output to include this node's output as well
						if (temp.getVc().getName().getOutput()) {
							if (temp.getVc().getViz().getMap().containsKey(VizColumn.type) && temp.getVc().getViz().getMap().get(VizColumn.type).equals(VizColumn.scatter)) {
								//Scatter plot case
								VisualComponentList toAdd = ((ScatterVCNode) currNode).getVcList();
								outputList.getVisualComponentList().addAll(toAdd.getVisualComponentList());
							} else {
								VisualComponentList toAdd = (VisualComponentList) (currNode).lookuptable.get(temp.getVc().getName().getName());
								outputList.getVisualComponentList().addAll(toAdd.getVisualComponentList());
							}
						}
						//System.out.println("To output = " + temp.getVc().getName().getOutput());
					}
					//System.out.println(" My map");
					//MapUtils.debugPrint(System.out, "myMap", currNode.lookuptable.getVariables());
					//System.out.println(" My map");
					//System.out.println(Arrays.toString(currNode.lookuptable.getVariables().entrySet().toArray()));

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
		return outputList;
	}
}
