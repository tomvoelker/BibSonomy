package org.bibsonomy.scraper.util;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.kde.acm.ACMBasicScraper;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class BibTeXUtilsTest {

	private static final String FIELD_VALUE = "http://www.slac.stanford.edu/spires/find/hep?key=6368328";
	private static final String FIELD_NAME  = "url";
	private final static String bibtexStart = "@Article{Okumura:2005qr,\n" +  
	"author    = \"Okumura, Ken-ichi\",\n" + 
	"title     = \"{Sparticle spectrum and EWSB of mixed modulus-anomaly\n" +
	"             mediation in fluxed string compactification models}\",\n" +
	"year      = \"2005\",\n" + 
	"eprint    = \"hep-ph/0509225\",\n" +
	"archivePrefix = \"arXiv\",\n" + 
	"SLACcitation  = \"%%CITATION = HEP-PH/0509225;%%\"\n";
	
	private final static String bibtex = bibtexStart + "}";

	/**
	 * Tests with a field not occuring in the entry
	 */
	@Test
	public void testAddFieldNotContained() {
		final String addFieldIfNotContained = BibTeXUtils.addFieldIfNotContained(bibtex, FIELD_NAME, FIELD_VALUE);
		final String expected = bibtexStart + "," + FIELD_NAME + " = {" + FIELD_VALUE + "}\n}";
		assertEquals(expected, addFieldIfNotContained);
	}

	/**
	 * Tests with a field occuring in the entry
	 */
	@Test
	public void testAddFieldContained() {
		final String addFieldIfNotContained = BibTeXUtils.addFieldIfNotContained(bibtex, "year", FIELD_VALUE);
		assertEquals(bibtex, addFieldIfNotContained);
	}
	
	/**
	 * Tests with a field not occuring in the entry
	 */
	@Test
	public void testAddField() {
		final StringBuffer buf = new StringBuffer(bibtex);
		BibTeXUtils.addField(buf, FIELD_NAME, FIELD_VALUE);
		final String expected = bibtexStart + "," + FIELD_NAME + " = {" + FIELD_VALUE + "}\n}";
		assertEquals(expected, buf.toString());
	}
	

}
