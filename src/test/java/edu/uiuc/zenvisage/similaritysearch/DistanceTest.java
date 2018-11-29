package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.MVIP;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;

public class DistanceTest {
	double[] src = {3, 0, 1, 2};
	double[] tar = {1, 2, 3, 4};
	double[] perturb = new double[4];
	int epsilon = 5;
	
	@Before
	public void init() {
		for(int i = 0; i < 4; i++) {
			perturb[i] = ((1 + Math.random()) * epsilon);			
		}
	}
	
	@Test
	public void testEuclidean() {
		Distance distance = new Euclidean();
		double res1 = distance.calculateDistance(src, tar);
		double[] src2 = new double[4];
		for(int i = 0; i < 4; i++) {
			src2[i] = perturb[i] * src[i];
		}
		double res2 = distance.calculateDistance(src2, tar);
		System.out.println(res1);
		System.out.println(res2);
		assertTrue((res2 <= 2 * epsilon * res1));
	}
	
	@Test
	public void testSegmentation() {
		Distance distance = new SegmentationDistance();
		double res1 = distance.calculateDistance(src, tar);
		double[] src2 = new double[4];
		for(int i = 0; i < 4; i++) {
			src2[i] = perturb[i] * src[i];
		}
		double res2 = distance.calculateDistance(src2, tar);
		System.out.println(res1);
		System.out.println(res2);
		assertTrue((res2 <= 2 * epsilon * res1));	
	}
	
	@Test
	public void testMVIP() {
		Distance distance = new MVIP();
		double res1 = distance.calculateDistance(src, tar);
		double[] src2 = new double[4];
		for(int i = 0; i < 4; i++) {
			src2[i] = perturb[i] * src[i];
		}
		double res2 = distance.calculateDistance(src2, tar);
		System.out.println(res1);
		System.out.println(res2);
		assertTrue((res2 <= 2 * epsilon * res1));
	}
	
	@Test
	public void testDTW() {
		Distance distance = new DTWDistance();
		double res1 = distance.calculateDistance(src, tar);
		double[] src2 = new double[4];
		for(int i = 0; i < 4; i++) {
			src2[i] = perturb[i] * src[i];
		}
		double res2 = distance.calculateDistance(src2, tar);
		System.out.println(res1);
		System.out.println(res2);
		assertTrue((res2 <= 2 * epsilon * res1));
		
	}
	
	@Test
	public void testSameVector() {
		Distance distance1 = new DTWDistance();
		double res1 = distance1.calculateDistance(src, src);		
		assertTrue(res1 == 0);
		
		Distance distance2 = new MVIP();
		double res2 = distance2.calculateDistance(src, src);		
		assertTrue(res2 == 0);
		
		Distance distance3 = new SegmentationDistance();
		double res3 = distance3.calculateDistance(src, src);		
		assertTrue(res3 == 0);
		
		Distance distance4 = new Euclidean();
		double res4 = distance4.calculateDistance(src, src);		
		assertTrue(res4 == 0);
	}
	
	@Test
	public void testInequalLengthDTW() {
		Distance distance = new DTWDistance();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthMVIP() {
		Distance distance = new MVIP();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthSegmentation() {
		Distance distance = new SegmentationDistance();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
	@Test
	public void testInequalLengthEuclidean() {
		Distance distance = new Euclidean();
		double[] target = {1,2,3,4,5};
		try {
			double res = distance.calculateDistance(src, target);
			assertTrue(false);
		} catch (AssertionError e) {
			assertTrue(true);
		}		
	}
	
}







