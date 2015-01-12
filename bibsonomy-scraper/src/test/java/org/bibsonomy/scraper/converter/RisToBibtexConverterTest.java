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
package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class RisToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	/**
	 * Test RIS to BibTeX Conversion
	 * @throws IOException 
	 */
	@Test
	public void testRisToBibtex() throws IOException {
		final String ris = TestUtils.readEntryFromFile(PATH_TO_FILES + "test1.ris");

		// test the canHandle heuristic
		assertTrue(RisToBibtexConverter.canHandle(ris));

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "test1_risBibtex.bib");
		final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
		final String bibTeX = ris2bConverter.risToBibtex(ris);
		assertEquals (expectedBibTeX, bibTeX);
	}
	
	/**
	 * another RIS to BibTeX test
	 * @throws IOException
	 */
	@Test
	public void testRisToBibtex2() throws IOException {
		final String ris = TestUtils.readEntryFromFile(PATH_TO_FILES + "WorldCat_53972111.ris");

		// test the canHandle heuristic
		assertTrue(RisToBibtexConverter.canHandle(ris));

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "WorldCat_53972111.bib");
		final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
		final String bibTeX = ris2bConverter.risToBibtex(ris);
		assertEquals (expectedBibTeX, bibTeX);
	}
	
	/**
	 * http://www.agu.org/pubs/crossref/2008/2008JD010287.shtml
	 * @throws Exception 
	 */
	@Test
	public void testRisToBibtex1() throws Exception {
		final String ris = TestUtils.readEntryFromFile(PATH_TO_FILES + "2008JD010287.ris");

		// test the canHandle heuristic
		assertTrue(RisToBibtexConverter.canHandle(ris));

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "2008JD010287.bib");
		final RisToBibtexConverter ris2bConverter = new RisToBibtexConverter();
		final String bibTeX = ris2bConverter.risToBibtex(ris);

		assertEquals(expectedBibTeX, bibTeX);
		
		// test canHandle
		assertFalse(RisToBibtexConverter.canHandle(expectedBibTeX));
	}
}
