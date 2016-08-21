package edu.uiuc.zenvisage.data.remotedb;		
import java.util.ArrayList;;		
public class VisualGroups {		
	private ArrayList<WrapperType> zValues;		
	private Points points;	
	public VisualGroups(){};
	public ArrayList<WrapperType> getzValues() {		
		return zValues;		
	}		
	public void setzValues(ArrayList<WrapperType> zValues) {		
		this.zValues = zValues;		
	}		
	public Points getVisualGroups() {		
		return points;		
	}		
	public void setVisualGroups(Points points) {		
		this.points = points;
	}
	public String toString(){
		String ret = "";
	    for(WrapperType z: zValues){
	    	ret += z.toString() + " ";
	    }
	    ret += "\n";
	    for(WrapperType x: points.getX()){
	    	ret += x.toString() + " ";
	    }
	    ret += "\n";
	    for(WrapperType y: points.getY()){
	    	ret += y.toString() + " ";
	    }
	    ret += "\n";

		return ret;
	}
}