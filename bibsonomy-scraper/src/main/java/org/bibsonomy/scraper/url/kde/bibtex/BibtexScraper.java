package org.bibsonomy.scraper.url.kde.bibtex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;

/**
 * Search in sourcecode from the given page for bibtex and scrape it.
 * @author tst
 * @version $Id$
 */
public class BibtexScraper implements Scraper {
	
	private static final String INFO = "Scraper for bibtex, independent from URL";

	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)throws ScrapingException {
		if(sc != null && sc.getUrl() != null){
			String source = sc.getPageContent();
			
			// html clean up
			source = StringEscapeUtils.unescapeHtml(source);
			source = source.replace("<br/>", "\n");
			// TODO: may be some other format elements like <i>, <p> etc. are still in code
			
			try {
				
				/* 
				 * copy from SnippetScraper
				 */
				BibtexParser parser = new BibtexParser(true);
				BibtexFile bibtexFile = new BibtexFile();
				BufferedReader sr = new BufferedReader(new StringReader(source));
				// parse source
				parser.parse(bibtexFile, sr);

				for (Object potentialEntry:bibtexFile.getEntries()) {
					if ((potentialEntry instanceof BibtexEntry)) {
						sc.setBibtexResult(potentialEntry.toString());
						return true; 
					}
				}
			} catch (ParseException ex) {
				throw new InternalFailureException(ex);
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}
		return false;
	}

}
