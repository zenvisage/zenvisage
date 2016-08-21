package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;
import java.util.List;

public class UnIndexedColumnValues {
	private ArrayList<String> columnValues= new ArrayList<String>();

	public List<String> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(ArrayList<String> columnValues) {
		this.columnValues = columnValues;
	}

}
