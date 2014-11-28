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

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.DublinCoreToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * Scraper to extract bibtex information from a site, which holds Dublin Core Metadata
 * in it's HTML
 * 
 * @author Lukas
 */
public class DublinCoreScraper implements Scraper {
	//scraper informations
	private static final String SITE_NAME = "DublinCoreScraper";
	private static final String SITE_URL = "http://dublincore.org/";
	private static final String INFO = "The DublinCoreScraper resolves bibtex out of HTML Metatags, which are defined" + 
	" in the DublinCore Metaformat, given by the " + AbstractUrlScraper.href(SITE_URL, "Dublin Core Metadata Initiative") + 
	"\n Because all components of DC-Metadata are optional and their values not standardized, the scraper may not always be successful.";
	
	//pattern for checking support for a given site
	
	private static final Pattern DUBLIN_CORE_PATTERN = Pattern.compile("(?im)(?=<\\s*meta[^>]*name=\"DC.Title[^>]\"[^>]*>)(?=<\\s*meta[^>]*name=\"DC.Type[^>]\"[^>]*>)" +
	"(?=<\\s*meta[^>]*name=\"DC.Creator[^>]\"[^>]*>)");
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#scrape(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrape(ScrapingContext scrapingContext) throws ScrapingException {
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		// get the page content to find the dublin core data
		final String page = scrapingContext.getPageContent();
		if (present(page)) {
			// try to extract Dublin Core metadata 
			 String result = DublinCoreToBibtexConverter.getBibTeX(page);
			/*
			 * We are not greedy, since many pages contain Dublin Core but often 
			 * not enough: if we would return true for all of them, we would 
			 * disable all following scrapers (in particular, the IE scraper). 
			 */
			if (present (result)) {
				// set scraper found
				scrapingContext.setScraper(this);
				
				//add url
				result = BibTexUtils.addFieldIfNotContained(result, "url", scrapingContext.getUrl().toString());
				// get bibtex information out of the DC metatags in the page
				scrapingContext.setBibtexResult(result);
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getScraper()
	 */
	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#supportsScrapingContext(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean supportsScrapingContext(ScrapingContext scrapingContext) {
		if (!present(scrapingContext.getUrl())) {
			return false;
		}
		try {
			final String page = scrapingContext.getPageContent();
			// check whether page has got Dublin Core Metadata or not
			return DUBLIN_CORE_PATTERN.matcher(page).find();
		} catch (final ScrapingException ex) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/**
	 * @return site name
	 */
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
