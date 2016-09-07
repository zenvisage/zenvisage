package edu.uiuc.zenvisage.data.remotedb;		
 		
import java.util.ArrayList;		
 		
public class Points {		
	private ArrayList <WrapperType> xList;		
	private ArrayList <WrapperType> yList;
	public Points (ArrayList <WrapperType> xList, ArrayList <WrapperType> yList){
		this.xList = xList; this.yList = yList;
	}
	public ArrayList <WrapperType> getXList() {		
		return xList;		
	}		
	public void setXList(ArrayList <WrapperType> x) {		
		this.xList = x;		
	}		
	public ArrayList <WrapperType> getYList() {		
 		return yList;		
 	}		
 	public void setYList(ArrayList <WrapperType> y) {		
 		this.yList = y;		
 	}		
 }