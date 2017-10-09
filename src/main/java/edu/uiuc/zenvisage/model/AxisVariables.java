package edu.uiuc.zenvisage.model;

public class AxisVariables {
  private String datasetName;
  private String x;
  private String y;
  private String z;
  
  public String getX() {
	return x;
  }
  public void setX(String x) {
	this.x = x;
  }
  public String getY() {
	return y;
  }
  public void setY(String y) {
	this.y = y;
  }
  public String getZ() {
	return z;
  }
  public void setZ(String z) {
	this.z = z;
  }
  public String getDatasetName() {
    return datasetName;
  }
  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }
  public String toString(){
    return "datasetName:"+datasetName+";x:"+x+";y:"+y+";z:"+z;
  }
}
