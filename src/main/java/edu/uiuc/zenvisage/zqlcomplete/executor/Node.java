/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward Xue
 * Abstraction of Nodes in a graph; just containing the links to other nodes
 */
public class Node {
	
	private List<Node> neighbors;
	
	public Node() {
		neighbors = new ArrayList<Node>();
	}
	
	public List<Node> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Node> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(Node neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public void removeNeighbor(Node neighbor) {
		this.neighbors.remove(neighbor);
	}
	
}
