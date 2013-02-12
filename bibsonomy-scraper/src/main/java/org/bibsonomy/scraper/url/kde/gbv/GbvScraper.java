/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.url.kde.gbv;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.PicaToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;


/**
 * @author hmi
 * @version $Id$
 */
public class GbvScraper extends AbstractUrlScraper{

	private final Log log = LogFactory.getLog(GbvScraper.class);

	private static final String SITE_NAME = "GVK - GBV Union Catalogue - 2.1 ";
	private static final String SITE_URL = "http://gso.gbv.de";
	private static final String HOST_NAME = "gso.gbv.de";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);


	/**
	 * TODO: This Scraper match only on URL's with specific query value in path and queries. The current patterns don't work.
	 */
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(HOST_NAME + ".*"), Pattern.compile(".*(/PPN.|TRM=[0-9]+)*")));
	//

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		
		sc.setScraper(this);

		try {
			// create a converter and start converting :)
			final PicaToBibtexConverter converter = new PicaToBibtexConverter(sc.getPageContent(), "xml", sc.getUrl().toString());

			final String bibResult = converter.getBibResult();

			if(bibResult != null){
				sc.setBibtexResult(bibResult);
				return true;
			}else
				throw new ScrapingFailureException("getting bibtex failed");

		} catch (Exception e){
			throw new InternalFailureException(e);
		}
}
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return INFO;
	}
}
