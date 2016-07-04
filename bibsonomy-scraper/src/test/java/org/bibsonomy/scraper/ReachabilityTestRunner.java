/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper;

import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.InformationExtraction.IEScraper;
import org.bibsonomy.scraper.generic.BibtexScraper;
import org.bibsonomy.scraper.generic.CoinsScraper;
import org.bibsonomy.scraper.generic.HighwireScraper;
import org.bibsonomy.scraper.generic.UnAPIScraper;
import org.bibsonomy.scraper.id.kde.isbn.ISBNScraper;
import org.bibsonomy.scraper.importer.IUnitTestImporter;
import org.bibsonomy.scraper.importer.xml.XMLUnitTestImporter;
import org.bibsonomy.scraper.junit.RemoteTest;
import org.bibsonomy.scraper.snippet.SnippetScraper;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * TODO: create tests based on the test data
 * Runner for reachability test for Scraper
 * 
 * @author tst
 */
@Category(RemoteTest.class) // TODO: check
@Ignore // TODO: do we want to run it every time?
public class ReachabilityTestRunner {
	private static final Log log = LogFactory.getLog(ReachabilityTestRunner.class);
	
	/** test context for the {@link ISBNScraper} */
	public static final ScrapingContext ISBN_SCRAPER_TEST_CONTEXT = new ScrapingContext(null, "9783608935448");

	/** test context for the {@link IEScraper} */
	public static final ScrapingContext IE_SCRAPER_TEST_CONTEXT = new ScrapingContext(null, "Michael May and Bettina Berendt and Antoine Cornuejols and Joao Gama and Fosca Giannotti and Andreas Hotho and Donato Malerba and Ernestina Menesalvas and Katharina Morik and Rasmus Pedersen and Lorenza Saitta and Yucel Saygin and Assaf Schuster and Koen Vanhoof. Research Challenges in Ubiquitous Knowledge Discovery. Next Generation of Data Mining (Chapman & Hall/Crc Data Mining and Knowledge Discovery Series), Chapman & Hall/CRC,2008.");

	
	/**
	 * Importer which reads the tests from a external sources.
	 */
	private IUnitTestImporter importer = null;
	
	/**
	 * Init Importer
	 */
	public ReachabilityTestRunner(){
		// importer for xml + bib file sources
		this.importer = new XMLUnitTestImporter();
	}
	
	/**
	 * This Method reads and runs the test.
	 */
	@Test
	public void run(){
		try {
			if(this.importer == null) {
				throw new Exception("no UnitTestImporter available");
			}
			
			final Collection<ScraperTestData> unitTests = this.importer.getUnitTests().values();
			final Collection<Scraper> compositeScrapers = new KDEScraperFactory().getScraper().getScraper();
			
			// check UrlScraper
			for (final ScraperTestData testData : unitTests) {
				final ScrapingContext context = UnitTestRunner.createScraperContext(testData);
				final Scraper testScraper = UnitTestRunner.createScraper(testData);
				checkScraper(compositeScrapers, context, testScraper);
			}
			
			// check UnAPIScraper
			checkScraper(compositeScrapers, new ScrapingContext(new URL("http://canarydatabase.org/record/488")), new UnAPIScraper());
			
			// check BibtexScraper
			checkScraper(compositeScrapers, new ScrapingContext(new URL("http://de.wikipedia.org/wiki/BibTeX")), new BibtexScraper());

			// check CoinsScraper
			checkScraper(compositeScrapers, new ScrapingContext(new URL("http://www.westmidlandbirdclub.com/bibliography/NBotWM.htm")), new CoinsScraper());

			// check SnippetScraper
			checkScraper(compositeScrapers, new ScrapingContext(null, " @techreport{triple/Store/Report,\n" +
					"title = {Scalability report on triple store applications},\n" +
					"author = {Ryan Lee},\n" +
					"institution = {Massachusetts Institute of Technology},\n" +
					"url = {http://simile.mit.edu/reports/stores/index.html},\n" +
					"year = {2004},\n" +
					"abstract = {This report examines a set of open source triple store systems suitable for The SIMILE Project's browser-like applications. Measurements on performance within a common hardware, software, and dataset environment grant insight on which systems hold the most promise for acting as large, remote backing stores for SIMILE's future requirements. The SIMILE Project (Semantic Interoperability of Metadata In like and Unlike Environments) is a joint research project between the World Wide Web Consortium (W3C), Hewlett-Packard Labs (HP), the Massachusetts Institute of Technology / Computer Science and Artificial Intelligence Laboratory (MIT / CSAIL), and MIT Libraries. Funding is provided by HP.},\n" +
					"keywords = {MIT applications kde performance performance-project rdf report ss07 store triple uni }\n" +
					"}"), new SnippetScraper());

			// check ISBNScraper
			checkScraper(compositeScrapers, ISBN_SCRAPER_TEST_CONTEXT, new ISBNScraper());

			// check IEScraper
			checkScraper(compositeScrapers, IE_SCRAPER_TEST_CONTEXT, new IEScraper());

			// check HighwireScraper
			checkScraper(compositeScrapers, new ScrapingContext(TestUtils.createURL("http://mend.endojournals.org/cgi/gca?sendit=Get+All+Checked+Abstract(s)&gca=17%2F1%2F1")), new HighwireScraper());

		} catch (final Exception e) {
			ParseFailureMessage.printParseFailureMessage(e, "main class");
			fail();
		}
	}
	
	private static void checkScraper(final Collection<Scraper> compositeScrapers, final ScrapingContext context, final Scraper testScraper){
		Scraper foundScraper = null;
		for (final Scraper scraper: compositeScrapers){
			if (scraper.supportsScrapingContext(context)){
				foundScraper = scraper;
				if (!scraper.getClass().getCanonicalName().equals(testScraper.getClass().getCanonicalName())){
					log.debug("not expected scraper found:" + scraper.getClass().getCanonicalName() + " expected scraper:" + testScraper.getClass().getCanonicalName());
				}
				break;
			}
		}
		
		if (foundScraper == null) {
			log.debug("not supported reachability test: " + testScraper.getClass().getCanonicalName());
		}
	}

}
