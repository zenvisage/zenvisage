package edu.uiuc.zenvisage.data.remotedb;		
 		
import java.util.ArrayList;		
 		
public class VisualGroup {		
	private ArrayList <?> X;		
	private ArrayList <?> Y;		
	public ArrayList <?> getX() {		
		return X;		
	}		
	public void setX(ArrayList <?> x) {		
		X = x;		
	}		
	public ArrayList <?> getY() {		
 		return Y;		
 	}		
 	public void setY(ArrayList <?> y) {		
 		Y = y;		
 	}		
 	public String getXType(){		
 		for (Object o : X) {		
 		    return o.getClass().toString();		
 		}		
 		return null;		
 	}		
 	public String getYType(){		
 		for (Object o : Y) {		
 		    return o.getClass().toString();		
 		}		
 		return null;		
 	}		
 }