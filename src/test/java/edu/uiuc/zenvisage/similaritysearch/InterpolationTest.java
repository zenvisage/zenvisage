package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;

import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.LinearNormalization;

public class InterpolationTest {
	private double[] Ys = {158, 156, 157, 156, 158, 159};
	private double[] Xs = {1, 2, 3, 4, 5, 6};
	private double[] Xrange = {1.0, 6.0};
	private int length = 11;
	private ArrayList<Float> Ylist = new ArrayList<Float>();
	private ArrayList<Float> Xlist = new ArrayList<Float>();
	private float[] XrangeFloat;

	@Before
	public void init() {
		for(double i : Ys) { 
			Ylist.add((float)i);
		}
		for(double i : Xs) { 
			Xlist.add((float)i);
		}
		XrangeFloat = Floats.toArray(Doubles.asList(Xrange));
	}
	
	@Test
	public void testInterpolation() {
		LinearNormalization norm = new LinearNormalization();
		DataReformation reform = new DataReformation(norm);
		double[] res = reform.getInterpolatedData(Xlist, Ylist, XrangeFloat, length);
		double[] truth = {158.0, 157.0, 156.0, 156.5, 157.0, 156.5, 156.0, 157.0, 158.0, 158.5, 159.0};
		norm.normalize(truth);
		assertTrue(Arrays.equals(res, truth));
	}
}







