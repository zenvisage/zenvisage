package edu.uiuc.zenvisage.model;
import java.util.HashMap;
import java.util.Map;

public class DynamicClass {
	
	public String dataset;
	public ClassElement[] classes;

	public DynamicClass() {
	}
	
	public ClassElement[] getClassElement(){
		return classes;
	}
	
	public void setClassElement(ClassElement[] classes){
		this.classes = classes;
	}
	
	public Map<String, ClassElement> getDCHashMap(){
		Map<String, ClassElement> map = new HashMap<>();
		for(ClassElement ce: classes){
			map.put(ce.name, ce);
		}
		return map;
	}
}
