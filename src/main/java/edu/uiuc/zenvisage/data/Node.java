package edu.uiuc.zenvisage.data;

import java.util.HashMap;
import java.util.Map;


public class Node {
	public boolean isleaf=false;
	public String type;
	public float value;
	public Map<String,Node> chilNodes=new HashMap<String,Node>();
	public double aggregate;
	
}
