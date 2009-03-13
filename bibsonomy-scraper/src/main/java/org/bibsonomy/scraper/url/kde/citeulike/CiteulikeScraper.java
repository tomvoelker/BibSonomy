package org.bibsonomy.scraper.url.kde.citeulike;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
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
 * Scraper for citeulike.org
 * @author tst
 * @version $Id$
 */
public class CiteulikeScraper extends AbstractUrlScraper {

	private static final String INFO = "CiteULike Scraper: scrapes publications from " + href("http://www.citeulike.org/", "CiteUlike");

	private static final String HOST = "citeulike.org";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));

	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);

		// build bibtex download URL
		String downloadUrl = sc.getUrl().toString();
		downloadUrl = downloadUrl.replace(HOST, HOST + "/bibtex");

		// download
		String bibtex = null;
		try {
			bibtex = WebUtils.getContentAsString(new URL(downloadUrl));
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		// set result
		if(bibtex != null){
			sc.setBibtexResult(bibtex);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");

	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
