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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.InputStream;
import java.util.Map;

import org.bibsonomy.scraper.ScraperTestData;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Importer for XML and bib sources.
 * @author tst
 */
public class XMLUnitTestImporter implements IUnitTestImporter {
	
	/**
	 * Name of xml file which contains the test cases.
	 */
	private static final String UNIT_TEST_DATA_XML_FILE_NAME = "UnitTestData.xml";
	
	private Map<String, ScraperTestData> unitTests;
	

	@Override
	public Map<String, ScraperTestData> getUnitTests() throws Exception{
		if(!present(this.unitTests)) {
			this.initUnitTests();
		}
		return this.unitTests;
	}
	
	/**
	 * Helper method whcih initializes the Unit tests if they are not already loaded.
	 * 
	 * @throws Exception
	 */
	private void initUnitTests() throws Exception {
		final XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader();

		final XMLUnitTestHandler handler = new XMLUnitTestHandler(); 
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		InputStream in = null;
		try {
			in = this.getClass().getResourceAsStream(UNIT_TEST_DATA_XML_FILE_NAME);
			final InputSource is = new InputSource(in);
			
			xmlreader.parse(is);
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		this.unitTests = handler.getTestData();
	}
	
}
