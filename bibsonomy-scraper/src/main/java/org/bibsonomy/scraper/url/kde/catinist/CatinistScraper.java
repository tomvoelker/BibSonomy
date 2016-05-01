/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.catinist;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.EndnoteToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class CatinistScraper extends AbstractUrlScraper {
	
	private static final Log log = LogFactory.getLog(CatinistScraper.class);
	private final static String SITE_NAME = "Refdoc";
	private final static String SITE_URL = "http://cat.inist.fr";
	private final static String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private final static String HOST = "cat.inist.fr";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		String requestURL = null;
		if (!scrapingContext.getUrl().toString().contains("exportN")) {
			requestURL = scrapingContext.getUrl().toString().replace("afficheN", "exportN");
		}
		else
			requestURL = scrapingContext.getUrl().toString();
		Pattern p = Pattern.compile("cpsidt=(.*\\d+)");
		Matcher m = p.matcher(scrapingContext.getUrl().toString());
		if (m.find()) {
			try {
				final String endNote = WebUtils.getPostContentAsString(new URL(requestURL), "aExport=export_endnote&cPanier=exporter&cpsidt=" + m.group(1));
				EndnoteToBibtexConverter converter = new EndnoteToBibtexConverter();
				final String bibtexResult = cleanBibtex(converter.toBibtex(endNote));
				if(bibtexResult != null) {
					scrapingContext.setBibtexResult(bibtexResult);
					return true;
				}
			}catch (IOException e) {
				log.error("error while scraping  " + requestURL, e);
			}
		}
		return false;
	}
	
	private String cleanBibtex(final String bibtex) {
		String bibtexResult = bibtex.replace("#160", "");
		return bibtexResult;
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

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}
}
