package edu.uiuc.zenvisage.model;

public class ZvQuery {
	public String method;
	public String xAxis;
	public String yAxis;
	public String databasename;
	public String groupBy;
	public String aggrFunc;
	public String aggrVar;
	public int outlierCount;
	public float[] dataX;
	public float[] dataY;
	public double yMax;
	public double yMin;
	public String error;
	public Sketch[] sketchPoints;
	public double minX;
	public double maxX;
	public boolean outputNormalized = false;
	public String distanceNormalized="linear";
	public String clustering="DBSCAN";
	public String distance_metric="Euclidean";
	public String predicateColumn;
	public String predicateOperator;
	public String predicateValue;
	public String filter;
	public float[] xRange;
	public boolean considerRange;
	public int kmeansClusterSize;
	public String smoothingType="";
	public double smoothingcoefficient=0.0;
	public boolean download=false;
	public boolean includeQuery=false;
	public boolean yOnly=false;
	public boolean downloadAll=false;
	public double downloadThresh= 0.0;
	public double minDisplayThresh=0.0;
	
	public double getMinDisplayThresh() {
		return minDisplayThresh;
	}
	public void setMinDisplayThresh(double minDisplayThresh) {
		this.minDisplayThresh = minDisplayThresh;
	}
	public String getDistance_metric() {
		return distance_metric;
	}
	public void setDistance_metric(String distance_metric) {
		this.distance_metric = distance_metric;
	}

	public String getClustering() {
		return clustering;
	}
	public void setClustering(String clustering) {
		this.clustering = clustering;
	}
	public boolean isOutputNormalized() {
		return outputNormalized;
	}
	public void setOutputNormalized(boolean outputNormalized) {
		this.outputNormalized = outputNormalized;
	}
	
	public String getDistanceNormalized() {
		return distanceNormalized;
	}
	
	public void setDistanceNormalized(String distanceNormalized) {
		this.distanceNormalized = distanceNormalized;
	}
	
	public void setYaxisAsError() {
		this.yAxis = this.error;
		this.aggrVar = this.error;
	}
	// constructor for args
	public ZvQuery (String method, String Yaxis, String Xaxis, String groupBy, String aggrFunc, String aggrVar, int outlierCount, boolean outputNormalized) {
		this.method = method;
		this.yAxis = Yaxis;
		this.xAxis = Xaxis;
		this.groupBy = groupBy;
		this.aggrFunc = aggrFunc;
		this.aggrVar = aggrVar;
		this.outlierCount = outlierCount;
		this.outputNormalized = outputNormalized;
	}
	// dummy constructor
	public ZvQuery () {
		
	}
	
	public Sketch[] getSketchPoints() {
		return sketchPoints;
	}
	public void setSketchPoints(Sketch[] sketchPoints) {
		this.sketchPoints = sketchPoints;
	}

	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getYaxis() {
		return yAxis;
	}
	public void setYaxis(String yaxis) {
		yAxis = yaxis;
	}
	public String getXaxis() {
		return xAxis;
	}
	public void setXaxis(String xaxis) {
		xAxis = xaxis;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getAggrFunc() {
		return aggrFunc;
	}
	public void setAggrFunc(String aggrFunc) {
		this.aggrFunc = aggrFunc;
	}
	public String getAggrVar() {
		return aggrVar;
	}
	public void setAggrVar(String aggrVar) {
		this.aggrVar = aggrVar;
	}
	public String getOutlierCount() {
		return Integer.toString(outlierCount);
	}
	public void setOutlierCount(int outlierCount) {
		this.outlierCount = outlierCount;
	}
	public float[] getDataX() {
		return dataX;
	}
	public void setDataX(float[] dataX) {
		this.dataX = dataX;
	}
	public float[] getDataY() {
		return dataY;
	}
	public void setDataY(float[] dataY) {
		this.dataY = dataY;
	}
	public int getkmeansClusterSize() {
		return kmeansClusterSize;
	}
	public void setkmeansClusterSize(int k) {
		this.kmeansClusterSize = k;
	}
	public String getSmoothingType() {
		return smoothingType;
	}
	public void setSmoothingType(String smoothingType) {
		this.smoothingType = smoothingType;
	}
	public double getSmoothingcoefficient() {
		return smoothingcoefficient;
	}
	public void setSmoothingcoefficient(double smoothingcoefficient) {
		this.smoothingcoefficient = smoothingcoefficient;
	}

	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}

	public boolean getDownload() {
		return download;
	}
	public boolean getDownloadAll() {
		return downloadAll;
	}
	public boolean getIncludeQuery() {
		return includeQuery;
	}
	public boolean getyOnly() {
		return yOnly;
	}	
	public boolean deriveDownloadX(){
		boolean downloadX = true;
		if (yOnly){
			 System.out.println("download Y only");
			 downloadX = false;
		}
		return downloadX;
	}
	public double getDownloadThresh(){
		return downloadThresh;
	}
	public String getdatabasename(){
		return databasename;
	}
}
