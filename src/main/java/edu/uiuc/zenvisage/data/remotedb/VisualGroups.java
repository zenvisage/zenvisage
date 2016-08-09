package edu.uiuc.zenvisage.data.remotedb;		
import java.util.ArrayList;;		
public class VisualGroups {		
	private ArrayList<String> zValues;		
	private ArrayList<VisualGroup> visualGroups;		
	public ArrayList<String> getzValues() {		
		return zValues;		
	}		
	public void setzValues(ArrayList<String> zValues) {		
		this.zValues = zValues;		
	}		
	public ArrayList<VisualGroup> getVisualGroups() {		
		return visualGroups;		
	}		
	public void setVisualGroups(ArrayList<VisualGroup> visualGroups) {		
		this.visualGroups = visualGroups;		
	}		
}