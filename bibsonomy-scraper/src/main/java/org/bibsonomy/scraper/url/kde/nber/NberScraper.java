package org.bibsonomy.scraper.url.kde.nber;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class NberScraper extends UrlScraper {

	private static final String info = "NBER Scraper: This Scraper parses a publication from " + href("http://www.nber.org/", "National Bureau of Economic Research");

	private static final String NBER_HOST  = "www.nber.org";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + NBER_HOST), UrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		//here we just need to append .bib to the url and we got the bibtex file
		try {
			sc.setUrl(new URL(sc.getUrl().toString() + ".bib"));
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		}

		final String bibResult = sc.getPageContent();

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}else
			throw new ScrapingFailureException("getting bibtex failed");
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {		
		return patterns;
	}

}
