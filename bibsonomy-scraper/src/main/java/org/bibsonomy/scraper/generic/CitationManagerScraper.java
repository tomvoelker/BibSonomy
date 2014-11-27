/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import bibtex.parser.ParseException;

/**
 * Superclass for scraping pages, using the same system like PNAS, RSOC or ScienceMag.
 * 
 * @author clemens
 */
public abstract class CitationManagerScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(CitationManagerScraper.class);
	
	/**
	 * @return The pattern to find the download link.
	 */
	public abstract Pattern getDownloadLinkPattern();

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			final String downloadLink = this.buildDownloadLink(sc.getUrl(), WebUtils.getContentAsString(sc.getUrl()));
			
			// download bibtex directly
			final String bibtex = WebUtils.getContentAsString(new URL(downloadLink));
			if (bibtex != null) {
				final StringBuilder bibtexResult = new StringBuilder();
				
				//adding key and url to bibtex if not contained
				try {
					final SimpleBibTeXParser parser = new SimpleBibTeXParser();
					final List<BibTex> publications = parser.parseBibTeXs(bibtex);
					for (final BibTex publication : publications) {
						if (!present(publication.getBibtexKey()) || publication.getBibtexKey().contains("\\s")) {
							publication.setBibtexKey(BibTexUtils.generateBibtexKey(publication));
						}
						if (!present(publication.getUrl())) {
							publication.setUrl(sc.getUrl().toExternalForm());
						}
						bibtexResult.append(BibTexUtils.toBibtexString(publication));
					}
				} catch (final ParseException ex) {
					throw new ScrapingException("Cannot parse BibTex");
				}
				
				sc.setBibtexResult(bibtexResult.toString());
				return true;
			}

		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
		}

		return false;
	}
	
	protected String buildDownloadLink(final URL url, final String content) throws ScrapingFailureException {
		if (log.isDebugEnabled()) {
			if (content.matches("Download Citation")) {
				log.debug("found \"Download Citation\" in content:");
			}
			log.debug(content);
		}
		
		// get link to download page
		final Matcher downloadLinkMatcher = this.getDownloadLinkPattern().matcher(content);
		if (downloadLinkMatcher.find()) { // add type=bibtex to the end of the link
			return "http://" + url.getHost() + downloadLinkMatcher.group(1) + "&type=bibtex";
		}
		
		throw new ScrapingFailureException("Download link is not available");
	}

}
