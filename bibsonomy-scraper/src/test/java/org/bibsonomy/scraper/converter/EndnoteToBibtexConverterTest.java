/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author sdo
 * @version $Id$
 */
public class EndnoteToBibtexConverterTest {
	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	/**
	 * Test Endnote to BibTeX Conversion
	 * @throws IOException 
	 */
	@Test
	public void testEndnoteToBibtex() throws IOException {
		/*
		 * Note that in the testfile 2 endnote entries are given
		 * the first is a regular endnote (like it is exported by BibSonomy)
		 * the second contains only authors to test the correct conversion of
		 * the author field to bibtex!
		 */
		final String endnote = TestUtils.readEntryFromFile(PATH_TO_FILES + "test1.endnote");

		// test the canHandle heuristic
		assertTrue(EndnoteToBibtexConverter.canHandle(endnote));

		// test the conversion
		final String expectedBibTeX = TestUtils.readEntryFromFile(PATH_TO_FILES + "test1_endnoteBibtex.bib");
		final EndnoteToBibtexConverter e2bConverter = new EndnoteToBibtexConverter();
		final String bibTeX = e2bConverter.endnoteToBibtex(endnote);
		assertEquals(expectedBibTeX.trim(), bibTeX.trim());
		
		// test canHandle with BibTex
		assertFalse(EndnoteToBibtexConverter.canHandle(expectedBibTeX));
	}
}