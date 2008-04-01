package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Schenk
 * @version $Id$
 */
public class ClassifierModeTest {

	/**
	 * tests getAbbreviation
	 */
	@Test
	public void getAbbreviation() {
		assertEquals("D", ClassifierMode.DAY.getAbbreviation());
		assertEquals("N", ClassifierMode.NIGHT.getAbbreviation());
	}
}