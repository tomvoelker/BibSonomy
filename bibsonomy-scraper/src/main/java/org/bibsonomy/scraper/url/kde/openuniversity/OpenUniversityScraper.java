package org.bibsonomy.scraper.url.kde.openuniversity;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.generic.SimpleGenericURLScraper;

/**
 * @author hagen
 * @version $Id$
 */
public class OpenUniversityScraper extends SimpleGenericURLScraper {

	private static final String SITE_NAME = "The Open University";
	private static final String SITE_URL = "http://www.open.ac.uk/";
	private static final String INFO = "This scraper parses a publication page of citations from "
			+ href(SITE_URL, SITE_NAME)+".";
	
	private static final String PUBLICATION_HOST = "oro.open.ac.uk";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<Pair<Pattern, Pattern>>();
	
	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?" + PUBLICATION_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	
	private static final Pattern ID_PATTERN = Pattern.compile("/(\\d++)/");
	
	private static final String DOWNLOAD_LINK_PREFIX = "http://oro.open.ac.uk/cgi/export/eprint/";
	private static final String DOWNLOAD_LINK_SUFFIX = "/BibTeX/oro-eprint-19554.bib";

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
		return DOWNLOAD_LINK_PREFIX + m.group(1) + DOWNLOAD_LINK_SUFFIX;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

}
