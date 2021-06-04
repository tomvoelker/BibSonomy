package org.bibsonomy.scraper.url.kde.neurips;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * Get BibTeX for NeurIPS Proceedings.
 *
 * @author rja
 */
public class NeurIPSScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Conference on Neural Information Processing Systems";
	private static final String SITE_URL = "https://neurips.cc/";
	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME) + ".";

	private static final Pattern HOST_PATTERN = Pattern.compile(".*" + "neurips.cc");
	private static final Pattern PATH_PATTERN = Pattern.compile("^" + "/paper.*$");
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(HOST_PATTERN, PATH_PATTERN));

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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		// https://proceedings.neurips.cc/paper/2016/file/90e1357833654983612fb05e3ec9148c-Reviews.html or
		// https://proceedings.neurips.cc/paper/2016/hash/90e1357833654983612fb05e3ec9148c-Abstract.html →
		// https://proceedings.neurips.cc/paper/2016/file/90e1357833654983612fb05e3ec9148c-Bibtex.bib
		final String downloadUrl = url.toString();
		// replace "hash" with "file"
		return downloadUrl
				.replace("/hash/", "/file/")
				.replace("Reviews.html", "Bibtex.bib")
				.replace("Abstract.html", "Bibtex.bib");
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result){
		 return BibTexUtils.addFieldIfNotContained(result, "url", sc.getUrl().toString());
	}
}
