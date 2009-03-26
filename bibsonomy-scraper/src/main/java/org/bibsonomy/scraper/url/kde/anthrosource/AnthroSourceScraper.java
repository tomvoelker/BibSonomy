package org.bibsonomy.scraper.url.kde.anthrosource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class AnthroSourceScraper extends AbstractUrlScraper {
	
	private Logger log = Logger.getLogger(AnthroSourceScraper.class);

	private static final String info = "AnthroSource Scraper: This Scraper parses a publication from " + href("http://www.anthrosource.net/", "anthrosource");

	private static final String AS_HOST  = "anthrosource.net";
	private static final String AS_ABSTRACT_PATH = "/doi/abs/";
	private static final String AS_BIBTEX_PATH = "/action/showCitFormats";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		final Pattern hostPattern = Pattern.compile(".*" + AS_HOST);
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(AS_ABSTRACT_PATH + ".*")));
		patterns.add(new Tuple<Pattern, Pattern>(hostPattern, Pattern.compile(AS_BIBTEX_PATH + ".*")));
	}
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		log.debug("Observed Scraper called: AnthroSourceScraper is called with " + sc.getUrl().toString());
		
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
