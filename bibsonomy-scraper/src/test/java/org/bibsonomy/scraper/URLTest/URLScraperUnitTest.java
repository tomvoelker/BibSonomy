/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.scraper.URLTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestResult;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScraperUnitTest;
import org.bibsonomy.scraper.ScrapingContext;
import org.junit.Ignore;

import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;

/**
 * ScraperUnitTest which represents a URLTest.
 * @author tst
 *
 */
@Ignore
public class URLScraperUnitTest extends ScraperUnitTest {
	
	private static final Logger log = Logger.getLogger(URLScraperUnitTest.class);
	
	/**
	 * Name of test method.
	 */
	private static final String TEST_METHOD = "runUnitTest";

	/*
	 * elements of a URLTest
	 */
	private String description = null;
	private String url = null;
	private String expectedRefrence = null;
	private String scrapedReference = null;
	private String id = null;
	private String bibFile = null;
	private boolean enabled = false;
	private Exception exception = null;
	
	/**
	 * Tells sub which method has to be called for test.
	 *
	 */
	public URLScraperUnitTest(){
		super(TEST_METHOD);
	}
	
	/**
	 * Tells sub which method has to be called for test and init all
	 * URLTest components.
	 * @param url
	 * @param expectedReference
	 * @param scraper
	 * @param description
	 * @param id
	 * @param bibFile
	 */
	public URLScraperUnitTest(String url, String expectedReference, Scraper scraper, String description, String id, String bibFile){
		super(TEST_METHOD);
		this.url = url;
		this.expectedRefrence = expectedReference;
		this.scraper = scraper;
		this.description = description;
		this.id = id;
		this.bibFile = bibFile;
	}
	
	/**
	 * Method which execute the URLTest.
	 *
	 */
	public void runUnitTest(){
		// prepare testURL
		URL testURL = null;
		try {
			testURL = new URL(url);
		} catch (MalformedURLException e) {}
		
		//System.out.println("current test = " + id + " started");
		
		// prepare ScrapingContext with testURL
		ScrapingContext testSC = new ScrapingContext(testURL);
		// check if result is valid bibtex
		boolean bibtexValid = false;
		
		// scrape
		try {
			scraper.scrape(testSC);
			
			/*
			 * finale check if bibtex is valid, if not so
			 */
			if(testSC.getBibtexResult() != null){
				BibtexParser parser = new BibtexParser(true);
				BibtexFile bibtexFile = new BibtexFile();
				BufferedReader sr = new BufferedReader(new StringReader(testSC.getBibtexResult()));
				// parse source
				parser.parse(bibtexFile, sr);
	
				for (Object potentialEntry:bibtexFile.getEntries())
					if ((potentialEntry instanceof BibtexEntry))
						bibtexValid = true;
			}
			
		} catch (ScrapingException e) {
			// store which Exceptions is occured during testing
			exception = e;
		} catch (ParseException ex) {
			// store which Exceptions is occured during testing
			exception = ex;
		} catch (IOException ex) {
			// store which Exceptions is occured during testing
			exception = ex;
		}
		
		// store scrape result
		scrapedReference = testSC.getBibtexResult();
		
		//System.out.println("current test = " + id + " finished");
		// test if expected bib is equal to scraped bib (which must be valid bibtex) 
		assertTrue(expectedRefrence.equals(testSC.getBibtexResult()) && bibtexValid);
	}

	/**
	 * Tested URL
	 * @return testedURL as String
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Scraped result 
	 * @return as String
	 */
	public String getScrapedReference() {
		return scrapedReference;
	}

	/**
	 * Expected bib
	 * @return as String
	 */
	public String getExpectedReference() {
		return expectedRefrence;
	}
	
	/**
	 * test description
	 * @return as String
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Exception which is occured during test
	 * @return occured Exception
	 */
	public Exception getException(){
		return exception;
	}
	
	/**
	 * Generate standard output of this test case. System.out and Logger
	 * is available.
	 * Inherited from ScraperUnitTest
	 */
	public void printTestFailure()throws Exception{
		//printTestFailure(System.out, result);
		printTestFailureLogger();
	}
	
	/**
	 * Output with PrintStream
	 * @param stream
	 * @param result
	 * @throws Exception
	 */
	public void printTestFailure(PrintStream stream) throws Exception{
		if(testResult == null)
			throw new Exception("test result is needed");
		
		if(testResult.errorCount() > 0 || testResult.failureCount() > 0){
			stream.println("*******************************************************************************");
			stream.println("failure in: " + getScraperClass().getName() + "test: " + getScraperTestId());
			stream.println("test description: " + getDescription());
			stream.println("url to tested reference: " + getURL());
			if(getException() != null){
				stream.println("Following exceptions occured:");
				getException().printStackTrace(stream);
			}
			stream.println("expected bibtex:\n" + getExpectedReference());
			stream.println("scraped bibtex:\n" + getScrapedReference());
			stream.println("*******************************************************************************");
		}
	}
	
	/**
	 * Output with Logger
	 * @param result
	 * @throws Exception
	 */
	public void printTestFailureLogger() throws Exception{
		if(testResult == null)
			throw new Exception("test result is needed");
		
		if(testResult.errorCount() > 0 || testResult.failureCount() > 0){
			StringWriter swriter = new StringWriter();
			PrintWriter pwriter = new PrintWriter(swriter, true);
			
			pwriter.println();
			pwriter.println("*******************************************************************************");
			pwriter.println("failure in: " + getScraperClass().getName() + "test: " + getScraperTestId());
			pwriter.println("test description: " + getDescription());
			pwriter.println("url to tested reference: " + getURL());
			if(getException() != null){
				pwriter.println("Following exceptions occured:");
				getException().printStackTrace(pwriter);
			}
			pwriter.println("expected bibtex:\n" + getExpectedReference());
			pwriter.println("scraped bibtex:\n" + getScrapedReference());
			pwriter.println("*******************************************************************************");
			pwriter.flush();
			pwriter.close();
			swriter.flush();

			log.error(swriter.toString());
		}
	}

	@Override
	public String getScraperTestId() {
		return id;
	}

	/**
	 * path to bibtex file
	 * @return
	 */
	public String getBibFile() {
		return this.bibFile;
	}

	/**
	 * test id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * scraping url
	 * @return
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * scraping url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * path to bibtex file
	 */
	public void setBibFile(String bibFile) {
		this.bibFile = bibFile;
	}

	/**
	 * test description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * tested Scraper
	 */
	public void setScraper(Scraper scraper) {
		this.scraper = scraper;
	}

	/**
	 * expected bibtex reference
	 * @return
	 */
	public String getExpectedRefrence() {
		return this.expectedRefrence;
	}

	/**
	 * expected bibtex reference
	 */
	public void setExpectedRefrence(String expectedRefrence) {
		this.expectedRefrence = expectedRefrence;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * 
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * occured exception
	 * @param exception
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	/**
	 * occured exception
	 */
	public void setScrapedReference(String scrapedReference) {
		this.scrapedReference = scrapedReference;
	}
	
}
