package org.bibsonomy.bibtex.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

import bibtex.parser.ParseException;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleBibTeXParserTest {

	private static final String entry1 = "@book{behrendt2007,\n" +
	"title = {Web 2.0 },\n" + 
	"author = {Jens Behrendt and Klaus Zeppenfeld},\n" + 
	"publisher = {Springer},\n" + 
	"year = 2007,\n" + 
	"url = {http://ftubhan.tugraz.at/han/ZDB-2-STI/www.springerlink.com/content/wk5317/},\n" + 
	"biburl = {http://www.bibsonomy.org/bibtex/22407a08751c316c63686d37228a25b3d/diam_eter},\n" + 
	"keywords = {ISR_07}\n" +
	"}";

	final String entry2 = "@article{foo,\n" +
	"title = {Foo Barness},\n" +
	"author = {M. Mustermann}}";

	@Test
	public void testParseBibTeX1() {
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		try {
			final BibTex bibtex = parser.parseBibTeX(entry1);

			assertEquals("Web 2.0 ", bibtex.getTitle());
			assertEquals("Jens Behrendt and Klaus Zeppenfeld", bibtex.getAuthor());
			assertEquals("book", bibtex.getEntrytype());
			assertEquals("behrendt2007", bibtex.getBibtexKey());
			assertEquals("Springer", bibtex.getPublisher());
			assertEquals("2007", bibtex.getYear());
			assertEquals("http://ftubhan.tugraz.at/han/ZDB-2-STI/www.springerlink.com/content/wk5317/", bibtex.getMiscField("url"));
			assertEquals("ISR_07", bibtex.getMiscField("keywords"));
			assertEquals("http://www.bibsonomy.org/bibtex/22407a08751c316c63686d37228a25b3d/diam_eter", bibtex.getMiscField("biburl"));

		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());		
		}
	}

	
	@Test
	public void testParseBibTeX2() {
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		try {
			final BibTex bibtex = parser.parseBibTeX(entry2);

			assertEquals("Foo Barness", bibtex.getTitle());
			assertEquals("M. Mustermann", bibtex.getAuthor());
			assertEquals("article", bibtex.getEntrytype());
			assertEquals("foo", bibtex.getBibtexKey());

		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());		
		}
	}

}
