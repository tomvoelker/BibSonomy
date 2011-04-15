package org.bibsonomy.scraper.url.kde.journalogy;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for Journalogy (Microsoft Academic Search)
 * http://www.journalogy.org
 * 
 * @author clemens
 * @version $Id$
 */
public class JournalogyScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Journalogy (Microsoft Academic Search)";
	private static final String SITE_URL = "http://www.journalogy.org/";
	private static final String info = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";
	
	private static final String HOST = "journalogy.org";
	private static final String HOST2 = "academic.research.microsoft.com";

	private static final List<Tuple<Pattern, Pattern>> patterns = new LinkedList<Tuple<Pattern, Pattern>>();
	static {
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST2), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final Pattern pattern_download = Pattern.compile("/BibTeX.bib?type=2&id=");
	private static final Pattern pattern_id = Pattern.compile("/(Paper|Publication)/([0-9]+)");
	
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			// extract id
			final Matcher idMatcher = pattern_id.matcher(sc.getUrl().toString());
			
			if(idMatcher.find()) {
				String downloadLink = "http://" + HOST2 + pattern_download + idMatcher.group(2);
				String bibtex = WebUtils.getContentAsString(downloadLink);
				if (bibtex != null) {
					// add the missing ","  
					bibtex = bibtex.replaceFirst("\\{", "\\{,");					
					sc.setBibtexResult(bibtex);
					return true;
				}
			} else {
				throw new ScrapingFailureException("No bibtex available.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}
}
