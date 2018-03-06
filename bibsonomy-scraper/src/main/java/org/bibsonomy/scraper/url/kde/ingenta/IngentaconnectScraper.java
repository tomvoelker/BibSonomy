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
package org.bibsonomy.scraper.url.kde.ingenta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import bibtex.parser.BibtexParser;

/** Scraper for ingentaconnect.
 * @author rja
 *
 */
public class IngentaconnectScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "Ingentaconnect";
	private static final String SITE_URL = "http://www.ingentaconnect.com/";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final String INGENTA_HOST = "ingentaconnect.com";

	private static final Pattern EXPORT_PATTERN = Pattern.compile("BibText Export\" href=\"([^\"]++)\"");

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + INGENTA_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	// for fixSpaceInKey to find spaces in BibTeX keys 
	private static final Pattern PATTERN_KEY_SPACE = Pattern.compile("^(\\w+) (\\w+ =)", Pattern.MULTILINE);
	
	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		try {
			final Matcher m = EXPORT_PATTERN.matcher(sc.getPageContent());

			if (m.find()) {
				sc.setScraper(this);
				/* 
				 * create query URL
				 */
				final URL queryURL = new URL(SITE_URL.substring(0, SITE_URL.length() - 1) + m.group(1));

				String bibtex = WebUtils.getContentAsString(queryURL);

				bibtex = DOIUtils.cleanDOI(bibtex);
				
				bibtex = fixSpaceInKey(bibtex);

				if (ValidationUtils.present(bibtex)) {
					sc.setBibtexResult(bibtex);
					return true;
				} 
				throw new ScrapingFailureException("getting bibtex failed");

			} 
			return false;
		} catch (MalformedURLException e) {
			throw new InternalFailureException(e);
		} catch (IOException ex) {
			throw new InternalFailureException(ex);
		}
	}

	/**
	 * The BibTeX returned contains keys with space, e.g., "publication date" which
	 * need to be fixed in order to be accepted by {@link BibtexParser}.
	 * 
	 * @param bibtex
	 * @return
	 */
	protected static String fixSpaceInKey(final String bibtex) {
		final Matcher matcher = PATTERN_KEY_SPACE.matcher(bibtex);
		if (matcher.find()) {
			return matcher.replaceFirst("$1$2");
		}
		return bibtex;
	}


	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
