/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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

package org.bibsonomy.scraper.InformationExtraction;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class IEScraperTest {

	private final String expectedBibtex = 
		"@misc{noauthororeditor2008,\n" +
		"booktitle = {Michael May and Bettina Berendt and Antoine Cornuejols and Joao Gama and Fosca Giannotti and Andreas Hotho and Donato Malerba and Ernestina Menesalvas and Katharina Morik and Rasmus Pedersen and Lorenza Saitta and Yucel Saygin and Assaf Schuster and Koen Vanhoof. Research Challenges in Ubiquitous Knowledge Discovery. Next Generation of Data Mining(Chapman & Hall / Crc Data Mining and Knowledge Discovery Series), Chapman & Hall / CRC},\n" +
		"date = {2008},\n" +
		"year = {2008}\n" +
		",url = {http://www.example.com/reasearch_challenges.html}\n" +
		"}";

	@Test
	public void testScrape() {
		final ScrapingContext sc = IEScraper.getTestContext();
		try {
			sc.setUrl(new URL("http://www.example.com/reasearch_challenges.html"));
		} catch (MalformedURLException ex) {
			fail(ex.getMessage());
		}

		final IEScraper scraper = new IEScraper();

		try {
			final boolean scrape = scraper.scrape(sc);
			Assert.assertTrue(scrape);
		} catch (final ScrapingException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}

		final String bibtex = sc.getBibtexResult();

		Assert.assertEquals(expectedBibtex, bibtex);

	}

}
