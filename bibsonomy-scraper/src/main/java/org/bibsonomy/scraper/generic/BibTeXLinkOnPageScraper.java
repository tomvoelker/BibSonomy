package org.bibsonomy.scraper.generic;

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a scraper that uses a regex to find the a bibtex link on the page
 *
 * @author dzo
 */
public abstract class BibTeXLinkOnPageScraper extends GenericBibTeXURLScraper {

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");

	@Override
	protected String getDownloadURL(final URL url, final String cookies) throws ScrapingException, IOException {
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				final String bibtexUrl = m.group(1);
				// if the url is a relative url
				if (bibtexUrl.startsWith("/")) {
					return UrlUtils.getHostWithProtocol(url) + bibtexUrl;
				}
				return bibtexUrl;
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}
}
