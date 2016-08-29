package edu.uiuc.zenvisage.data.remotedb;

public class VisualComponent {		
	
	private WrapperType zValue;		
	private Points points;
	
	/*Empty Constructor*/
	public VisualComponent(){};
	
	public WrapperType getZValue() {		
		return zValue;		
	}
	
	public void setZValues(WrapperType zValues) {		
		this.zValue = zValues;		
	}
	
	public Points getVisualGroups() {		
		return points;		
	}
	
	public void setVisualGroups(Points points) {		
		this.points = points;
	}
	
	public String toString(){
		String ret = "";

	    ret += zValue.toString() + " ";  
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