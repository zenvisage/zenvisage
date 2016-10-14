package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
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
	Map<String, Node> nodeMap = new HashMap<String, Node>();
	
	
	/**
	 * Processes a ZQLTable row by row and generates a plan graph
	 * @return QueryGraph for this table
	 */
	public QueryGraph processZQLTable(ZQLTable table) {
		
		List<Node> queryEntryNodes = new ArrayList<Node>();
		QueryGraph graph = new QueryGraph();
		LookUpTable lookuptable = new LookUpTable();
		
		for (ZQLRow row : table.getZqlRows()) {
			XColumn x = row.getX();
			YColumn y = row.getY();
			ZColumn z = row.getZ(); // eg z1 or v1
			Name name = row.getName();
			
			VisualComponentQuery vc = new VisualComponentQuery(row.getName(), x, y, z, row.getConstraint(), row.getViz());
			SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
			Node vcNode = new VisualComponentNode(vc, lookuptable, sqlQueryExecutor);
			Processe process = row.getProcesse();
			ProcessNode processNode = new ProcessNode(process, lookuptable);

			// Robustness. Update HashMap only if new assignment to existing variable occurs
			// Update hash map
			boolean updatedX = false;
			boolean updatedY = false;
			boolean updatedZ = false;
			System.out.println(name);
			System.out.println(name.getName());
			nodeMap.put(name.getName(), vcNode); // if they reuse a name, assume future rows refer to the latest reused name node
			if (x.getValues() != null && !x.getValues().isEmpty()) {
				nodeMap.put(x.getVariable(), vcNode);
				updatedX = true;
			}
			if (y.getValues() != null && !y.getValues().isEmpty()) {
				nodeMap.put(y.getVariable(), vcNode);
				updatedY = true;
			}
			if (z.getValues() != null && !z.getValues().isEmpty()) {
				nodeMap.put(z.getVariable(), vcNode);
				updatedZ = true;
			}
			if (process != null) {
				for (String variable : process.getVariables()) {
					nodeMap.put(variable, processNode); // if they reuse a process variable, assume future rows refer to the latest reused variable name node
				}
			}
			// So if any x,y,or z variable is referenced from above, link that as a parent
			// make sure that if we have updated a variable with a new assignment, make sure to NOT have a parent (since this node has the latest value)
			if (!updatedX && nodeMap.containsKey(x.getVariable())) {
				Node parent = nodeMap.get(x.getVariable());
				parent.addChild(vcNode);
				vcNode.addParent(vcNode);
			} else if (!updatedY && nodeMap.containsKey(y.getVariable())) {
				Node parent = nodeMap.get(y.getVariable());
				parent.addChild(vcNode);
				vcNode.addParent(vcNode);
			} else if (!updatedZ && nodeMap.containsKey(z.getVariable())) {
				Node parent = nodeMap.get(z.getVariable());
				parent.addChild(vcNode);
				vcNode.addParent(vcNode);
			} else {
				// New entry node! Add as an entry node for this query, and entry node for the entire graph
				queryEntryNodes.add(vcNode);
				graph.entryNodes.add(vcNode);
			}
			
			boolean hasParent = false;
			if (process != null) {
				for (String argument : process.getArguments()) {
					Node parent = nodeMap.get(argument);
					if (parent != null) {
						hasParent = true;
						parent.addChild(processNode);
						processNode.addParent(parent);					
					}
				}
			}
			if (!hasParent && process != null) {
				queryEntryNodes.add(processNode);
				graph.entryNodes.add(processNode);
				// process nodes depend on some parameters, so is this case reachable?
			}
			

		}
		return graph;
		
	}
}
