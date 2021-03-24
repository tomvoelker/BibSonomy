package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * abstract scraper for a citation manager that is used by some libraries. You can identify using the final download url
 * for the export (should be /action/downloadCitation).
 *
 * @author dzo
 */
public abstract class CitMgrScraper extends AbstractUrlScraper {

	private static final Pattern DOUBLE_COMMA_FIX = Pattern.compile("},,\n");

	private static String repairBibTeX(final String bibTeX) {
		return DOUBLE_COMMA_FIX.matcher(bibTeX).replaceFirst("},\n");
	}

	@Override
	protected final boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		final URL url = scrapingContext.getUrl();

		try {
			final String doi = this.getDOI(url);
			if (!present(doi)) {
				throw new ScrapingFailureException("can't get doi from url");
			}
			// the doi must be not save encoded :(
			final String postContent = "doi=" + doi + "&downloadFileName=pericles_1467981741&format=bibtex&direct=other-type&include=abs&submit=Download";
			final String downloadUrl = this.getDownloodSiteUrl(url) + "action/downloadCitation";
			final String bibtex = WebUtils.getContentAsString(downloadUrl, null, postContent, url.toExternalForm());
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(repairBibTeX(bibtex.trim()));
			}
			return true;
		} catch (final IOException | URISyntaxException e) {
			throw new InternalFailureException(e);
		}
	}

	protected String getDownloodSiteUrl(final URL url) throws ScrapingFailureException {
		return this.getSupportedSiteURL();
	}

	protected String getDOI(URL url) throws URISyntaxException {
		final String doi = DOIUtils.extractDOI(url.getPath());
		return UrlUtils.decodePathSegment(doi);
	}
}
