/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.importer.xml;

import static org.junit.Assert.*;

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class XMLUnitTestImporterTest {

	@Test
	public void testGetUnitTests() {
		final XMLUnitTestImporter importer = new XMLUnitTestImporter();
		
		
		try {
			final List<ScraperUnitTest> unitTests = importer.getUnitTests();

			/*
			 * well, we have more than 60 scrapers, so we should have more than 100 tests ...
			 */
			assertTrue(unitTests.size() > 100);

			/*
			 * check each test
			 */
			for (final ScraperUnitTest scraperUnitTest : unitTests) {
				if (scraperUnitTest instanceof URLScraperUnitTest) {
					assertNotNull(((URLScraperUnitTest) scraperUnitTest).getExpectedReference());
				}
				
			}
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
		
	}

}
