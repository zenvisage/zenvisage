package edu.uiuc.zenvisage.classcreation.distance;

import org.junit.Test;

import edu.uiuc.zenvisage.service.distance.EMD;

public class EMDTest {
	@Test
	public void TestCorrectness() {
		EMD distance = new EMD();
		double[][] src = new double[][] {{1,2}, {1,5}, {2,2}, {4,5}, {6,7}, {3,10}};
		double[][] tar = new double[][] {{5,6}, {2,3}, {4,8}, {1,9}, {2,12}, {3,6}, {1,7}};
		distance.calculateDistance(src, tar);
	}
}
