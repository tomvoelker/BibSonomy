package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class PicaToBibtexConverterTest {
	
	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	@Test
	public void testGetBibResult1() throws IOException {
		testFile("opac1", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=273285416");
	}

	@Test
	public void testGetBibResult2() throws IOException {
		testFile("opac2", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=231779038");
	}

	@Test
	public void testGetBibResult3() throws IOException {
		testFile("opac3", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=185335748");
	}

	@Test
	public void testGetBibResult4() throws IOException {
		testFile("opac4", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=098898043");
	}
	
	@Test
	public void testGetBibResult5() throws IOException {
		testFile("opac5", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=178208876");
	}
	
	/**
	 * Has ISBN in another field
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetBibResult6() throws IOException {
		testFile("opac6", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=118339710");
	}

	@Test
	public void testGetBibResult7() throws IOException {
		testFile("opac7", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=184916631");
	}

	
	private void testFile(final String fileName, final String url) throws IOException {
		final String xml = this.readEntryFromFile(fileName + ".xml");
		final String bib = this.readEntryFromFile(fileName + ".bib");
		
		final PicaToBibtexConverter pica = new PicaToBibtexConverter(xml, "xml", url);
		
		assertEquals(bib.trim(), pica.getBibResult().trim());
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
