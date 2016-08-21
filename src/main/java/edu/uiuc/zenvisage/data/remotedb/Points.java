package edu.uiuc.zenvisage.data.remotedb;		
 		
import java.util.ArrayList;		
 		
public class Points {		
	private ArrayList <WrapperType> X;		
	private ArrayList <WrapperType> Y;
	public Points (ArrayList <WrapperType> X, ArrayList <WrapperType> Y){
		this.X = X; this.Y = Y;
	}
	public ArrayList <WrapperType> getX() {		
		return X;		
	}		
	public void setX(ArrayList <WrapperType> x) {		
		X = x;		
	}		
	public ArrayList <WrapperType> getY() {		
 		return Y;		
 	}		
 	public void setY(ArrayList <WrapperType> y) {		
 		Y = y;		
 	}		
 }