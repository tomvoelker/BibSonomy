/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import java.io.IOException;
import org.junit.Test;
import org.bibsonomy.testutil.TestUtils;

/**
 * @author Mohamemd Abed
 */
public class CslToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/csl2bibtex/";
	
	/**
	 * another CSL to BibTeX test
	 * @throws IOException
	 */
	@Test
	public void testCslToBibtex1() throws IOException {
		final String csl = TestUtils.readEntryFromFile(PATH_TO_FILES + "csltobibtextest1.json");

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "csltobibtextest1.bib").trim();
		final CslToBibtexConverter cslToBibtexConverter = new CslToBibtexConverter();
		final String bibTeX = cslToBibtexConverter.toBibtex(csl);
		assertEquals(expectedBibTeX, bibTeX);
	}

	@Test
	public void testCSLToBibTexACMConvert() throws IOException {
		final String csl = TestUtils.readEntryFromFile(PATH_TO_FILES + "csl2bibtex2.json");

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "csl2bibtex2.bib").trim();
		final CslToBibtexConverter cslToBibtexConverter = new CslToBibtexConverter();
		final String bibTeX = cslToBibtexConverter.toBibtex(csl);
		assertEquals(expectedBibTeX, bibTeX);
	}
}
