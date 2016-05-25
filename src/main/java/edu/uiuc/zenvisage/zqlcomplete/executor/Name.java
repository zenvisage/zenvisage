package edu.uiuc.zenvisage.zqlcomplete.executor;

public class Name {
	
	private String name;
	private Boolean output;
	private Boolean sketch;
	
	public Name() {
		name = "";
		output = false;
		sketch = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String source) {
		name = source;  
	}
	
	public void setOutput(Boolean source) {
		output = source;
	}
	
	public Boolean getOutput() {
		return output;
	}
	
	public void setSketch(Boolean source) {
		sketch = source;
	}
	
	public Boolean getSketch() {
		return sketch;
	}
	
	
}
