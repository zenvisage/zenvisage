package edu.uiuc.zenvisage.data.remotedb;

public class Attribute {
	public String name;
	public String type;
	public String axis;
	public Attribute(String name, String type, String axis){
		this.name = name;
		this.type = type;
		this.axis = axis;
	}
	public String toString(){
		return "name:"+name+",type:"+type+",axis:"+axis;
	}
}
