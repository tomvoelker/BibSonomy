/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.snippet;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class SnippetScraperTest {

	/** Works with 
	 * BibtexAbstractValue value = parseValue();
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape1() throws MalformedURLException {

		String bibtex = 
			"@COMMENT{a\n" +
			"bar\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertFalse(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	/** Works not.
	 * 
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape2() throws MalformedURLException {

		String bibtex = 
			"@COMMENT{a,\n" +
			"bar\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertFalse(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	/** Works with full-fledged entry parsing
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape3() throws MalformedURLException {

		final String bibtex = 
			"@COMMENT{a,\n" +
			"bar = {foo}\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertFalse(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	/** Works with full-fledged entry parsing
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape4() throws MalformedURLException {

		final String bibtex = 
			"@COMMENT{a,\n" +
			"bar = {foo}\n" +
			"}\n" +
			"\n" +
			"@article{foo,\n" +
			"title = {foo}\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertTrue(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	/** Works with full-fledged entry parsing
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape5() throws MalformedURLException {

		final String bibtex = 
			"@COMMENT{a,\n" +
			"bar = {foo}\n" +
			"\n" +
			"@article{foo,\n" +
			"title = {foo}\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertTrue(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}


	/** Works with full-fledged entry parsing
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape6() throws MalformedURLException {

		final String bibtex = 
			"@COMMENT{a,@article{foo,\n" +
			"title = {foo}\n" +
			"}";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertTrue(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	/** Works with full-fledged entry parsing
	 * @throws MalformedURLException
	 */
	@Test
	public void testScrape7() throws MalformedURLException {

		final String bibtex = 
			"  @COMMENT{meta_data,\n" + 
			"    INDEX     = {META},\n" + 
			"    TYPE      = {Research links},\n" + 
			"       TITLE = {peters research links},\n" + 
			"      HEADING = {links and info collected by plr},\n" + 
			"      SOURCE = {entered by plr}\n" + 
			"  }\n";

		final ScrapingContext context = new ScrapingContext(new URL("http://ffo.bar"));
		context.setSelectedText(bibtex);
		final SnippetScraper scraper = new SnippetScraper();

		try {
			assertFalse(scraper.scrape(context));
		} catch (final ScrapingException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

}
