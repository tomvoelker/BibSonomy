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
package org.bibsonomy.scraper.url.kde.iop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.ValidationUtils;
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
		if (publicationIdMatcher.find()) {
			final String pubId = publicationIdMatcher.group(1);

			final List<NameValuePair> postData = new ArrayList<NameValuePair>(4);
			
			postData.add(new BasicNameValuePair("articleId", pubId));
			postData.add(new BasicNameValuePair("exportFormat", "iopexport_bib"));
			postData.add(new BasicNameValuePair("exportType", "abs"));
			postData.add(new BasicNameValuePair("navsubmit", "Export+abstract"));
			try {
				final String bibtex = WebUtils.getContentAsString("http://" + NEW_IOP_HOST + "/export", null, postData, null);
				if (ValidationUtils.present(bibtex)) {
					sc.setBibtexResult(bibtex.trim());
					return true;
				}
			} catch (MalformedURLException ex) {
				throw new ScrapingFailureException("URL to scrape does not exist. It maybe malformed.");
			} catch (IOException ex) {
				throw new ScrapingFailureException("An unexpected IO error has occurred. Maybe IOP is down.");
			}
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
