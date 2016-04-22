package org.bibsonomy.search.index.utils.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * tests for {@link PlainTextExtractor}
 *
 * @author dzo
 */
public class PlainTextExtractorTest {
	private static final PlainTextExtractor EXTRACTOR = new PlainTextExtractor();
	
	/**
	 * tests {@link PlainTextExtractor#extractContent(File)}
	 * @throws Exception
	 */
	@Test
	public void testExtractContent() throws Exception {
		final File testFile = new File(PlainTextExtractorTest.class.getClassLoader().getResource("extraction/test.txt").getFile());
		assertEquals("Human dignity shall be inviolable. To respect and protect it shall be the duty of all state authority.", EXTRACTOR.extractContent(testFile));
	}
	
	/**
	 * tests {@link PlainTextExtractor#supports(File)}
	 * @throws Exception
	 */
	@Test
	public void testSupports() throws Exception {
		assertFalse(EXTRACTOR.supports("test.dmg"));
		assertFalse(EXTRACTOR.supports("test.pdf"));
		assertFalse(EXTRACTOR.supports("test"));
		assertTrue(EXTRACTOR.supports("test.txt"));
	}

}
