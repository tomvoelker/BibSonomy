package org.bibsonomy.scraper.generic;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
@Deprecated
public class ScrapingService extends UrlScraper {

	private static final Logger log = Logger.getLogger(ScrapingService.class);
	private static String baseurl = "";
	private final static String info = "This scraper handles several URLs by forwarding them to an external service.";
	
	static {
		try {
			baseurl = ((String) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("scrapingServiceURL"));
		} catch (NamingException e) {
			log.fatal(e);
		}
	}
	
	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null) {
			try {
				log.debug("calling external service with url " + sc.getUrl());
				URL url = new URL (baseurl + "?url=" + URLEncoder.encode(sc.getUrl().toString(), "UTF-8"));
				log.debug("calling external service " + url);
				String content = sc.getContentAsString(url);
				if (content != null && content.startsWith("% ConnoteaScraper")) {
					log.debug("got content");
					sc.setBibtexResult(content);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);

					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			} catch (UnsupportedEncodingException e) {
				throw new InternalFailureException(e);
			}
		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(UrlScraper.EMPTY_PATTERN, UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		// match every url
		return true;
	}

	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		// return false, this scraper is deprecated
		return false;
	}
	
}
