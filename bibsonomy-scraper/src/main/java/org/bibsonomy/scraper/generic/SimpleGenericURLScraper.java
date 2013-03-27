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

import java.io.IOException;
import java.net.URL;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.util.WebUtils;

/**
 * Super class to support pattern "URL in -> URL out".
 * 
 * @author hagen
 * @version $Id$
 */
public abstract class SimpleGenericURLScraper extends AbstractUrlScraper {

	/**
	 * Implementations of this class should return the download link for the BibTeX file.
	 * 
	 * @param url The URL to be scraped.
	 * @return The URL that points to the download.
	 */
	public abstract String getBibTeXURL(final URL url);
	
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			String bibtexURL = getBibTeXURL(scrapingContext.getUrl());
			String bibtexResult = WebUtils.getContentAsString(bibtexURL);
			
			if (present(bibtexResult)) {
				scrapingContext.setBibtexResult(bibtexResult);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
	}

}
