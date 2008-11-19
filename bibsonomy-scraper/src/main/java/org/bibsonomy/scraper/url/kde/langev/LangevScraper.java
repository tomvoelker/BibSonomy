package org.bibsonomy.scraper.url.kde.langev;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;

/**
 * @author wbi
 * @version $Id$
 */
public class LangevScraper implements Scraper, UrlScraper {

	private static final String info = "ISRL Scraper: This scraper parses a publication page from the <a href=\"http://www.isrl.uiuc.edu/\">The Graduate School of Library and Information Science at the University of Illinois</a> " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String ISRL_HOST  = "isrl.uiuc.edu";
	private static final String ISRL_HOST_NAME  = "http://www.isrl.uiuc.edu";
	private static final Pattern ISRL_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);
	
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
				sc.setScraper(this);
				
				final String url = sc.getUrl().toString();
				
				final Matcher m = ISRL_PATTERN.matcher(sc.getPageContent());	
				if (m.matches()) {
					sc.setBibtexResult(m.group(1));
					return true;
				}else
					throw new PageNotSupportedException("no bibtex snippet found");
		}
		return false;
	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + ISRL_HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
