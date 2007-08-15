package scraper.url.kde.multiple;

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

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

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
				}
			} catch (MalformedURLException e) {
				log.fatal(e);
			} catch (UnsupportedEncodingException e) {
				log.fatal(e);
			} catch (ScrapingException e) {
				log.fatal(e);
			}
		}
		return false;
	}

}
