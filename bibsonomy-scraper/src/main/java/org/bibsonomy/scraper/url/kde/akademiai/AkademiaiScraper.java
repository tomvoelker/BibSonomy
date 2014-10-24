package org.bibsonomy.scraper.url.kde.akademiai;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 *
 * @author Haile
 */
public class AkademiaiScraper extends AbstractUrlScraper{
	private static final Log log = LogFactory.getLog(AkademiaiScraper.class);

	private static final String SITE_NAME = "Akademiai Kiado";
	private static final String SITE_URL = "http://www.akademiai.com/home/main.mpx";
	private static final String INFO =  "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);

	private static final String RIS_URL  = "http://www.akademiai.com/export.mpx?code=";
	private static final Pattern URL_PATTERN = Pattern.compile(".*/content/(.*?)/");

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "akademiai.com"), AbstractUrlScraper.EMPTY_PATTERN));

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#scrapeInternal(org.bibsonomy.scraper.ScrapingContext)
	 */
	private static String ExtractID(String url){
		String id = null;
		Matcher m = URL_PATTERN.matcher(url.toString());
		if(m.find()){
			id = m.group(1);
		}
		return id;
	}
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		String url = sc.getUrl().toString();
		String id = ExtractID(url);
		String ris_url = RIS_URL + id + "&mode=ris";
		String bibtex = null;
		try {
			String  content = WebUtils.getContentAsString(ris_url,WebUtils.getCookies(new URL(url)));
			RisToBibtexConverter rbc = new RisToBibtexConverter();

			bibtex = rbc.risToBibtex(content);

			if(bibtex != null){
				sc.setBibtexResult(bibtex);
				return true;
			}

		} catch (IOException e) {
			log.error("Requested page could not downloaded ", e);
		}

		return false;
	}

}
