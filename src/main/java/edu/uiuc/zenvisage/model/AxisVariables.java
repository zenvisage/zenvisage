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
  
  public String[][] getXList(){
	System.out.println(x);
	return processList(this.x);
  }
  
  public String[][] getYList(){
	return processList(this.y);
  }
  
  public String[][] getZList(){
    return processList(this.z);
  }
  
  public String[][] processList(String raw){
    String[] raw0 = raw.split(",");
    int len = raw0.length;
    String[][] retList = new String[len][2];
    for(int i = 0; i < len; i++){
	  String[] raw1 = raw0[i].split(" ");
	  retList[i][0] = raw1[0];
	  retList[i][1] = raw1[1];
    }
    return retList;
  }

}
