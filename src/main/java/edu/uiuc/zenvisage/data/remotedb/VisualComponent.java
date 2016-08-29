package edu.uiuc.zenvisage.data.remotedb;

public class VisualComponent {		
	
	private WrapperType zValue;		
	private Points points;
	
	/*Empty Constructor*/
	public VisualComponent(WrapperType zValue, Points points){
		this.zValue = zValue;
		this.points = points;
	};
	
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
		
		StringBuilder ret = new StringBuilder();
	    ret.append(zValue.toString()+"/n");  
	    
	    for(WrapperType x: points.getX())
	    	ret.append(x.toString() + " ");
	    ret.append("/n");
	    
	    for(WrapperType y: points.getY())
	    	ret.append(y.toString() + " ");
	    ret.append("/n");
	    
		return ret.toString();
		
	}
		
}