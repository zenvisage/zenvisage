package edu.uiuc.zenvisage.model;

public class VariableMeta {
	private String attribute;
	private String type;
	private boolean selectedX;
	private boolean selectedY;
	private boolean selectedZ;
	private float min;
	private float max;
	public VariableMeta(String attribute, String type, boolean selectedX, boolean selectedY, 
			boolean selectedZ, float min, float max){
		this.attribute = attribute;
		this.type = type;
		this.selectedX = selectedX;
		this.selectedY = selectedY;
		this.selectedZ = selectedZ;
		this.min = min;
		this.max = max;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
}
