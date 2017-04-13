package edu.uiuc.zenvisage.data.remotedb;

public class WrapperType {
	private Integer intValue = null; 
	private String strValue = null; 
	private Float floatValue  = null; 
	
	public WrapperType(String input) {
		if (input.matches("^(-?)\\d+$")){ //"^\\d+$")){
			this.intValue = Integer.parseInt(input);
		} else if (input.matches("^([+-]?\\d*\\.?\\d*)$")){ //"\\d+(?:\\.\\d+)?")){
			this.floatValue = Float.parseFloat(input);
		} else {
			this.strValue = input;
		}
	}
	
	public WrapperType(String input, String metaType) {
		switch(metaType){
			case "int":
				this.intValue = Integer.parseInt(input); break;
			case "string":
				this.strValue = input; break;
			case "float":
				this.floatValue = Float.parseFloat(input); break;
		}
	}
	
	public WrapperType(int x) {
		this.intValue = x;
	}
	public WrapperType(float x) {
		this.floatValue = x;
	}

	public boolean equals(WrapperType input) {
		
		if(input == null && this.intValue == null && this.strValue == null && this.floatValue == null) {
			return true;
		}
		if(input == null) return false;
		
		if(input.intValue != null && this.intValue != null && input.intValue == this.intValue) return true;
		if(input.floatValue != null && this.floatValue != null && input.floatValue == this.floatValue) return true;
		if(input.strValue != null && this.strValue != null && input.strValue.equals(this.strValue)) return true;
		return false;
	}
	
	public String getStrValue() {
		return strValue;
	}
	
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
	public Integer getIntValue() {
		return this.intValue;
	}
	
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	
	public Float getNumberValue() {
		if(intValue != null){
			return new Float(intValue);
		}

		if(floatValue != null){
			return floatValue;
		}
		return new Float(0.0);
	}
	
	public void setfloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}
	
	public String toString(){
		if(intValue != null){
			return intValue.toString();
		}
		if(strValue != null){
			return strValue.toString();
		}
		if(floatValue != null){
			return floatValue.toString();
		}
		return null;
	}
}
