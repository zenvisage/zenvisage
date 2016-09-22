/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uiuc.zenvisage.zqlcomplete.executor.Name;
import edu.uiuc.zenvisage.zqlcomplete.executor.Processe;
import edu.uiuc.zenvisage.zqlcomplete.executor.XColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.YColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZColumn;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable;

/**
 * @author Edward Xue
 * Abstraction of the query graph representation of a query
 * 
 * Nodes are of type QueryNode
 */
public class QueryGraph extends Graph {
		
	public QueryGraph () {
		
	}
	


	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("Graph view: \n");
		for (Node node : entryNodes) {
			if (node instanceof VisualComponentNode) {
				result.append("node is: " + ((VisualComponentNode) node).getVc().getName());
				for (Node parent: node.getParents()) {
					
				}
			}
		}
		return super.toString();
	}
}
