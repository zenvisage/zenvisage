package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
	
	public LinkedHashMap<String, LinkedHashMap<Float, Float>> toInMemoryHashmap(){
		LinkedHashMap<String, LinkedHashMap<Float, Float>> output = new LinkedHashMap<String, LinkedHashMap<Float, Float>>();
		for(VisualComponent i: visualComponentList){
			List<WrapperType> xList = i.getPoints().getXList();
			List<WrapperType> yList = i.getPoints().getYList();
			LinkedHashMap<Float, Float> map = new LinkedHashMap<Float, Float>();
			for(int j = 0; j < xList.size(); j++) {
				map.put(new Float(xList.get(j).getIntValue()), new Float(yList.get(j).getfloatValue()));
			}
			output.put(new String(i.getZValue().toString()), map);
		}
		return output;
	}
	
	public String toString(){
		StringBuilder ret = new StringBuilder();
		for(VisualComponent i : this.visualComponentList){
			ret.append(i.toString());
		}
		return ret.toString();
	}
}
