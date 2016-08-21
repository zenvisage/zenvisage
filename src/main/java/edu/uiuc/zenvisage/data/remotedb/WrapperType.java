package edu.uiuc.zenvisage.data.remotedb;

public class WrapperType {
	private Integer intValue = null; 
	private String strValue = null; 
	private Double doubleValue  = null; 
	public WrapperType(String s){
		this.setStrValue(s);
	} 
	public WrapperType(Integer i){
		this.setIntValue(i);
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	public Integer getIntValue() {
		return intValue;
	}
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
}
