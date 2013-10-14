package org.bibsonomy.services.filesystem.extension;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class ListExtensionCheckerTest {
	
	private static final String EXTENSION_1 = "pdf";
	private static final String EXTENSION_2 = "abc";
	private static final String EXTENSION_3 = "app";
	private static final ListExtensionChecker EXTENSION_CHECKER = new ListExtensionChecker(Arrays.asList(EXTENSION_1, EXTENSION_2, EXTENSION_3));
	
	/**
	 * tests for {@link ListExtensionChecker#checkExtension(String)}
	 * @throws Exception
	 */
	@Test
	public void testCheckExtension() throws Exception {
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_1));
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_2));
		assertTrue(EXTENSION_CHECKER.checkExtension(EXTENSION_3));
		assertFalse(EXTENSION_CHECKER.checkExtension("thisextension"));
	}

}
