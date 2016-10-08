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
		// so we have an entryList which is an ArrayList<Node>
		// the nodes we put in the list are of type QueryNode
	}
	


	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("Graph view: \n");
		for (Node node : entryNodes) {
			if (node instanceof VisualComponentNode) {
				result.append("--node is: " + ((VisualComponentNode) node).getVc().getName() + "\n");
				result.append("----parents are: \n");
				for (Node parent: node.getParents()) {
					if (node instanceof VisualComponentNode) {
						result.append("----VC: " + ((VisualComponentNode)node).getVc().getName() + "\n");
					}
					else if (node instanceof ProcessNode){
						result.append("----P: " + ((ProcessNode)node).getProcess().getVariables() + "\n");
					}
				}
				for (Node child: node.getChildren()) {
					if (node instanceof VisualComponentNode) {
						result.append("----VC: " + ((VisualComponentNode)node).getVc().getName() + "\n");
					}
					else if (node instanceof ProcessNode){
						result.append("----P: " + ((ProcessNode)node).getProcess().getVariables() + "\n");
					}
				}				
			}
			else if (node instanceof ProcessNode) {
				if (((ProcessNode) node).getProcess() != null) {
				result.append("--node is: " + ((ProcessNode) node).getProcess().getVariables() + "\n");
				}
				else {
					result.append("--node is null process");
				}
				result.append("----parents are: \n");
				for (Node parent: node.getParents()) {
					if (node instanceof VisualComponentNode) {
						result.append("----VC: " + ((VisualComponentNode)node).getVc().getName() + "\n");
					}
					else if (node instanceof ProcessNode){
						result.append("----P: " + ((ProcessNode)node).getProcess().getVariables() + "\n");
					}
				}
				for (Node child: node.getChildren()) {
					if (node instanceof VisualComponentNode) {
						result.append("----VC: " + ((VisualComponentNode)node).getVc().getName() + "\n");
					}
					else if (node instanceof ProcessNode){
						result.append("----P: " + ((ProcessNode)node).getProcess().getVariables() + "\n");
					}
				}				
			}
		}
		return result.toString();
	}
}
