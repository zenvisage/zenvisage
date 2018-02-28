/**
 * 
 */
package edu.uiuc.zenvisage.zql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward Xue
 * Abstraction of Nodes in a graph; just containing the links to other nodes
 * Our specialized purpose of a query graph means our nodes need certain properties
 * An easy way to see who the parents are (a dependency), and an easy way to get to the children (next step)
 */
public class Node {
	
	public int numbering = -1; 
	private List<Node> children;
	private List<Node> parents;
	
	public Node() {
		children = new ArrayList<Node>();
		parents = new ArrayList<Node>();
	}
	
	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		// TODO: currently not a deep copy!
		this.children = children;
	}
	
	public void addChild(Node child) {
		this.children.add(child);
	}
	
	public void removeChild(Node child) {
		this.children.remove(child);
	}
	
	public List<Node> getParents() {
		return parents;
	}
	
	public void setParents(List<Node> parents) {
		// TODO: not a deep copy!
		this.parents = parents;
	}
	
	public void addParent(Node parent) {
		this.parents.add(parent);
	}
	
	public void removeParent(Node parent) {
		this.parents.remove(parent);
	}
	
	
}
