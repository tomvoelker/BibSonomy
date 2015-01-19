/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.importer.xml;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.bibsonomy.scraper.ScraperTestData;
import org.junit.Test;

/**
 * @author rja
 */
public class XMLUnitTestImporterTest {

	@Test
	public void testGetUnitTests() throws Exception {
		final XMLUnitTestImporter importer = new XMLUnitTestImporter();
		final Map<String, ScraperTestData> unitTests = importer.getUnitTests();

		/*
		 * well, we have more than 60 scrapers, so we should have more than 100 tests ...
		 */
		assertTrue(unitTests.size() > 100);

		/*
		 * check each test
		 */
		for (final ScraperTestData scraperTestData : unitTests.values()) {
			assertNotNull(scraperTestData.getExpectedBibTeX());
		}
	}

}
