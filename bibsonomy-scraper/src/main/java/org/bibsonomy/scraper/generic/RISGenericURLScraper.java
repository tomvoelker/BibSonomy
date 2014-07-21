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
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * Transforms the scraping result from RIS to BibTeX after scraping, using the {@link RisToBibtexConverter}.
 * 
 * 
 * @author Haile
 *
 */
public abstract class RISGenericURLScraper extends AbstractUrlScraper {
	public abstract String getRISURL(final URL url);
	private static RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			String bibtexURL = getRISURL(scrapingContext.getUrl());
			String bibtexResult = WebUtils.getContentAsString(bibtexURL);
			final String bibtex = RIS2BIB.risToBibtex(bibtexResult);
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(bibtex);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}
}
