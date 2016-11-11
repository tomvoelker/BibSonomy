package org.bibsonomy.layout.csl;

import static org.bibsonomy.util.ValidationUtils.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * tests for {@link CSLFilesManager}
 * @author dzo
 */
public class CSLFilesManagerTest {
	
	private static final CSLFilesManager MANAGER = new CSLFilesManager();
	
	/**
	 * init the {@link #MANAGER}
	 */
	@BeforeClass
	public static void init() {
		MANAGER.init();
	}
	
	/**
	 * tests {@link CSLFilesManager#getStyleByName(String)}
	 */
	@Test
	public void testGetStyleByName() {
		assertNull(MANAGER.getStyleByName("mycsl"));
		final String cslID = "colombian-journal-of-anesthesiology";
		final CSLStyle style = MANAGER.getStyleByName(cslID);
		assertEquals("Colombian Journal of Anesthesiology", style.getDisplayName());
		assertEquals(cslID + ".csl", style.getId());
	}
	
	/**
	 * tests {@link CSLFilesManager#getLocaleFile(String)}
	 */
	@Test
	public void testGetLocale() {
		assertNotNull(MANAGER.getLocaleFile("de-DE"));
	}
}
