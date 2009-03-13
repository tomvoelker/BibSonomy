package org.bibsonomy.scraper.url.kde.bmj;

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
import org.bibsonomy.util.WebUtils;

/**
 * @author wbi
 * @version $Id$
 */
public class BMJScraper extends AbstractUrlScraper {

	private static final String info = "BMJ Scraper: This Scraper parses a publication from " + href("http://www.bmj.com/", "BMJ");

	private static final String BMJ_HOST  = "bmj.com";
	private static final String BMJ_HOST_NAME  = "http://www.bmj.com";
	private static final String BMJ_ABSTRACT_PATH = "/cgi/content/full/";
	private static final String BMJ_BIBTEX_PATH = "/cgi/citmgr?gca=";
	private static final String BMJ_BIBTEX_DOWNLOAD_PATH = "/cgi/citmgr?type=bibtex&gca=";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + BMJ_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	public String getInfo() {
		return info;
	}

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String url = sc.getUrl().toString();
		String id = null;

		if(url.startsWith(BMJ_HOST_NAME + BMJ_ABSTRACT_PATH)) {
			id = "bmj;" + url.substring(url.indexOf("/full/") + 6);
		}

		if(url.startsWith(BMJ_HOST_NAME + BMJ_BIBTEX_PATH)) {
			id = url.substring(url.indexOf(BMJ_BIBTEX_PATH) + BMJ_BIBTEX_PATH.length());
		}

		try {
			final String bibResult = WebUtils.getContentAsString(new URL(BMJ_HOST_NAME + BMJ_BIBTEX_DOWNLOAD_PATH + id)).trim().replaceFirst(" ", "");
			if (bibResult != null) {
				sc.setBibtexResult(bibResult);
				return true;
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		return false;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
