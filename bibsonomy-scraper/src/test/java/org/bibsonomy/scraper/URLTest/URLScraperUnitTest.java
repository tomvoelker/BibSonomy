package org.bibsonomy.scraper.URLTest;

import java.io.PrintStream;
import java.io.PrintWriter;
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
import org.bibsonomy.scraper.exceptions.ScrapingException;

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
	private Scraper scraper = null;
	private String id = null;
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
	 */
	public URLScraperUnitTest(String url, String expectedReference, Scraper scraper, String description, String id){
		super(TEST_METHOD);
		this.url = url;
		this.expectedRefrence = expectedReference;
		this.scraper = scraper;
		this.description = description;
		this.id = id;
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
		
		// prepare ScrapingContext with testURL
		ScrapingContext testSC = new ScrapingContext(testURL);
		
		// scrape
		try {
			scraper.scrape(testSC);
		} catch (ScrapingException e) {
			// store which Exceptions is occured during testing
			exception = e;
		}
		
		// store scrape result
		scrapedReference = testSC.getBibtexResult();
		
		// test if expected bib is equal to scraped bib
		assertTrue(expectedRefrence.equals(testSC.getBibtexResult()));
	}

	/**
	 * Class of the tested Scraper
	 */
	public Class getScraperClass() {
		return scraper.getClass();
	}

	public Scraper getScraper() {
		return this.scraper;
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
	public void printTestFailure(TestResult result)throws Exception{
		//printTestFailure(System.out, result);
		printTestFailureLogger(result);
	}
	
	/**
	 * Output with PrintStream
	 * @param stream
	 * @param result
	 * @throws Exception
	 */
	public void printTestFailure(PrintStream stream, TestResult result) throws Exception{
		if(result == null)
			throw new Exception("test result is needed");
		
		if(result.errorCount() > 0 || result.failureCount() > 0){
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
	public void printTestFailureLogger(TestResult result) throws Exception{
		if(result == null)
			throw new Exception("test result is needed");
		
		if(result.errorCount() > 0 || result.failureCount() > 0){
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
}
