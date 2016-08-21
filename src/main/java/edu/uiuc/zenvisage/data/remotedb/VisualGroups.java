package edu.uiuc.zenvisage.data.remotedb;		
import java.util.ArrayList;;		
public class VisualGroups {		
	private ArrayList<WrapperType> zValues;		
	private Points points;		
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
}