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

import java.io.IOException;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class OAIConverterTest {

	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	@Test
	public void testConvert1() throws Exception {
		this.testFile("arxiv1");
	}

	@Test
	public void testConvert2() throws Exception {
		this.testFile("arxiv2");
	}
	

	private void testFile(final String fileName) throws IOException, ScrapingException {
		final String xml = TestUtils.readEntryFromFile(OAIConverterTest.PATH_TO_FILES + fileName + ".xml");
		final String bib = TestUtils.readEntryFromFile(OAIConverterTest.PATH_TO_FILES + fileName + ".bib");
		
		assertEquals(bib.trim(), OAIConverter.convert(xml).trim());
	}
}
