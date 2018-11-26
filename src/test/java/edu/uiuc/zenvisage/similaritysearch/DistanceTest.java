package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.MVIP;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;

public class DistanceTest {
	double[] src = {3, 0, 1, 2};
	double[] tar = {1, 2, 3, 4};
	
	@Test
	public void testEuclidean() {
		Distance distance = new Euclidean();
		double res = distance.calculateDistance(src, tar);
		double truth = 4;
		assertTrue(res == truth);
	}
	
//	@Test
//	public void testSegmentation() {
//		Distance distance = new SegmentationDistance();
//		double res = distance.calculateDistance(src, tar);
//		double truth = -1;
//		assertTrue(res == truth);
//	}
//	
//	@Test
//	public void testMVIP() {
//		Distance distance = new MVIP();
//		double res = distance.calculateDistance(src, tar);
//		double truth = -1;
//		assertTrue(res == truth);
//	}
//	
//	@Test
//	public void testDTW() {
//		Distance distance = new DTWDistance();
//		double res = distance.calculateDistance(src, tar);
//		double truth = -1;
//		assertTrue(res == truth);
//	}
//	
	@Test
	public void testSameVector() {
		Distance distance = new DTWDistance();
		double res = distance.calculateDistance(src, src);
		double truth = 0;
		assertTrue(res == truth);
	}
	
	@Test
	public void testInequalLength() {
		Distance distance = new DTWDistance();
		double[] tar2 = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, tar2);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}	
	}
}







