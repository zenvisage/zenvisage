package edu.uiuc.zenvisage.data.remotedb;
public class ColumnMetadata {
  public String name;
  public String dataType;
  public boolean isIndexed;
  public float min = 100000;
  public float max=-1000;
  public String unit;
  public float pAAWidth = (float) 0.1;
  public int numberOfSegments = 3000;
  public String columnType;
}
