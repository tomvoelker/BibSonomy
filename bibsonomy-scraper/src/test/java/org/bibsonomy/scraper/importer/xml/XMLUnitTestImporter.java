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

import java.util.List;

import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Importer for XML and bib sources.
 * @author tst
 *
 */
public class XMLUnitTestImporter implements IUnitTestImporter {
	
	/**
	 * Name of xml file which contains the test cases.
	 */
	private static final String UNIT_TEST_DATA_XML_FILE_NAME = "UnitTestData.xml";
	

	public List<ScraperUnitTest> getUnitTests() throws Exception{
		final XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader();

		XMLUnitTestHandler handler = new XMLUnitTestHandler(); 
		xmlreader.setContentHandler(handler);
		xmlreader.setEntityResolver(handler);
		xmlreader.setErrorHandler(handler);

		final InputSource is = new InputSource(this.getClass().getResourceAsStream(UNIT_TEST_DATA_XML_FILE_NAME));
		
		xmlreader.parse(is);
		
		return handler.getTests();
	}
	
}
