package edu.uiuc.zenvisage.similaritysearch;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import edu.uiuc.zenvisage.service.SmoothingUtil;


public class SmoothingTest {

	private LinkedHashMap<String,LinkedHashMap<Float,Float>> data;
	
	@Before
	public void init() {
		String valueStr = "1.0=158, 2.0=156, 3.0=157, 4.0=156, 5.0=158, 6.0=159, 7.0=163, 8.0=164, 9.0=164, 10.0=162, 11.0=161, 12.0=160";
		data = stringToMap(valueStr, "Owings Mills");
	}
	
	private LinkedHashMap<String,LinkedHashMap<Float,Float>> stringToMap(String value, String key) {
		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = new LinkedHashMap<String,LinkedHashMap<Float,Float>>();
		LinkedHashMap<Float,Float> valueMap = new LinkedHashMap<Float,Float>();
		for(String s : value.split(", ")){
			valueMap.put(Float.valueOf(s.split("=")[0]), Float.valueOf(s.split("=")[1]));
		}
		res.put(key, valueMap);
		return res;
	}
	
	private String mapToString(LinkedHashMap<String,LinkedHashMap<Float,Float>> map, String key) {
		LinkedHashMap<Float,Float> value = map.get(key);
		StringBuilder sb = new StringBuilder();
		Iterator<Float> it = (Iterator<Float>) value.keySet().iterator();
		while(it.hasNext()){
			Float s = it.next();
			sb = sb.append(String.valueOf(s)).append("=").append(String.valueOf(value.get(s))).append(", ");
		}
		String res = sb.toString();
		return res.substring(0, res.length() - 2);
	}
	
//	@Test
//	public void testMovingAverage() {
//		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = SmoothingUtil.applySmoothing(data, "movingaverage", 0.5);
//		String calcValue = mapToString(res, "Owings Mills");
//		String trueValue = "";
//		System.out.println(calcValue);
//		assertEquals(calcValue, trueValue);
//	}
//	
//	@Test
//	public void testExponential() {
//		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = SmoothingUtil.applySmoothing(data, "exponentialmovingaverage", 0.5);
//		String calcValue = mapToString(res, "Owings Mills");
//		String trueValue = "";
//		System.out.println(calcValue);
//		assertEquals(calcValue, trueValue);
//	}
//	
//	@Test
//	public void testLeoss() {
//		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = SmoothingUtil.applySmoothing(data, "leossInterpolation", 0.5);
//		String calcValue = mapToString(res, "Owings Mills");
//		String trueValue = "";
//		System.out.println(calcValue);
//		assertEquals(calcValue, trueValue);
//	}
//	
//	@Test
//	public void testGaussian() {
//		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = SmoothingUtil.applySmoothing(data, "gaussian", 0.5);
//		String calcValue = mapToString(res, "Owings Mills");
//		String trueValue = "";
//		System.out.println(calcValue);
//		assertEquals(calcValue, trueValue);
//	}
	
	@Test
	public void testZeroCoefficient() {
		LinkedHashMap<String,LinkedHashMap<Float,Float>> res = SmoothingUtil.applySmoothing(data, "gaussian", 0);
		String calcValue = mapToString(res, "Owings Mills");
		String trueValue = "1.0=158.0, 2.0=156.0, 3.0=157.0, 4.0=156.0, 5.0=158.0, 6.0=159.0, 7.0=163.0, 8.0=164.0, 9.0=164.0, 10.0=162.0, 11.0=161.0, 12.0=160.0";
		assertEquals(calcValue, trueValue);
	}
	
}






