package edu.uiuc.zenvisage.model;
import java.util.ArrayList;

public class DynamicClass {
	
	public ClassElement[] classes;

	public DynamicClass() {
	}
	
	public ClassElement[] getClassElement(){
		return classes;
	}
	
	public void setClassElement(ClassElement[] classes){
		this.classes = classes;
	}
	
}
