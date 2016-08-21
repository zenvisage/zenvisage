package edu.uiuc.zenvisage.data.remotedb;

public class WrapperType {
	private Integer intValue = null; 
	private String strValue = null; 
	private Double doubleValue  = null; 
	
	
	public WrapperType(String input){
		if (input.matches("^\\d+$")){
			this.intValue = Integer.parseInt(input);
		} else if (input.matches("[+-]([0-9]*[.])?[0-9]+")){
			this.doubleValue = Double.parseDouble(input);
		} else {
			this.strValue = input;
		}
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
	public String toString(){
		if(intValue != null){
			return intValue.toString();
		}
		if(strValue != null){
			return strValue.toString();
		}
		if(doubleValue != null){
			return doubleValue.toString();
		}
		return null;
	}
}
