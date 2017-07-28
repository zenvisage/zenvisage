package edu.uiuc.zenvisage.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ClassElement {

	public String name;
	/**
	 * its [n][2], intervals e.g. [0-100] [101-200]... and such
	 */
	public float[][] values;
	public String tag;
	public String attributes;
	public String ranges;
	public int count;
	
	public ClassElement(){}
	public ClassElement(String name, float[][] values) {
		this.name = name;
		this.values = values;
	}
	
	public ClassElement(String name, float[][] values, String tag, String attributes, String ranges, int count) {
		this.name = name;
		this.values = values;
		this.tag = tag;
		this.attributes = attributes;
		this.ranges = ranges;
		this.count = count;
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
	
	/**
	 * [[0.0, 90.0], [90.0, 25144.643]] to float[][]
	 */
	public static float[][] fromStringToFloatArray(String input){
		input = input.substring(1,input.length()-1).replaceAll(" ","");
		StringBuilder sb = new StringBuilder();
		List<float[]> l = new ArrayList<float[]>();
		for(int i = 0; i < input.length(); i ++ ){
			char cur = input.charAt(i);
			if(cur == ']'){
				String[] sS = sb.toString().split(",");
				l.add(new float[]{Float.parseFloat(sS[0]), Float.parseFloat(sS[1])});
				sb.setLength(0);
				i++;//skip , eg.[min,90],[90,101],[101,max] split as ,[90,101] for next string
			}else{
				if(cur!='['){
					sb.append(cur);
				}
			}
		}
		float[][] ret = new float[l.size()][2];
		for(int i = 0; i < l.size(); i++){
			ret[i] = l.get(i);
		}
		return ret;
	}
}
