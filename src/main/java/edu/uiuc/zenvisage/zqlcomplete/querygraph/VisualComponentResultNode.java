/**
 * 
 */
package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;

/**
 * @author Edward Xue
 * Stores result of a VC node
 */
public class VisualComponentResultNode extends Node{
	private VisualComponentList vcList;

	public VisualComponentResultNode() {
		this.vcList = new VisualComponentList();
	}

	public VisualComponentList getVcList() {
		return vcList;
	}

	public void setVcList(VisualComponentList vcList) {
		this.vcList = vcList;
	}
	
	public boolean isProcessed() {
		if (vcList == null) {
			return false;
		}
		// Our node is process if we have results in our vcList object
		return !vcList.getVisualComponentList().isEmpty();
	}
	
}
