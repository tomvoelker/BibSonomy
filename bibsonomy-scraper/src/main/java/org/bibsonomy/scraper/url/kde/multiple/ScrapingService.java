package org.bibsonomy.scraper.url.kde.multiple;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

public class ScrapingService implements Scraper {

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


	public boolean scrape(ScrapingContext sc) throws ScrapingException {
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

}
