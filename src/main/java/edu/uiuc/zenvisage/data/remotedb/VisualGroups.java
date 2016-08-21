package edu.uiuc.zenvisage.data.remotedb;		
import java.util.ArrayList;;		
public class VisualGroups {		
	private ArrayList<?> zValues;		
	private Points points;		
	public ArrayList<?> getzValues() {		
		return zValues;		
	}		
	public void setzValues(ArrayList<?> zValues) {		
		this.zValues = zValues;		
	}		
	public Points getVisualGroups() {		
		return points;		
	}		
	public void setVisualGroups(Points points) {		
		this.points = points;
	}		
}