package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * @author sdo
 * @version $Id$
 */
public class ToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";

	/**
	 * Test RIS to BibTeX Conversion
	 */
	@Test
	public void testRisToBibtex() {
		try {
			String ris = this.readEntryFromFile("test1.ris");

			// test the canHandle heuristic
			assertTrue(RisToBibtexConverter.canHandle(ris));

			// test the conversion
			final String expectedBibTeX = this.readEntryFromFile("test1_risBibtex.bib");
			final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
			final String bibTeX = ris2bConverter.RisToBibtex(ris);
			assertEquals (expectedBibTeX, bibTeX);
		} catch (IOException ex) {
			fail(ex.getMessage());
		}

	}

	/**
	 * Test Endnote to BibTeX Conversion
	 */
	@Test
	public void testEndnoteToBibtex() {
		/*
		 * Note that in the testfile 2 endnote entries are given
		 * the first is a regular endnote (like it is exported by BibSonomy)
		 * the second contains only authors to test the correct conversion of
		 * the author field to bibtex!
		 */
		try {
			String endnote = this.readEntryFromFile("test1.endnote");

			// test the canHandle heuristic
			assertTrue(EndnoteToBibtexConverter.canHandle(endnote));

			// test the conversion
			final String expectedBibTeX = this.readEntryFromFile("test1_endnoteBibtex.bib");
			final EndnoteToBibtexConverter e2bConverter = new EndnoteToBibtexConverter();
			final String bibTeX = e2bConverter.endnoteToBibtex(endnote);
			assertEquals(expectedBibTeX.trim(), bibTeX.trim());
		} catch (IOException e) {
			fail(e.getMessage());
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