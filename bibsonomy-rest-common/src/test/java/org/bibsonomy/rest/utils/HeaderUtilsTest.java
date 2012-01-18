package org.bibsonomy.rest.utils;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.SortedMap;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class HeaderUtilsTest {

	@Test
	public void testGetPreferredTypes() {
		/*
		 * was throwing an exception (broken header?!)
		 */
		final String header = "text/html,application/xhtml+xml,application/xml;image/png,image/jpeg,image/*;q=0.9,*/*;q=0.8";
		final SortedMap<Double, List<String>> preferredTypes = HeaderUtils.getPreferredTypes(header);
		assertEquals("{1.0=[text/html, application/xhtml+xml, application/xml, image/jpeg], 0.9=[image/*], 0.8=[*/*]}", preferredTypes.toString());
	
	}

}
