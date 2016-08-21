package edu.uiuc.zenvisage.data.remotedb;		
 		
import java.util.ArrayList;		
 		
public class Points {		
	private ArrayList <WrapperType> xList;		
	private ArrayList <WrapperType> yList;
	public Points (ArrayList <WrapperType> xList, ArrayList <WrapperType> yList){
		this.xList = xList; this.yList = yList;
	}
	public ArrayList <WrapperType> getX() {		
		return xList;		
	}		
	public void setX(ArrayList <WrapperType> x) {		
		this.xList = x;		
	}		
	public ArrayList <WrapperType> getY() {		
 		return yList;		
 	}		
 	public void setY(ArrayList <WrapperType> y) {		
 		this.yList = y;		
 	}		
 }