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
 * Takes in a ZQLTable, and parses it into a query graph
 */
public class ZQLParser {
	
	// A query graph needs O(1) access to any node
	// Here the map is of form key = input var, value = result Node that output this
	Map<String, Node> resultNodes;
	
	
	/**
	 * Processes a ZQLTable row by row and generates a plan graph
	 * @return QueryGraph for this table
	 */
	public QueryGraph processZQLTable(ZQLTable table) {
		
		List<Node> queryEntryNodes = new ArrayList<Node>();
		QueryGraph graph = new QueryGraph();
		
		for (ZQLRow row : table.getZqlRows()) {
			XColumn x = row.getX();
			YColumn y = row.getY();
			ZColumn z = row.getZ(); // eg z1 or v1
			Name name = row.getName();
			
			VisualComponentQuery vc = new VisualComponentQuery(row.getName(), x, y, z, row.getConstraint(), row.getViz());
			Node vcNode = new VisualComponentNode(vc, true);
			
			// TODO: Robustness. Update hashmap only if new assignment to exisitng variable occurs
			if (resultNodes.containsKey(x.getVariable())) {
				Node parent = resultNodes.get(x.getVariable());
				parent.addChild(vcNode);
				vcNode.addParent(vcNode);
			}
			else {
				// New entry node! Add as an entry node for this query, and entry node for the entire graph
				queryEntryNodes.add(vcNode);
				graph.entryNodes.add(vcNode);
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
				graph.entryNodes.add(processNode);
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
		return null;
		
	}
}
