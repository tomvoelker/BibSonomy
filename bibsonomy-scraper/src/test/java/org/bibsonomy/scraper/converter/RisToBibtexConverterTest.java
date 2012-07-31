/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class RisToBibtexConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
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

		assertEquals (expectedBibTeX, bibTeX);
	}
}
