/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.NameValuePair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Note: do not use this class directly when implementing scrapers, use the
 * "implemented" specialized format scrapers {@link GenericBibTeXURLScraper},
 * {@link GenericEndnoteURLScraper} and {@link GenericRISURLScraper}
 * 
 * @author dzo
 */
public abstract class AbstractGenericFormatURLScraper extends AbstractUrlScraper {

    // FIXME: why is this part of this class? Should be moved to the scraper which needs this
    private static final Pattern URL_PATTERN_FOR_URL = Pattern.compile("URL = \\{ \n\\s+(.*)\n\\s+\n\\}");


	/**
	 * @param url
	 * @param cookies
	 * @return the download url
	 * @throws ScrapingException
	 * @throws IOException
	 */
	protected abstract String getDownloadURL(final URL url, String cookies) throws ScrapingException, IOException;

    
	@Override
	protected final boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			final URL url = scrapingContext.getUrl();
			
			final String cookies;
			if (this.retrieveCookiesFromSite()) {
				cookies = WebUtils.getCookies(url);
			} else {
				cookies = null;
			}
			
			final String downloadURL = this.getDownloadURL(url, cookies);
			final List<NameValuePair> postData = this.getDownloadData(url, cookies);
			if (downloadURL == null) {
				throw new ScrapingFailureException("can't get download URL for " + url);
			}
			
			final String downloadResult = WebUtils.getContentAsString(downloadURL, cookies, postData, null, url.toString());
			String bibtex = this.convert(downloadResult);
			
			/*
			 * clean the bibtex for better format
			 */
			if (present(bibtex)) {
				final Matcher m = URL_PATTERN_FOR_URL.matcher(bibtex);
				if (m.find()) {
					bibtex = bibtex.replaceAll(URL_PATTERN_FOR_URL.toString(), "url = {" + m.group(1) + "}");
				}
				bibtex = postProcessScrapingResult(scrapingContext, bibtex);
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

	/**
	 * returns a list of name value pairs
	 * @param url
	 * @param cookies
	 * @return
	 */
	protected List<NameValuePair> getDownloadData(final URL url, final String cookies) throws ScrapingException {
		return null;
	}

	/**
	 * @return iff cookies from the site should be used
	 */
	protected boolean retrieveCookiesFromSite() {
		return false;
	}

	/**
	 * @param scrapingContext
	 * @param bibtex
	 * @return the postProcessed bibtex
	 */
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex;
	}

	/**
	 * @param downloadResult
	 * @return downloadResult, converted to bibtex
	 */
	protected abstract String convert(String downloadResult);
}
