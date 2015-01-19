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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.ScraperTestData;
import org.bibsonomy.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for building a list with {@link URLScraperUnitTest}s. 
 * @author tst
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

	private Map<String, ScraperTestData> testData = null;
	
	private ScraperTestData currentTestData = null;
	
	private StringBuffer charBuffer = null;
	
	/**
	 * default constructor
	 */
	public XMLUnitTestHandler() {
		super();
		this.testData = new HashMap<String, ScraperTestData>();
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (charBuffer != null)
			charBuffer.append(ch, start, length);
	}
	
	/**
	 * @return a map containing all tests
	 */
	public Map<String, ScraperTestData> getTestData() {
		return this.testData;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (localName.equals(ELEMENT_URL_TEST)) {
			testData.put(currentTestData.getTestId(), currentTestData);
			currentTestData = null;
		} else if(localName.equals(ELEMENT_Test_DESCRIPTION)){
			currentTestData.setDescription(charBuffer.toString());
			charBuffer = null;
		} else if(localName.equals(ELEMENT_URL)){
			currentTestData.setUrl(charBuffer.toString());
			charBuffer = null;
		} else if (localName.equals(ELEMENT_BIB_FILE)){
			try {
				currentTestData.setExpectedBibTeX(getExpectedReference(charBuffer.toString()));
			} catch (IOException ex) {
				log.error("Bibtex file " + charBuffer.toString() + " not exist", ex);
				currentTestData.setExpectedBibTeX(null);
			}
			charBuffer = null;
		} else if (localName.equals(ELEMENT_ENABLED)) {
			if (charBuffer.toString().equals("false")) {
				currentTestData.setEnabled(false);
			}
			charBuffer = null;
		} else if (localName.equals(ELEMENT_SELECTION)) {
			currentTestData.setSelection(charBuffer.toString());
			charBuffer = null;
		} else if (localName.equals(ELEMENT_SCRAPER)) {
			currentTestData.setScraperClassName(charBuffer.toString());
			charBuffer = null;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equals(ELEMENT_URL_TEST)) {
			currentTestData = new ScraperTestData();
			currentTestData.setTestId(attributes.getValue(ATTRIBUTE_ID));
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
		final InputStreamReader is = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(PATH_TO_BIBS + bibFile), StringUtils.CHARSET_UTF_8);
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
