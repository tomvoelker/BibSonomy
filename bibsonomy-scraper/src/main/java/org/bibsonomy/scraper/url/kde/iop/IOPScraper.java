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
package org.bibsonomy.scraper.url.kde.iop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.WebUtils;


/**
 * SCraper for http://www.iop.org and http://www.ioscience.iop.org/article
 * @author tst
 */
public class IOPScraper extends AbstractUrlScraper {

	/* URL parts */
	private static final String IOP_URL_PATH_START = "/article";
	private static final String IOP_EJ_URL_BASE    = "http://www.iop.org";
	private static final String SITE_NAME = "IOP";
	private static final String SITE_URL = IOP_EJ_URL_BASE + "/";
	private static final String INFO = "Scraper for electronic journals from " + href(SITE_URL, SITE_NAME);
	private static final String IOP_HOST = "iop.org";
	private static final String NEW_IOP_HOST = "iopscience.iop.org";

	/*
	 * needed regular expressions to extract the publication id from the url
	 */
	private static final Pattern PUBLICATION_ID_PATTERN = Pattern.compile("^.*?article\\/.*?\\/(.*?)($|\\/meta)");

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern,Pattern>>();
	static{
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + IOP_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NEW_IOP_HOST), Pattern.compile(IOP_URL_PATH_START + ".*")));
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		final Matcher publicationIdMatcher = PUBLICATION_ID_PATTERN.matcher(sc.getUrl().toString());
		String pubId = "";
		if (publicationIdMatcher.find()) {
			pubId = publicationIdMatcher.group(1);
		}
		// TODO: handle publ id not found
	
		final String postArgs = "articleId=" + pubId +
						  "&exportFormat=iopexport_bib" + 
						  "&exportType=abs" +
						  "&navsubmit=Export+abstract";
		try {
			final String bibtex = WebUtils.getPostContentAsString(new URL("http://" + NEW_IOP_HOST + "/export"), postArgs, StringUtils.CHARSET_UTF_8);
			if (bibtex != null) {
				sc.setBibtexResult(bibtex.trim());
				return true;
			}
		} catch (MalformedURLException ex) {
			throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
		} catch (IOException ex) {
			throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe IOP is down.");
		}
		return false;
	}
	
	@Override
	public String getInfo(){
		return INFO;
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
