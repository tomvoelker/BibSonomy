/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Note: do not use this class directly when implementing scrapers, use the
 * "implemented" specialized format scrapers {@link GenericBibTeXURLScraper},
 * {@link GenericEndnoteURLScraper} and {@link GenericRISURLScraper}
 * 
 * @author dzo
 */
public abstract class AbstractGenericFormatURLScraper extends AbstractUrlScraper {
	
	protected abstract String getDownloadURL(final URL url) throws ScrapingException;
	
	@Override
	protected final boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			final URL url = scrapingContext.getUrl();
			final String downloadURL = getDownloadURL(url);
			if (downloadURL == null) {
				throw new ScrapingFailureException("can't get download url for " + url);
			}
			final String downloadResult = WebUtils.getContentAsString(downloadURL);
			
			String bibtex = this.convert(downloadResult);
			
			if (present(bibtex)) {
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
