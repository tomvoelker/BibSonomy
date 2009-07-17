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

package org.bibsonomy.scraper;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.URLTest.URLScraperUnitTest;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.scraper.generic.CoinsScraper;
import org.bibsonomy.scraper.generic.HighwireScraper;
import org.bibsonomy.scraper.generic.UnAPIScraper;
import org.bibsonomy.scraper.id.kde.isbn.ISBNScraper;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;
import org.bibsonomy.scraper.snippet.SnippetScraper;

/**
 * Runner for reachability test for Scraper
 * @author tst
 * @version $Id$
 */
public class ReachabilityTestRunner {
	
	private static final Logger log = Logger.getLogger(ReachabilityTestRunner.class);
	
	/**
	 * Importer which reads the tests from a external sources.
	 */
	private IUnitTestImporter importer = null;
	
	/**
	 * Init Importer
	 */
	public ReachabilityTestRunner(){
		// importer for xml + bib file sources
		importer = new XMLUnitTestImporter();
	}
	
	/**
	 * This Method reads and runs the test.
	 */
	public void run(){
		URL log4j = new UnitTestRunner().getClass().getResource("log4j.properties");
		PropertyConfigurator.configure(log4j);
		try {
			if(importer == null)
				throw new Exception("no UnitTestImporter available");
			
			List<ScraperUnitTest> unitTests = importer.getUnitTests();
			
			Collection<Scraper> compositeScrapers = new KDEScraperFactory().getScraper().getScraper();
			
			// check UrlScraper
			for(ScraperUnitTest test : unitTests){
				URLScraperUnitTest urlTest = (URLScraperUnitTest) test;
				
				Scraper testScraper = urlTest.getScraper();
				
				ScrapingContext context = new ScrapingContext(new URL(urlTest.getURL()));
				
				checkScraper(compositeScrapers, context, testScraper);
			}
			
			// check UnAPIScraper
			checkScraper(compositeScrapers, UnAPIScraper.getTestContext(), new UnAPIScraper());
			
			// check BibtexScraper
			checkScraper(compositeScrapers, BibtexScraper.getTestContext(), new BibtexScraper());

			// check CoinsScraper
			checkScraper(compositeScrapers, CoinsScraper.getTestContext(), new CoinsScraper());

			// check SnippetScraper
			checkScraper(compositeScrapers, SnippetScraper.getTestContext(), new SnippetScraper());

			// check ISBNScraper
			checkScraper(compositeScrapers, ISBNScraper.getTestContext(), new ISBNScraper());

			// check IEScraper
			checkScraper(compositeScrapers, IEScraper.getTestContext(), new IEScraper());

			// check HighwireScraper
			checkScraper(compositeScrapers, HighwireScraper.getTestContext(), new HighwireScraper());

		} catch (Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
		}
	}
	
	private void checkScraper(Collection<Scraper> compositeScrapers, ScrapingContext context, Scraper testScraper){
		Scraper foundScraper = null;
		for(Scraper scraper: compositeScrapers){
			if(scraper.supportsScrapingContext(context)){
				foundScraper = scraper;
				if(!scraper.getClass().getCanonicalName().equals(testScraper.getClass().getCanonicalName())){
					log.debug("not expected scraper found:" + scraper.getClass().getCanonicalName() + " expected scraper:" + testScraper.getClass().getCanonicalName());
				}
				break;
			}
		}
		
		if(foundScraper == null)
			log.debug("not supported reachability test: " + testScraper.getClass().getCanonicalName());
	}

	/**
	 * starts the whole party
	 * @param args not needed
	 */
	public static void main(String[] args){
		new ReachabilityTestRunner().run();
	}

}
