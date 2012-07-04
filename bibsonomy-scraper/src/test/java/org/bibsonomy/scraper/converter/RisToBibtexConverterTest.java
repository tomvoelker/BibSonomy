package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class RisToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	/**
	 * http://www.agu.org/pubs/crossref/2008/2008JD010287.shtml
	 * @throws Exception 
	 */
	@Test
	public void testRisToBibtex1() throws Exception {
		final String ris = TestUtils.readEntryFromFile(PATH_TO_FILES + "2008JD010287.ris");

		// test the canHandle heuristic
		assertTrue(RisToBibtexConverter.canHandle(ris));

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "2008JD010287.bib");
		final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
		final String bibTeX = ris2bConverter.risToBibtex(ris);

		assertEquals (expectedBibTeX, bibTeX);
	}
}
