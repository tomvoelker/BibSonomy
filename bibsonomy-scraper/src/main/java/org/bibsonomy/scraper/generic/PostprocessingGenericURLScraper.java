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

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Allows {@link SimpleGenericURLScraper}s to postprocess the scraped result, i.e., by converting it to BibTeX or modifying the BibTeX.
 * 
 * @author Haile
 */
public abstract class PostprocessingGenericURLScraper extends SimpleGenericURLScraper {


	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		final boolean result = super.scrapeInternal(scrapingContext);
		if (result) {
			scrapingContext.setBibtexResult(this.postProcessScrapingResult(scrapingContext, scrapingContext.getBibtexResult()));
		}
		return result;
	}
	
	protected abstract String postProcessScrapingResult(ScrapingContext sc, final String result);

}
