/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for building a list with {@link URLScraperUnitTest}s. 
 * @author tst
 * @version $Id$
 */
public class XMLUnitTestHandler extends DefaultHandler {
	
	private Log log = LogFactory.getLog(XMLUnitTestHandler.class);
	
	private static final String ELEMENT_URL_TEST = "URLTest";
	private static final String ELEMENT_Test_DESCRIPTION = "TestDescription";
	private static final String ELEMENT_URL = "URL";
	private static final String ELEMENT_BIB_FILE = "BibFile";
	private static final String ELEMENT_SCRAPER = "Scraper";
	private static final String ELEMENT_ENABLED = "Enabled";
	private static final Object ELEMENT_SELECTION = "Selection";
	private static final String ATTRIBUTE_ID = "id";
	
	private static final String PATH_TO_BIBS = "org/bibsonomy/scraper/data/";

	private LinkedList<ScraperUnitTest> tests = null;
	
	private URLScraperUnitTest currentTest = null;
	
	private StringBuffer charBuffer = null;
	
	/*
	 * default constructor
	 */
	public XMLUnitTestHandler() {
		super();
		this.tests = new LinkedList<ScraperUnitTest>();
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if(charBuffer != null)
			charBuffer.append(ch, start, length);
	}

	public List<ScraperUnitTest> getTests() {
		return this.tests;
	}

	public void setTests(LinkedList<ScraperUnitTest> tests) {
		this.tests = tests;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		if(localName.equals(ELEMENT_URL_TEST)){
			tests.add(currentTest);
			currentTest = null;
		}else if(localName.equals(ELEMENT_Test_DESCRIPTION)){
			currentTest.setDescription(charBuffer.toString());
			charBuffer = null;
		}else if(localName.equals(ELEMENT_URL)){
			currentTest.setUrl(charBuffer.toString());
			charBuffer = null;
		}else if(localName.equals(ELEMENT_BIB_FILE)){
			currentTest.setBibFile(charBuffer.toString());
			try {
				currentTest.setExpectedRefrence(getExpectedReference(charBuffer.toString()));
			} catch (IOException ex) {
				log.error("Bibtex file " + charBuffer.toString() + " not exist", ex);
				currentTest.setExpectedRefrence(null);
			}
			charBuffer = null;
		} else if (localName.equals(ELEMENT_ENABLED)) {
			if (charBuffer.toString().equals("false")) {
				currentTest.setEnabled(false);
			}
			charBuffer = null;
		} else if (localName.equals(ELEMENT_SELECTION)) {
			currentTest.setSelection(charBuffer.toString());
			charBuffer = null;
		} else if (localName.equals(ELEMENT_SCRAPER)) {
			Scraper scraper;
			try {
				scraper = (Scraper) Class.forName(charBuffer.toString()).newInstance();
				currentTest.setScraper(scraper);
			} catch (InstantiationException ex) {
				log.error("Scraper " + charBuffer.toString() + " in UnitTestData.xml not exist", ex);
				currentTest.setScraper(null);
			} catch (IllegalAccessException ex) {
				log.error("Scraper " + charBuffer.toString() + " in UnitTestData.xml not exist", ex);
				currentTest.setScraper(null);
			} catch (ClassNotFoundException ex) {
				log.error("Scraper " + charBuffer.toString() + " in UnitTestData.xml not exist", ex);
				currentTest.setScraper(null);
			}
			charBuffer = null;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equals(ELEMENT_URL_TEST)) {
			currentTest = new URLScraperUnitTest();
			currentTest.setId(attributes.getValue(ATTRIBUTE_ID));
		} else if (localName.equals(ELEMENT_Test_DESCRIPTION)) {
			charBuffer = new StringBuffer();
		} else if (localName.equals(ELEMENT_URL)) {
			charBuffer = new StringBuffer();
		} else if (localName.equals(ELEMENT_BIB_FILE)) {
			charBuffer = new StringBuffer();
		} else if (localName.equals(ELEMENT_SCRAPER)) {
			charBuffer = new StringBuffer();
		} else if (localName.equals(ELEMENT_ENABLED)) {
			charBuffer = new StringBuffer();
		} else if (localName.equals(ELEMENT_SELECTION)) {
			charBuffer = new StringBuffer();
		}
	}
	
	/**
	 * Loads bib file with expected reference for test case.
	 * @param bibFile Name of the bib file as String
	 * @return Content from bib file as String
	 * @throws IOException
	 */
	private String getExpectedReference(String bibFile) throws IOException{
		final InputStreamReader is = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(PATH_TO_BIBS + bibFile), "UTF-8");
		final StringWriter writer = new StringWriter();
		
		int read = is.read();
		while(read != -1){
			writer.write(read);
			read = is.read();
		}
		
		// clean up
		writer.flush();
		writer.close();
		is.close();
		
		return writer.toString();
	}
	
}
