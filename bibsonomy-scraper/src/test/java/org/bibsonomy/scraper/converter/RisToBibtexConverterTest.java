package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import bibtex.parser.ParseException;

/**
 * @author rja
 * @version $Id$
 */
public class RisToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	/**
	 * http://www.agu.org/pubs/crossref/2008/2008JD010287.shtml
	 * @throws ParseException 
	 */
	@Test
	public void testRisToBibtex1() throws ParseException {
		try {
			String ris = this.readEntryFromFile("2008JD010287.ris");

			// test the canHandle heuristic
			assertTrue(RisToBibtexConverter.canHandle(ris));

			// test the conversion
			final String expectedBibTeX = this.readEntryFromFile("2008JD010287.bib");
			final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
			final String bibTeX = ris2bConverter.risToBibtex(ris);

			assertEquals (expectedBibTeX, bibTeX);
		} catch (IOException ex) {
			fail(ex.getMessage());
		}
	}
	
	private String readEntryFromFile(final String fileName) throws IOException {
		final StringBuffer resultString = new StringBuffer();
		final BufferedReader in = new BufferedReader(new InputStreamReader(ToBibtexConverterTest.class.getClassLoader().getResourceAsStream(PATH_TO_FILES + fileName), "UTF-8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			resultString.append(line + "\n");
		}
		return resultString.toString();
	}

}
