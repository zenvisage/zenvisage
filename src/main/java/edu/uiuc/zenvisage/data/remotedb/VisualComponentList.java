package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class VisualComponentList {

	private String ztype;
	private String xType;
    private String ytype;
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
				map.put(xList.get(j).getNumberValue(), yList.get(j).getNumberValue());
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
	
	/**
	 * @return the ztype
	 */
	public String getZtype() {
		return ztype;
	}

	/**
	 * @param ztype the ztype to set
	 */
	public void setZtype(String ztype) {
		this.ztype = ztype;
	}

	/**
	 * @return the xType
	 */
	public String getxType() {
		return xType;
	}

	/**
	 * @param xType the xType to set
	 */
	public void setxType(String xType) {
		this.xType = xType;
	}

	/**
	 * @return the ytype
	 */
	public String getYtype() {
		return ytype;
	}

	/**
	 * @param ytype the ytype to set
	 */
	public void setYtype(String ytype) {
		this.ytype = ytype;
	}

}
