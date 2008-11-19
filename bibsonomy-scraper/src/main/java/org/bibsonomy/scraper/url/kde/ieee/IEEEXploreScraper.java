
package org.bibsonomy.scraper.url.kde.ieee;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.CompositeScraper;
import org.bibsonomy.scraper.Tuple;


/** General scraper for IEEE Explore
 * @author rja
 *
 */
public class IEEEXploreScraper extends CompositeScraper {
	private static final String info = "IEEEXplore Scraper: This scraper creates a BibTeX entry for the media at " + 
	"<a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a> . Author: KDE";

	private static final String HOST = "ieeexplore.ieee.org";
	private static final String XPLORE_PATH = "/Xplore";
	private static final String SEARCH_PATH = "/search/";

	private static final List<Tuple<Pattern,Pattern>> patterns = new LinkedList<Tuple<Pattern,Pattern>>();

	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(XPLORE_PATH + ".*")));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(SEARCH_PATH + ".*")));
	}
	
	public IEEEXploreScraper() {
		addScraper(new IEEEXploreJournalProceedingsScraper());
		addScraper(new IEEEXploreBookScraper());
		addScraper(new IEEEXploreStandardsScraper());
	}

	public String getInfo() {
		return info;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}