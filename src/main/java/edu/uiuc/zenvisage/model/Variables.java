package edu.uiuc.zenvisage.model;

import java.util.List;

public class Variables {
	private String datasetName;
	private List<Variable> variables;

	public List<Variable> getVariables() {
		return variables;
	}

	public void setList(List<Variable> variables) {
		this.variables = variables;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
}
