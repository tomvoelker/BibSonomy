package org.bibsonomy.scraper.url.kde.citeseer;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;


/** Scraper for CiteSeer
 * @author rja
 *
 */
public class CiteseerBasicScraper extends UrlScraper {

	private static final String info = "CiteSeer Scraper: This scraper parses a publication page from the " +
	"Scientific Literature Digital Library " + href("http://citeseer.ist.psu.edu/", "CiteSeer");

	private static final String  CS_HOST_NAME   = "citeseer.ist.psu.edu";
	private static final Pattern bibPattern = Pattern.compile(".*<pre>\\s*(@[A-Za-z]+\\s*\\{.+?\\})\\s*</pre>.*", Pattern.MULTILINE | Pattern.DOTALL);
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + CS_HOST_NAME), UrlScraper.EMPTY_PATTERN));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException{
		sc.setScraper(this);
		
		final Matcher m = bibPattern.matcher(sc.getPageContent());	
		if (m.matches()) {
			sc.setBibtexResult(m.group(1));
			return true;
		}else
			throw new PageNotSupportedException("no bibtex snippet available");
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
