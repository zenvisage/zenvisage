package edu.uiuc.zenvisage.data.remotedb;

import java.util.HashMap;
import java.util.Map;

public class DatabaseMetaData {
	
	 public Map<String,ColumnMetadata> xAxisColumns= new HashMap<String,ColumnMetadata>();
	 public Map<String,ColumnMetadata> yAxisColumns= new HashMap<String,ColumnMetadata>();
	 public Map<String,ColumnMetadata> zAxisColumns= new HashMap<String,ColumnMetadata>();
	 public Map<String,ColumnMetadata> predicateColumns= new HashMap<String,ColumnMetadata>();
	 public String dataset;
	 
	 public DatabaseMetaData() {
		
	}
	public Map<String, ColumnMetadata> getxAxisColumns() {
		return xAxisColumns;
	}
	public void setxAxisColumns(Map<String, ColumnMetadata> xAxisColumns) {
		this.xAxisColumns = xAxisColumns;
	}
	public Map<String, ColumnMetadata> getyAxisColumns() {
		return yAxisColumns;
	}
	public void setyAxisColumns(Map<String, ColumnMetadata> yAxisColumns) {
		this.yAxisColumns = yAxisColumns;
	}
	public Map<String, ColumnMetadata> getzAxisColumns() {
		return zAxisColumns;
	}
	public void setzAxisColumns(Map<String, ColumnMetadata> zAxisColumns) {
		this.zAxisColumns = zAxisColumns;
	}
	public Map<String, ColumnMetadata> getPredicateColumns() {
		return predicateColumns;
	}
	public void setPredicateColumns(Map<String, ColumnMetadata> predicateColumns) {
		this.predicateColumns = predicateColumns;
	}

}
