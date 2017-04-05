package edu.uiuc.zenvisage.model;

import java.util.Arrays;

public class ClassElement {

	public String name;
	/**
	 * its [n][2], intervals e.g. [0-100] [101-200]... and such
	 */
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
	/**
	 * 0 means not found, 1 means found index 0 of values[][] as the interval etc.
	 */
	public int getInterval(float input){
		for(int i = 0; i < values.length; i++){
			Arrays.sort(values[i]);
			if(input >= values[i][0] && input < values[i][1]) return i+1;
		}
		return 0;
	}
	
	public String getSQL(int i){
		Arrays.sort(values[i]);
		return this.name + " >= " + values[i][0] + " AND " 
				+ this.name + " <= " + values[i][1];
	}
}
