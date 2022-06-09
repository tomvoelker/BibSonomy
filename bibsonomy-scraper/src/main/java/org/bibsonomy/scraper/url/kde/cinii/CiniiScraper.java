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
package org.bibsonomy.scraper.url.kde.cinii;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * @author Mohammed Abed
 */
public class CiniiScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "cinii Articles";
	private static final String SITE_URL = "http://ci.nii.ac.jp/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String OLD_CINII_HOST = "ci.nii.ac.jp";
	private static final String NEW_CINII_HOST = "cir.nii.ac.jp";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<Pair<Pattern, Pattern>>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + OLD_CINII_HOST), GenericBibTeXURLScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NEW_CINII_HOST), GenericBibTeXURLScraper.EMPTY_PATTERN));
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
		return info;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		//the language is changed with cookies
		HttpGet get = new HttpGet(sc.getUrl().toString() + ".bib");
		get.addHeader("Cookie", "CIR_LANGUAGE=en");
		try {
			String bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
			sc.setBibtexResult(bibtex);
			return true;
		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}
	}
}
