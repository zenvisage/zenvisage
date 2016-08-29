package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;

public class VisualComponentList {
	
	private ArrayList<VisualComponent> visualComponentList;

	public VisualComponentList(){}
	
	public ArrayList<VisualComponent> getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(ArrayList<VisualComponent> visualComponentList) {
		this.visualComponentList = visualComponentList;
	}
	
	public void addVisualComponent(VisualComponent input){
		this.visualComponentList.add(input);
	}
	
	public String toString(){
		StringBuilder ret = new StringBuilder();
		for(VisualComponent i : this.visualComponentList){
			ret.append(i.toString());
		}
		return ret.toString();
	}
}
