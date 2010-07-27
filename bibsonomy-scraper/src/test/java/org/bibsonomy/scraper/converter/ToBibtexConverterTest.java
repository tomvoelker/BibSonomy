package org.bibsonomy.scraper.converter;

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
		// test the conversion
		try {
			final String ris = this.readEntryFromFile("test1.ris");

			// test the canHandle heuristic
			assertTrue(RisToBibtexConverter.canHandle(ris));


			final String expectedBibTeX = this.readEntryFromFile("test1_risBibtex.bib");
			final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
			final String bibTeX = ris2bConverter.RisToBibtex(ris);
			assertTrue (expectedBibTeX.equals(bibTeX));
		} catch (IOException ex) {
			fail(ex.getMessage());
		}

	}

	/**
	 * Test Endnote to BibTeX Conversion
	 */
	@Test
	public void testEndnoteToBibtex() {
		try {
			String endnote = this.readEntryFromFile("test1.endnote");

			// test the canHandle heuristic
			assertTrue(EndnoteToBibtexConverter.canHandle(endnote));

			// test the conversion
			String expectedBibTeX = this.readEntryFromFile("test1_endnoteBibtex.bib");
			EndnoteToBibtexConverter e2bConverter = new EndnoteToBibtexConverter();
			String bibTeX = e2bConverter.endnoteToBibtex(endnote);
			assertTrue (expectedBibTeX.trim().equals(bibTeX.trim()));
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