package edu.uiuc.zenvisage.model;

public class Variable {
	private String name;
	private String type;
	private boolean selectedX;
	private boolean selectedY;
	private boolean selectedZ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelectedX() {
		return selectedX;
	}
	public void setSelectedX(boolean selectedX) {
		this.selectedX = selectedX;
	}
	public boolean isSelectedY() {
		return selectedY;
	}
	public void setSelectedY(boolean selectedY) {
		this.selectedY = selectedY;
	}
	public boolean isSelectedZ() {
		return selectedZ;
	}
	public void setSelectedZ(boolean selectedZ) {
		this.selectedZ = selectedZ;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{name:"+name+",");
		sb.append("type:"+type+",");
		sb.append("selectedX:"+selectedX+",");
		sb.append("selectedY:"+selectedY+",");
		sb.append("selectedZ:"+selectedZ+"}");
		return sb.toString();
	}
}
