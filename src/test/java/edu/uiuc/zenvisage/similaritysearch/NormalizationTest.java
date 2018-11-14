package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.uiuc.zenvisage.service.utility.LinearNormalization;

public class NormalizationTest {
	
	@Test
	public void testLinearNormalization() {
		double[] input = {156.0, 160.0, 145.0, 190.0, 140.0, 150.0};
		double[] truth = {32.0, 40.0, 10.0, 100.0, 0.0, 20.0};
		LinearNormalization norm = new LinearNormalization();
		norm.normalize(input);
		assertTrue(Arrays.equals(input, truth));
	}
}
