/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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

	private static final String entry2 = "@article{foo,\n" +
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
			assertEquals("http://ftubhan.tugraz.at/han/ZDB-2-STI/www.springerlink.com/content/wk5317/", bibtex.getUrl());
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

	@Test
	public void testParse3() {
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();
		try {
			final String foo = 
				"@article{foo,\n" +
				"author = {{Hartmann}, L. and {Burkert}, A.},\n" +
				"title = {Hallo}\n}";
			final BibTex bibtex = parser.parseBibTeX(foo);


			System.out.println(bibtex);
//			assertEquals("Foo Barness", bibtex.getTitle());
//			assertEquals("M. Mustermann", bibtex.getAuthor());
//			assertEquals("article", bibtex.getEntrytype());
//			assertEquals("foo", bibtex.getBibtexKey());

		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());		
		}

	}

	/**
	 * Currently, we normalize author names, i.e., 
	 * 
	 * Knuth, D.E.
	 * 
	 * becomes
	 * 
	 * D.E. Knuth. 
	 * 
	 * In principle, this is bad, because it breaks names like
	 * 
	 * Vander Wal, Martin
	 * 
	 * (BibTeX then thinks "Vander" is a second surname).
	 * 
	 * Nevertheless, we document this "feature" here because we can't just 
	 * change it without changing other methods (like author handling).
	 */
	@Test
	public void testAuthorNormalization() {
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();

		try {
			final BibTex parsedBibTeX = parser.parseBibTeX(
					"@article{foo,\n" +
					"  author = {Knuth, D.E.}\n" + 
					"}"
			);
			
			assertEquals("D.E. Knuth", parsedBibTeX.getAuthor());

		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());
		}	
	}

	/**
	 * We disabled month normalization, this is documented here.
	 * 
	 * Why did we disable it? Otherwise, a month like "jun" would be normalized
	 * to "June" and using this with BibTeX destroys I18N! (there are some 
	 * BibTeX styles, which can substitute "jun" by the correct word, depending
	 * on the language you have set for your document. This works only with the
	 * abbreviations!).
	 */
	@Test
	public void testMonthNormalization() {
		final SimpleBibTeXParser parser = new SimpleBibTeXParser();

		try {
			final BibTex parsedBibTeX = parser.parseBibTeX(
					"@article{foo,\n" +
					"  month = jun\n" + 
					"}"
			);
			
			assertEquals("jun", parsedBibTeX.getMonth());

		} catch (ParseException ex) {
			fail(ex.getMessage());
		} catch (IOException ex) {
			fail(ex.getMessage());
		}	
	}

	protected BibTex getExampleBibtex() {
		final BibTex bib = new BibTex();
		bib.setEntrytype("inproceedings");
		bib.setBibtexKey("KIE");
		bib.setTitle("The most wonderfult title on earth");
		bib.setAuthor("Hans Dampf and Peter Silie");
		bib.setJournal("Journal of the most wonderful articles on earth");
		bib.setYear("2525");
		bib.setVolume("3");
		bib.setAbstract("This is a nice abstract.");
		bib.setPrivnote("This is private!");

		bib.setMisc("doi = {my doi}, isbn = {999-12345-123-x}, vgwort = {12}");
		return bib;
	}

}
