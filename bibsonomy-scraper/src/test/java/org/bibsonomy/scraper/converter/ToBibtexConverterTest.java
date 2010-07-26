package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

/**
 * @author sdo
 * @version $Id$
 */
public class ToBibtexConverterTest {
	
	private final String PATH_TO_FILES = "src/test/resources/org/bibsonomy/scraper/converter/";

	/**
	 * Test Ris to BibTeX Conversion
	 */
	@Test
	public void testRisToBibtex() {
		String ris = this.readEntryFromFile("test1.ris");
		
		// test the canHandle heuristic
		assertTrue(RisToBibtexConverter.canHandle(ris));
		
		// test the conversion
		String expectedBibTeX = this.readEntryFromFile("test1_risBibtex.bib");
		RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
		String bibTeX = ris2bConverter.RisToBibtex(ris);
		assertTrue (expectedBibTeX.equals(bibTeX));
		
	}

	/**
	 * Test Endnote to BibTeX Conversion
	 */
	@Test
	public void testEndnoteToBibtex() {
		String endnote = this.readEntryFromFile("test1.endnote");
		
		// test the canHandle heuristic
		assertTrue(EndnoteToBibtexConverter.canHandle(endnote));
		
		// test the conversion
		String expectedBibTeX = this.readEntryFromFile("test1_endnoteBibtex.bib");
		EndnoteToBibtexConverter e2bConverter = new EndnoteToBibtexConverter();
		String bibTeX = e2bConverter.endnoteToBibtex(endnote);
		assertTrue (expectedBibTeX.trim().equals(bibTeX.trim()));
		
	
	}

	private String readEntryFromFile(String fileName) {
		BufferedReader in;
		StringBuffer resultString = new StringBuffer();
		try {
			in = new BufferedReader(new FileReader(PATH_TO_FILES + fileName));
			String line = null;
			while ((line = in.readLine()) != null) {
				resultString.append(line);
				resultString.append("\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Reading Endnote from file failed");
		}
		return resultString.toString();
	}
}