package org.bibsonomy.services.filesystem.extension;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class WildcardExtensionCheckerTest {
	
	private static final WildcardExtensionChecker EXTENSION_CHECKER = new WildcardExtensionChecker();

	/**
	 * tests {@link WildcardExtensionChecker#checkExtension(String)}
	 */
	@Test
	public void testCheckExtension() {
		assertTrue(EXTENSION_CHECKER.checkExtension("pdf"));
		assertTrue(EXTENSION_CHECKER.checkExtension("app"));
		assertTrue(EXTENSION_CHECKER.checkExtension("apk"));
		assertTrue(EXTENSION_CHECKER.checkExtension(null));
		assertTrue(EXTENSION_CHECKER.checkExtension("pptx"));
		assertTrue(EXTENSION_CHECKER.checkExtension("abcdefghijklmnopqrstuvwxyz"));
	}
}
