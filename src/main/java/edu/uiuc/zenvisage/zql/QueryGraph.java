/**
 * 
 */
package edu.uiuc.zenvisage.zql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
	
	public void printString() {
		StringBuilder result = new StringBuilder();
		
		result.append("Graph view: \n");
		for (Node node : entryNodes) {
			Stack<Node> stack = new Stack<Node>();
			stack.push(node);
			while(!stack.isEmpty()) {
				Node currNode = stack.pop();
				if (currNode instanceof VisualComponentNode) {
					result.append("--node is: " + ((VisualComponentNode) currNode).getVc().getName().getName() + "\n");
					result.append("----parents are: \n");
					for (Node parent: currNode.getParents()) {
						if (parent instanceof VisualComponentNode) {
							result.append("----VC: " + ((VisualComponentNode)parent).getVc().getName().getName() + "\n");
						}
						else if (parent instanceof ProcessNode){
							result.append("----P: " + ((ProcessNode)parent).getProcess().getVariables() + "\n");
						}
					}
					result.append("----children are: \n");
					for (Node child: currNode.getChildren()) {
						stack.push(child);
						if (child instanceof VisualComponentNode) {
							result.append("----VC: " + ((VisualComponentNode)child).getVc().getName().getName() + "\n");
						}
						else if (child instanceof ProcessNode){
							result.append("----P: " + ((ProcessNode)child).getProcess().getVariables() + "\n");
						}
					}				
				}
				else if (currNode instanceof ProcessNode) {
					if (((ProcessNode) currNode).getProcess() != null) {
					result.append("--node is: " + ((ProcessNode) currNode).getProcess().getVariables() + "\n");
					}
					else {
						result.append("--node is null process");
					}
					result.append("----parents are: \n");
					for (Node parent: currNode.getParents()) {
						if (parent instanceof VisualComponentNode) {
							result.append("----VC: " + ((VisualComponentNode)parent).getVc().getName().getName() + "\n");
						}
						else if (parent instanceof ProcessNode){
							result.append("----P: " + ((ProcessNode)parent).getProcess().getVariables() + "\n");
						}
					}
					result.append("----children are: \n");
					for (Node child: currNode.getChildren()) {
						stack.push(child);
						if (child instanceof VisualComponentNode) {
							result.append("----VC: " + ((VisualComponentNode)child).getVc().getName().getName() + "\n");
						}
						else if (child instanceof ProcessNode){
							result.append("----P: " + ((ProcessNode)child).getProcess().getVariables() + "\n");
						}
					}				
				}
				System.out.println(result.toString());
				result.setLength(0); // "clears" our stringbuilder
			}
		}
	}
}
