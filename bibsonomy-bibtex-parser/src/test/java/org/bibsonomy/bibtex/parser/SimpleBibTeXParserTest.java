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

}
