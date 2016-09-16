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
 * @author eideh
 * Abstraction of the query graph representation of a query
 */
public class QueryGraph extends Graph {

	// A query graph needs O(1) access to any node
	// Here the map is of form key = input var, value = result Node that output this
	Map<String, Node> resultNodes;
	
	
	public QueryGraph () {
		
	}
	

	/**
	 * Processes a ZQLTable row by row and generates a result graph (currently)
	 * @return List of entry points (for this table)
	 */
	public List<Node> processZQLTable(ZQLTable table) {
		
		List<Node> queryEntryNodes = new ArrayList<Node>();
		
		for (ZQLRow row : table.getZqlRows()) {
			XColumn x = row.getX();
			YColumn y = row.getY();
			ZColumn z = row.getZ(); // eg z1 or v1
			Name name = row.getName();
			
			VisualComponent vc = new VisualComponent(row.getName(), x, y, z, row.getConstraint(), row.getViz());
			Node vcNode = new VisualComponentNode(vc, true);
			if (resultNodes.containsKey(x.getVariable())) {
				Node parent = resultNodes.get(x.getVariable());
				parent.addChild(vcNode);
				vcNode.addParent(vcNode);
			}
			else {
				// New entry node! Add as an entry node for this query, and entry node for the entire graph
				queryEntryNodes.add(vcNode);
				entryNodes.add(vcNode);
			}
			Processe process = row.getProcesse();
			ProcessNode processNode = new ProcessNode(process, true);
			boolean hasParent = false;
			for (String argument : process.getArguments()) {
				Node parent = resultNodes.get(argument);
				if (parent != null) {
					hasParent = true;
					parent.addParent(processNode);
					processNode.addParent(parent);					
				}
			}
			if (!hasParent) {
				queryEntryNodes.add(processNode);
				entryNodes.add(processNode);
				// process nodes depend on some parameters, so is this case reachable?
			}
			
			// Update hash map
			resultNodes.put(name.getName(), vcNode);
			resultNodes.put(x.getVariable(), vcNode);
			resultNodes.put(y.getVariable(), vcNode);
			resultNodes.put(z.getVariable(), vcNode);
			for (String variable : process.getVariables()) {
				resultNodes.put(variable, processNode);
			}
		}
		return queryEntryNodes;
		
	}
}
