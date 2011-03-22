package org.bibsonomy.scraper.generic;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Superclass for scraping pages, using the same system like PNAS, RSOC or ScienceMag.
 * 
 * @author clemens
 * @version $Id$
 */
public abstract class CitationManagerScraper extends AbstractUrlScraper {
		
	/**
	 * @return The pattern to find the download link.
	 */
	public abstract Pattern getDownloadLinkPattern();

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			final String content = WebUtils.getContentAsString(sc.getUrl());

			// get link to download page
			final Matcher downloadLinkMatcher = getDownloadLinkPattern().matcher(content);
			final String downloadLink;
			if(downloadLinkMatcher.find()) // add type=bibtex to the end of the link
				downloadLink = "http://" + sc.getUrl().getHost() + downloadLinkMatcher.group(1) + "&type=bibtex";
			else
				throw new ScrapingFailureException("Download link is not available");

			// download bibtex directly
			final String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtex != null) {
				// clean up (whitespaces in bibtex key)
				final int indexOfComma = bibtex.indexOf(",");
				
				final String key = bibtex.substring(0, indexOfComma).replaceAll("\\s", "");
				final String rest = bibtex.substring(indexOfComma);				
				sc.setBibtexResult(key + rest);
				return true;
			}

		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}

		return false;
	}

}
