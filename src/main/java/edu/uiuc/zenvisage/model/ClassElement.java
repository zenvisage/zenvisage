package edu.uiuc.zenvisage.model;

public class ClassElement {

	public String name;
	public float[][] values;

	public ClassElement() {
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public float[][] getValues(){
		return this.values;
	}
	
	public void setFloat(float[][] values){
		this.values = values;
	}
}
