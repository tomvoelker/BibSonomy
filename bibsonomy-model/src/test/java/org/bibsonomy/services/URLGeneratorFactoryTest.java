package org.bibsonomy.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * tests for the {@link URLGeneratorFactory}
 *
 * @author dzo
 */
public class URLGeneratorFactoryTest {
	
	/**
	 * tests {@link URLGeneratorFactory#buildProjectHome(String, String)}
	 */
	@Test
	public void testBuildProjectHome() {
		assertEquals("/", URLGeneratorFactory.buildProjectHome(null, null));
		assertEquals("/export/", URLGeneratorFactory.buildProjectHome(null, "export"));
		assertEquals("https://www.bibsonomy.org/export/", URLGeneratorFactory.buildProjectHome("https://www.bibsonomy.org/", "export"));
	}
}
