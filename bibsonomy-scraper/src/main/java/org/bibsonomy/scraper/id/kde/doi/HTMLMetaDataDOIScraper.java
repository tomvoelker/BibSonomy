/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.id.kde.doi;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.HTMLMetaDataDublinCoreToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * if none of the scrapers could get a bibtex this scraper searches for a doi, so {@link ContentNegotiationDOIScraper} can try to get it
 *
 * @author Johannes
 */
public class HTMLMetaDataDOIScraper extends HTMLMetaDataDublinCoreToBibtexConverter implements Scraper {

	private static final String INFO = "The HTMLMetaDataDOIScraper gets a doi from the webpage, if no URL scraper matched the previously redirected page.";

	private static final Pattern DOIPATTERN_HIGHWIRE_PRESS_TAGS = Pattern.compile("<meta\\s+name=\"citation_doi\"\\s+content=\"(.*?)\"");

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		String doi = getDoiFromMetaData(scrapingContext.getUrl());
		if (!present(doi)) {
			doi = DOIUtils.getDoiFromURL(scrapingContext.getUrl());
		}

		// FIXME: extractDOI is called which does not work for large text
		/*
		if (doi == null) {
			try {
				doi = DOIUtils.getDoiFromWebPage(scrapingContext.getUrl());
			} catch (final IOException e) {
				throw new ScrapingException(e);
			}
		}*/

		if (present(doi)) {
			scrapingContext.setSelectedText(doi);
		}

		// always return false as this scraper doesn't actually get the bibtex, only sets the doi
		return false;
	}

	/**
	 * checks if any doi can be found in DublinCore- or HighwirePressTags-Metadata and returns the doi if found
	 * @param url
	 * @return
	 * @throws ScrapingException
	 */
	protected String getDoiFromMetaData(final URL url) throws ScrapingException{
		try {
			if (present(url)) {
				final String content = WebUtils.getContentAsString(url);

				//try to get doi from Highwire Press tags
				final Matcher m = DOIPATTERN_HIGHWIRE_PRESS_TAGS.matcher(content);
				if (m.find()) {
					return m.group(1);
				}

				// try to get doi from dublin core
				final String doi = extractData(content).get("doi");
				if (ValidationUtils.present(doi)) {
					return doi;
				}	
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getScraper()
	 */
	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper>singletonList(this);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		//no need to search for a doi if doiURL is set or a doi is selected 
		return scrapingContext.getDoiURL() == null && !DOIUtils.isSupportedSelection((scrapingContext.getSelectedText()));
	}


	// Somehow these dummies are necessary
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return null;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return null;
	}

}
