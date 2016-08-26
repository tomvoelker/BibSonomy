package org.bibsonomy.scraper.junit;

import static org.bibsonomy.util.ValidationUtils.present;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.util.StringUtils;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;

/**
 * asserts for remote tests
 *
 * @author dzo
 */
public class RemoteTestAssert {
	
	/**
	 * calls the specified scraper with the 
	 * @param url
	 * @param selection
	 * @param scraperClass
	 * @param resultFile
	 */
	public static void assertScraperResult(final String url, final String selection, final Class<? extends Scraper> scraperClass, final String resultFile) {
		try {
			final Scraper scraper = createScraper(scraperClass);
			final ScrapingContext scrapingContext = createScraperContext(url, selection);
			scraper.scrape(scrapingContext);
			final String bibTeXResult = scrapingContext.getBibtexResult();
			/*
			 * final check if bibtex is valid, if not so
			 */
			boolean bibtexValid = false;
			
			if (bibTeXResult != null){
				final BibtexParser parser = new BibtexParser(true);
				final BibtexFile bibtexFile = new BibtexFile();
				final BufferedReader sr = new BufferedReader(new StringReader(bibTeXResult));
				// parse source
				parser.parse(bibtexFile, sr);
				
				for (final BibtexAbstractEntry potentialEntry : bibtexFile.getEntries())
					if ((potentialEntry instanceof BibtexEntry))
						bibtexValid = true;
				// test if expected bib is equal to scraped bib (which must be valid bibtex) 
				assertTrue("scraped BibTeX not valid", bibtexValid);
				final String expectedRefrence = getExpectedBibTeX(resultFile).trim();
				assertEquals(expectedRefrence, bibTeXResult.trim());
			} else {
				fail("nothing scraped");
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param resultFile
	 * @return
	 */
	private static String getExpectedBibTeX(String resultFile) throws IOException {
		try (final InputStream in = RemoteTestAssert.class.getClassLoader().getResourceAsStream("org/bibsonomy/scraper/data/" + resultFile)) {
			return StringUtils.getStringFromReader(new BufferedReader(new InputStreamReader(in, StringUtils.DEFAULT_CHARSET)));
		}
	}

	private static Scraper createScraper(final Class<? extends Scraper> scraperClass) throws InstantiationException, IllegalAccessException {
		return scraperClass.newInstance();
	}
	
	private static ScrapingContext createScraperContext(final String url, final String selection) throws MalformedURLException {
		final URL testURL;
		if (present(url)) {
			testURL = new URL(url);
		} else {
			testURL = null;
		}
		final ScrapingContext testSC = new ScrapingContext(testURL);
		if (selection != null) {
			testSC.setSelectedText(selection);
		}
		return testSC;
	}
}
