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
package org.bibsonomy.scraper.url.kde.wormbase;

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
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for http://www.wormbase.org
 * @author tst
 */
public class WormbaseScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Wormbase";
	private static final String SITE_URL = "http://www.wormbase.org/";
	private static final String INFO = "Scraper for papers from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "wormbase.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern pattern = Pattern.compile("name=([^;]*);");

	private static final String DOWNLOAD_URL = "http://www.textpresso.org/cgi-bin/wb/exportendnote?mode=singleentry&lit=C.%20elegans&id=";

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);


		// get id
		final Matcher matcherName = pattern.matcher(sc.getUrl().toString());
		if(matcherName.find()) {
			final String name = matcherName.group(1);

			// get endnote
			try {
				final String endnote = WebUtils.getContentAsString(new URL(DOWNLOAD_URL + name));

				// convert bibtex
				final EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
				String bibtex = converter.processEntry(endnote);

				if(bibtex != null){
					// append url
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtex);
					return true;
				}else
					throw new ScrapingFailureException("generating bibtex failed");

			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}

		}else
			throw new PageNotSupportedException("no paper ID available");
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

}
