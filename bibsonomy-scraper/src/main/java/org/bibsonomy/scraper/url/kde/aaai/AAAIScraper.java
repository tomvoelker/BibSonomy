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
package org.bibsonomy.scraper.url.kde.aaai;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.BibtexScraper;

/**
 * @author hagen
 */
public class AAAIScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Association for the Advancement of Artificial Intelligence";

	private static final String SITE_URL = "http://www.aaai.org/";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME)+".";
	
	private static final String PAPER_VIEW_PATH_FRAGMENT = "paper/view";
	private static final String ARTICLE_VIEW_PATH_FRAGMENT = "article/view";
	private static final String DOWNLOAD_PATH_FRAGMENT = "rt/captureCite";
	private static final String PAPER_DOWNLOAD_PATH_SUFFIX = "/0/BibtexCitationPlugin";
	
	private static final List<Pair<Pattern,Pattern>> PATTERNS = new LinkedList<Pair<Pattern,Pattern>>();

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?www.aaai.org"), Pattern.compile(PAPER_VIEW_PATH_FRAGMENT)));
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*?www.aaai.org"), Pattern.compile(ARTICLE_VIEW_PATH_FRAGMENT)));
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

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		
		scrapingContext.setScraper(this);
		
		//build download link
		String downloadLink = scrapingContext.getUrl().toExternalForm();
		
		downloadLink = downloadLink.replace(PAPER_VIEW_PATH_FRAGMENT, DOWNLOAD_PATH_FRAGMENT);
		downloadLink = downloadLink.replace(ARTICLE_VIEW_PATH_FRAGMENT, DOWNLOAD_PATH_FRAGMENT);
		downloadLink += PAPER_DOWNLOAD_PATH_SUFFIX;
	
		//use BibtexScraper with download link
		ScrapingContext bibContext;
		try {
			bibContext = new ScrapingContext(new URL(downloadLink));
		} catch (MalformedURLException ex) {
			throw new ScrapingException(ex);
		}
		if (new BibtexScraper().scrape(bibContext)) {
			String bibtexResult = bibContext.getBibtexResult();
			//replace conference field key by booktitle
			if (!bibtexResult.contains("booktitle")) {
				bibtexResult = bibtexResult.replaceAll("conference\\*?=", "booktitle=");
			}
			
			//replace entry type paper by inproceedings
			//FIXME: are all those publications inproceedings?
			bibtexResult = bibtexResult.replace("@paper", "@inproceedings");
			
			scrapingContext.setBibtexResult(bibtexResult);
			return true;
		}
		return false;
	}
}
