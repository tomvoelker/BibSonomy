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

import java.io.IOException;

import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class PicaToBibtexConverterTest {
	
	private static final String PATH_TO_FILES = "org/bibsonomy/scraper/converter/";
	
	@Test
	public void testGetBibResult1() throws IOException {
		this.testFile("opac1", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=273285416");
	}

	@Test
	public void testGetBibResult2() throws IOException {
		this.testFile("opac2", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=231779038");
	}

	@Test
	public void testGetBibResult3() throws IOException {
		this.testFile("opac3", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=185335748");
	}

	@Test
	public void testGetBibResult4() throws IOException {
		this.testFile("opac4", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=098898043");
	}
	
	@Test
	public void testGetBibResult5() throws IOException {
		this.testFile("opac5", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=178208876");
	}
	
	/**
	 * Has ISBN in another field
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetBibResult6() throws IOException {
		this.testFile("opac6", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=118339710");
	}

	@Test
	public void testGetBibResult7() throws IOException {
		this.testFile("opac7", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=184916631");
	}

	
	@Test
	public void testGetBibResult8() throws IOException {
		this.testFile("opac8", "http://opac.bibliothek.uni-kassel.de/DB=1/XML=1.0/CHARSET=UTF-8/PRS=PP/PPN?PPN=00078091X");
	}
	
	private void testFile(final String fileName, final String url) throws IOException {
		final String xml = TestUtils.readEntryFromFile(PATH_TO_FILES + fileName + ".xml");
		final String bib = TestUtils.readEntryFromFile(PATH_TO_FILES + fileName + ".bib");
		
		final PicaToBibtexConverter pica = new PicaToBibtexConverter(xml, "xml", url);
		
		assertEquals(bib.trim(), pica.getBibResult().trim());
	}
}
