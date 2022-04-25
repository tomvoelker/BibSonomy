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

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.HTMLMetaDataEprintToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Scraper to extract bibtex information from a site, which holds Eprint Metadata
 * in its HTML
 * @author tst
 */
public class EprintScraper implements Scraper {

	private static final String INFO = "Scraper for repositories which use " + AbstractUrlScraper.href("http://www.eprints.org/", "eprints");
	private static final String SITE_NAME = "EprintsScraper";
	private static final String SITE_URL = "https://www.eprints.org/";
	private static final HTMLMetaDataEprintToBibtexConverter converter = new HTMLMetaDataEprintToBibtexConverter();

	private static final Pattern EPRINT_PATTERN_TITLE = Pattern.compile("<\\s*meta(?=[^>]*name=\"eprints.title\")[^>]*content=\"([^\"]*)\"[^>]*>", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	private static final Pattern EPRINT_PATTERN_AUTHOR = Pattern.compile("<\\s*meta(?=[^>]*name=\"eprints.creators_name\")[^>]*content=\"([^\"]*)\"[^>]*>", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	@Override
	public boolean scrape(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		sc.setBibtexResult(converter.toBibtex(sc.getPageContent()));
		return true;
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext sc) {
		try {
			final String pageContent = sc.getPageContent();
			return EPRINT_PATTERN_TITLE.matcher(pageContent).find() && EPRINT_PATTERN_AUTHOR.matcher(pageContent).find();
		}catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @return site name
	 */
	public String getSupportedSiteName(){
		return SITE_NAME;
	}

	/**
	 * @return site url
	 */
	public String getSupportedSiteURL(){
		return SITE_URL;
	}

}
