package org.bibsonomy.scraper.url.kde.jstage;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 * @version $Id:$
 */
public class JStageScraper extends AbstractUrlScraper{
	private static final String SITE_NAME = "J-Stage";
	private static final String SITE_URL = "https://jstage.jst.go.jp";
	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + 
			". Publications can be entered as a selected BibTeX snippet or by posting the page of the reference.";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jstage.jst.go.jp"), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		URL url = sc.getUrl();
		String[] bibPath = url.getPath().split("/");
		try{
			String bibtexURL = "https://" +sc.getUrl().getHost() + "/AF06S010ShoshJkuDld?sryCd=" + bibPath[2] + "&noVol=" + bibPath[3] + "&noIssue=" + bibPath[4] + "&kijiCd=" + bibPath[5] + "&kijiLangKrke=en&kijiToolIdHkwtsh=AT0073";
			
			final String bibtex = StringEscapeUtils.unescapeHtml(WebUtils.getContentAsString(BibTexUtils.addFieldIfNotContained(bibtexURL, "url", sc.getUrl().toString())));
			if (present(bibtex)) {
				sc.setBibtexResult(bibtex);
				return true;
			} else {
				throw new ScrapingFailureException("getting bibtex failed");
			}


		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
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
		return URL_PATTERNS;
	}
}
