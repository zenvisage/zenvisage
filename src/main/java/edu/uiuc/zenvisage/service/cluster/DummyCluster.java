package edu.uiuc.zenvisage.service.cluster;import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

public class DummyCluster {
	List<CentroidCluster<DoublePoint>> clusters;
	int[] realSizes;
	public DummyCluster (List<CentroidCluster<DoublePoint>> c, int[] rs) {
		this.clusters = c;
		this.realSizes = rs;
	}
	public List<CentroidCluster<DoublePoint>> getClusters() {
		return this.clusters;
	}
	public int[] getRealSizes() {
		return this.realSizes;
	}
}