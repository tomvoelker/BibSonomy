package org.bibsonomy.scraper.url.kde.aaai;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.BibtexScraper;

/**
 * @author hagen
 * @version $Id$
 */
public class AAAIScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Association for the Advancement of Artificial Intelligence";

	private static final String SITE_URL = "http://www.aaai.org/";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String PAPER_VIEW_PATH_FRAGMENT = "paper/view";
	private static final String PAPER_DOWNLOAD_PATH_FRAGMENT = "rt/captureCite";
	private static final String PAPER_DOWNLOAD_PATH_SUFFIX = "/0/BibtexCitationPlugin";
	
	private static final List<Pair<Pattern,Pattern>> PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?www.aaai.org"), Pattern.compile(PAPER_VIEW_PATH_FRAGMENT)));
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		String downloadLink = scrapingContext.getUrl().toExternalForm().replace(PAPER_VIEW_PATH_FRAGMENT, PAPER_DOWNLOAD_PATH_FRAGMENT);
		downloadLink += PAPER_DOWNLOAD_PATH_SUFFIX;
		ScrapingContext bibContext;
		try {
			bibContext = new ScrapingContext(new URL(downloadLink));
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		if (new BibtexScraper().scrape(bibContext)) {
			String bibtexResult = bibContext.getBibtexResult();
			
			//replace entry type paper by inproceedings
			//FIXME: are all those publications inproceedings?
			bibtexResult = bibtexResult.replace("@paper", "@inproceedings");
			
			scrapingContext.setBibtexResult(bibtexResult);
			return true;
		}
		return false;
	}

}
