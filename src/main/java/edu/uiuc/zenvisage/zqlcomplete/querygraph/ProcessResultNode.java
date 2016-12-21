package edu.uiuc.zenvisage.zqlcomplete.querygraph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward Xue
 * Stores result of a Process node
 */
public class ProcessResultNode extends Node {
	
	// Processes return a subset of values from a column (X,Y, or Z)
	List<String> columnSelection;
	
	public ProcessResultNode() {
		columnSelection = new ArrayList<String>();
	}

	public List<String> getColumnSelection() {
		return columnSelection;
	}

	public void setColumnSelection(List<String> columnSelection) {
		this.columnSelection = columnSelection;
	}
	
	public boolean isProcessed() {
		if (columnSelection == null) {
			return false;
		}
		// Our node is process if we have results in our vcList object
		return !columnSelection.isEmpty();
	}}
