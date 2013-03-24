package org.bibsonomy.scraper.url.kde.jcb;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.SimpleGenericURLScraper;

/**
 * @author hagen
 * @version $Id$
 */
public class JCBScraper extends SimpleGenericURLScraper {

	private static final String SITE_NAME = "JCB";
	private static final String SITE_URL = "http://jcb.rupress.org/";
	private static final String INFO = "This Scraper parses a publication from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final List<Pair<Pattern,Pattern>> PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();
	
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile("jcb.rupress.org"), Pattern.compile("content")));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile("(/\\d++){3}");

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
	public String getBibTeXURL(URL url) {
		Matcher m = ID_PATTERN.matcher(url.toExternalForm());
		if (!m.find()) return null;
		String result = null;
		try {
			result = URLEncoder.encode(m.group().replaceFirst("/", ";"), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
		return "http://jcb.rupress.org/citmgr?type=bibtex&gca=jcb" + result;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
