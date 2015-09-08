package org.bibsonomy.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class SimpleTokenizerTest {
	@Test
	public void testIt() {
		List<String >res = new ArrayList<>();
		for (String token : new SimpleTokenizer("Henner Hurz  Schorsche")) {
			res.add(token);
		}
		Assert.assertEquals(Arrays.asList("Henner", "Hurz", "Schorsche"), res);
	}
	
	
	@Test
	public void testIt2() {
		List<String >res = new ArrayList<>();
		for (String token : new SimpleTokenizer("Michael Collins")) {
			res.add(token);
		}
		Assert.assertEquals(Arrays.asList("Michael", "Collins"), res);
	}
	
}
