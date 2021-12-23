package org.bibsonomy.scraper.url.kde.apsphysics;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APSPhysicsScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "APS Physics";
	private static final String SITE_URL = "https://journals.aps.org/";
	private static final String SITE_HOST = "journals.aps.org";
	private static final String INFO = "For selected BibTeX snippets and articles from " + href(SITE_URL , SITE_NAME)+".";
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<meta content=\"([^<]*?)\" name=\"description\" />");


	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String urlPath = url.getPath();
		urlPath = urlPath.replaceAll("abstract|references|pdf|cited-by|supplemental", "export");
		return "https://"+ url.getHost() +"/" + urlPath + "?type=bibtex&download=true";
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String bibtex) {
		try {
			String pageContent = WebUtils.getContentAsString(sc.getUrl().toString().replaceAll("references|pdf|cited-by|supplemental|export", "abstract"));
			Matcher m_abstract = PATTERN_ABSTRACT.matcher(pageContent);
			if (m_abstract.find()){
				String abstractOfBibtex = m_abstract.group(1);
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractOfBibtex);
			}
		} catch (IOException ignored) {}
		return bibtex;
	}



}
