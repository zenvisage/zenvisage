package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VisualComponentList {

	public ArrayList<VisualComponent> visualComponentList;
    public HashMap<String,VisualComponent> ZToVisualComponents = new HashMap<>();
	
	
	public VisualComponentList(){}
	
	public ArrayList<VisualComponent> getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(ArrayList<VisualComponent> visualComponentList) {
		this.visualComponentList = visualComponentList;
	}
	
	public void addVisualComponent(VisualComponent input){
		if (this.visualComponentList == null) {
			this.visualComponentList = new ArrayList<VisualComponent>();
		}
		this.visualComponentList.add(input);
	}
	
	public synchronized LinkedHashMap<String, LinkedHashMap<Float, Float>> toInMemoryHashmap(){
		LinkedHashMap<String, LinkedHashMap<Float, Float>> output = new LinkedHashMap<String, LinkedHashMap<Float, Float>>();
		for(VisualComponent i: visualComponentList){
			List<WrapperType> xList = i.getPoints().getXList();
			if (xList.size() < 2) {
				continue; // don't add VisualComponents that are too small
			}
			List<WrapperType> yList = i.getPoints().getYList();
			LinkedHashMap<Float, Float> map = new LinkedHashMap<Float, Float>();
			for(int j = 0; j < xList.size(); j++) {
				map.put(new Float(xList.get(j).getNumberValue()), new Float(yList.get(j).getNumberValue()));
			}
			//System.out.println("zValue:"+i.getZValue());
//			if(i.getZValue()==null || i.getZValue().toString() == null ) continue;
			String key = new String(i.getZValue().toString());
			output.put(key, map);
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
