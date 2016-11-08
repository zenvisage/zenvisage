package edu.uiuc.zenvisage.data.remotedb;

public class VisualComponent {		
	
	private WrapperType zValue; // get the string, that is chart ztype
	private Points points;
	private String xAttribute;
	private String yAttribute;
	
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
	
	public Points getPoints() {		
		return points;		
	}
	
	public void setPoints(Points points) {		
		this.points = points;
	}
	
	public String toString(){
		
		StringBuilder ret = new StringBuilder();
	    ret.append(zValue.toString()+"\n");  
	    
	    for(WrapperType x: points.getXList())
	    	ret.append(x.toString() + " ");
	    ret.append("\n");
	    
	    for(WrapperType y: points.getYList())
	    	ret.append(y.toString() + " ");
	    ret.append("\n");
	    
		return ret.toString();
		
	}

	public String getxAttribute() {
		return xAttribute;
	}

	public void setxAttribute(String xAttribute) {
		this.xAttribute = xAttribute;
	}

	public String getyAttribute() {
		return yAttribute;
	}

	public void setyAttribute(String yAttribute) {
		this.yAttribute = yAttribute;
	}
		
}