package org.bibsonomy.scraper.url.kde.langev;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author wbi
 * @version $Id$
 */
public class LangevScraper extends UrlScraper {

	private static final String info = "ISRL Scraper: This scraper parses a publication page from the " + href("http://www.isrl.uiuc.edu/", "The Graduate School of Library and Information Science at the University of Illinois");
	
	private static final String ISRL_HOST  = "isrl.uiuc.edu";
	private static final Pattern ISRL_PATTERN = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + ISRL_HOST), UrlScraper.EMPTY_PATTERN));

	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		final Matcher m = ISRL_PATTERN.matcher(sc.getPageContent());	
		if (m.matches()) {
			sc.setBibtexResult(m.group(1));
			return true;
		}else
			throw new PageNotSupportedException("no bibtex snippet found");
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
