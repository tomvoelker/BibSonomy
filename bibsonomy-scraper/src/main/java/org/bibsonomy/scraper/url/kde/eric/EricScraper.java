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
package org.bibsonomy.scraper.url.kde.eric;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * SCraper for papers from http://www.eric.ed.gov/
 * @author tst
 */
public class EricScraper extends AbstractUrlScraper {

	private static final String SITE_URL = "http://www.eric.ed.gov/";
	private static final String SITE_NAME = "Education Resources Information Center";
	private static final String INFO = "Scraper for publications from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String ERIC_HOST = "eric.ed.gov";

	private static final String EXPORT_BASE_URL = "http://www.eric.ed.gov/ERICWebPortal/MyERIC/clipboard/performExport.jsp?texttype=endnote&accno=";

	private static final Pattern ACCNO_PATTERN = Pattern.compile("accno=([^&]*)");

	private static final RisToBibtexConverter CONVERTER = new RisToBibtexConverter();


	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + ERIC_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	public String getInfo() {
		return INFO;
	}

	protected boolean scrapeInternal (ScrapingContext sc)throws ScrapingException {
		/*
		 * example:
		 * http://www.eric.ed.gov/ERICWebPortal/Home.portal?_nfpb=true&ERICExtSearch_SearchValue_0=star&searchtype=keyword&ERICExtSearch_SearchType_0=kw&_pageLabel=RecordDetails&objectId=0900019b802f2e44&accno=EJ786532&_nfls=false
		 * accno=EJ786532
		 * 
		 * texttype=endnote
		 * 
		 */

		sc.setScraper(this);

		try {
			final URL url = sc.getUrl();
			// extract accno from URL query
			final Matcher accnoMatcher = ACCNO_PATTERN.matcher(url.getQuery());
			if (accnoMatcher.find()) {
				final String downloadUrl = EXPORT_BASE_URL + accnoMatcher.group(1);

				// download RIS
				final String ris = WebUtils.getContentAsString(new URL(downloadUrl));

				// convert to BibTeX
				final String bibtex = CONVERTER.risToBibtex(ris);

				// add downloaded bibtex to result 
				sc.setBibtexResult(BibTexUtils.addFieldIfNotContained(bibtex, "url", url.toString()));
					return true;

			} else {
				throw new PageNotSupportedException("Value for accno is missing.");
			}
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}
