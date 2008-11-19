package org.bibsonomy.scraper.url.kde.biomed;

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

/**
 * @author wbi
 * @version $Id$
 */
public class BioMedCentralScraper extends UrlScraper {

	private static final String info = "BioMed Central Scraper: This Scraper parse a publication from " + href("http://www.biomedcentral.com/", "BioMed Central</a>");

	private static final String BIOMEDCENTRAL_HOST  = "biomedcentral.com";
	private static final String BIOMEDCENTRAL_HOST_NAME  = "http://www.biomedcentral.com";
	private static final String BIOMEDCENTRAL_BIBTEX_PATH = "citation";
	private static final String BIOMEDCENTRAL_BIBTEX_PARAMS = "?format=bibtex&include=cit&direct=0&action=submit";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BIOMEDCENTRAL_HOST), UrlScraper.EMPTY_PATTERN));

	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String url = sc.getUrl().toString();

		if(!(url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH + "/") || 
				url.endsWith("/" + BIOMEDCENTRAL_BIBTEX_PATH) ||
				url.endsWith(BIOMEDCENTRAL_BIBTEX_PATH))) {

			if(!url.endsWith("/")) {
				url += "/" + BIOMEDCENTRAL_BIBTEX_PATH;
			} else {
				url += BIOMEDCENTRAL_BIBTEX_PATH;
			}
		}

		if(!url.endsWith("/")) {
			url += "/" + BIOMEDCENTRAL_BIBTEX_PARAMS;
		} else {
			url += BIOMEDCENTRAL_BIBTEX_PARAMS;
		}			

		try {
			sc.setUrl(new URL(url));
		} catch (MalformedURLException ex) {
			throw new InternalFailureException(ex);
		}
		String bibResult = sc.getPageContent();

		if(bibResult != null) {
			sc.setBibtexResult(bibResult);
			return true;
		}
		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
