package org.bibsonomy.scraper.url.kde.jstage;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Haile
 * @version $Id:$
 */
public class JStageScraper extends PostprocessingGenericURLScraper{
	private static final String SITE_NAME = "J-Stage";
	private static final String SITE_URL = "https://jstage.jst.go.jp";
	private static final String INFO = "Extracts publications from " + href(SITE_URL, SITE_NAME) + 
			". Publications can be entered as a selected BibTeX snippet or by posting the page of the reference.";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "jstage.jst.go.jp"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<p class=\"normal\"\\s*>\\s+<br>\\s+(.*)\\s+</p>");
	
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
	private static String abstractParser(URL url){
		try{
		Matcher m = PATTERN_ABSTRACT.matcher(WebUtils.getContentAsString(url));
		if(m.find())
			return m.group(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.PostprocessingGenericURLScraper#postProcessScrapingResult(org.bibsonomy.scraper.ScrapingContext, java.lang.String)
	 */
	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result) {
		try{
			result = BibTexUtils.addFieldIfNotContained(result, "url", sc.getUrl().toString());
			result = BibTexUtils.addFieldIfNotContained(result, "abstract", abstractParser(sc.getUrl()));
			return StringEscapeUtils.unescapeHtml(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getBibTeXURL(URL url) {
		String[] bibPath = url.getPath().split("/");
		try{
			return "https://" + url.getHost() + "/AF06S010ShoshJkuDld?sryCd=" + bibPath[2] + "&noVol=" + bibPath[3] + "&noIssue=" + bibPath[4] + "&kijiCd=" + bibPath[5] + "&kijiLangKrke=en&kijiToolIdHkwtsh=AT0073";
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
