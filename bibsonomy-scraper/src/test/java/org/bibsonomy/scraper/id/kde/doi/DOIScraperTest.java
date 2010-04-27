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

package org.bibsonomy.scraper.id.kde.doi;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for DOIScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class DOIScraperTest {

	
	
	@Test
	@Ignore
	public void testScraper1() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dx.doi.org/10.1007/11922162"));
		final DOIScraper scraper = new DOIScraper();
		
		Assert.assertFalse(scraper.scrape(sc));
		
		Assert.assertEquals("http://www.springerlink.com/index/10.1007/11922162", sc.getUrl().toString());
		
	}

	
	@Test
	@Ignore
	public void testScraper2() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://www.example.com/"));
		final DOIScraper scraper = new DOIScraper();
		
		sc.setSelectedText("10.1145/160688.160713");
		//DOI: 10.1016/j.spl.2008.05.017
		
		Assert.assertFalse(scraper.scrape(sc));
		
		Assert.assertEquals("http://portal.acm.org/citation.cfm?doid=160688.160713", sc.getUrl().toString());
		
	}
	
}
