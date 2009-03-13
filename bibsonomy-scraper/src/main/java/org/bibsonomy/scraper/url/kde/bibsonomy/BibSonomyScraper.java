package org.bibsonomy.scraper.url.kde.bibsonomy;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for single publications from bibsonomy.org.
 * 
 * @author tst
 *
 */
public class BibSonomyScraper extends AbstractUrlScraper {

	private static final String INFO = "BibSonomyScraper: If you don't like the copy button from " + href("http://www.bibsonomy.org", "BibSonomy") + ", use your postPublication button.";

	private static final String BIBSONOMY_HOST = "bibsonomy.org";
	private static final String BIBSONOMY_BIB_PATH = "/bib/bibtex";
	private static final String BIBSONOMY_BIBTEX_PATH = "/bibtex";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();
	
	static {
		final Pattern hostPattern = Pattern.compile(".*" + BIBSONOMY_HOST);
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(BIBSONOMY_BIB_PATH + ".*")));
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(BIBSONOMY_BIBTEX_PATH + ".*")));
	}
	/**
	 * Scrapes only single publications from bibsonomy.org/bibtex and bibsonomy.org/bib/bibtex 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {

			String bibResult = null;

			// if /bibtex page then change path from /bibtex to /bib/bibtex and download
			if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIBTEX_PATH)){
				String url = sc.getUrl().toString();
				url = url.replace(BIBSONOMY_BIBTEX_PATH, BIBSONOMY_BIB_PATH);
				bibResult = WebUtils.getContentAsString(new URL(url));

				// if /bib/bibtex page then download directly
			}else if(sc.getUrl().getPath().startsWith(BIBSONOMY_BIB_PATH)){
				bibResult = sc.getPageContent();
			}

			if(bibResult != null){
				sc.setBibtexResult(bibResult);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
