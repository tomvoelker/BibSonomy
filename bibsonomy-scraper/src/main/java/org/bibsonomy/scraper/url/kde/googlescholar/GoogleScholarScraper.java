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
package org.bibsonomy.scraper.url.kde.googlescholar;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * This scraper supports download links from the GoogleSonomy Firefox plugin.
 * 
 * @author tst
 */
public class GoogleScholarScraper extends GenericBibTeXURLScraper {
	private static final String SITE_URL  = "http://scholar.google.com/";
	private static final String SITE_NAME = "Google Scholar";
	private static final String INFO = "Scrapes BibTex from " + href(SITE_URL, SITE_NAME) + ".";
	
	private static final String HOST = "scholar.google.";
	private static final String PATH1 = "/citations";


	private static final Pattern ALL_VERSIONS_PATTERN = Pattern.compile("cluster=(\\d*)");
	private static final Pattern CITATIONS_URL_PATTERN = Pattern.compile("data-clk-atid=\"(\\S*?)\"");
	private static final Pattern BIBTEX_URL_PATTERN = Pattern.compile("scisig=(\\S+)&.*?>BibTeX");
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + HOST + ".*"), Pattern.compile(PATH1 + ".*"))
	);
	
	@Override
	protected String getDownloadURL(final URL url, String cookies) throws ScrapingException {
		try {
			//a link to the bibtex cannot be found on the page of the article, but on the page of the search engine
			String pageContent1 = WebUtils.getContentAsString(url);
			Matcher m_allVersion = ALL_VERSIONS_PATTERN.matcher(pageContent1);

			if (m_allVersion.find()){
				String allVersionsUrl = "https://scholar.google.com/scholar?oi=bibs&hl=en&cluster=" + m_allVersion.group(1);
				String pageContent2 = WebUtils.getContentAsString(allVersionsUrl);
				// here again it is not possible to get to the bibtex directly, because the button is loaded with js.
				Matcher m_citationsUrl = CITATIONS_URL_PATTERN.matcher(pageContent2);

				if (m_citationsUrl.find()){
					//url of the page where the link to the bibtex is not loaded with js and therefore can be scraped
					String citationsUrl = "https://scholar.google.com/scholar?q=info:" + m_citationsUrl.group(1) + ":scholar.google.com/&output=cite&scirp=3&scfhb=1&hl=en";
					String pageContent3 = WebUtils.getContentAsString(citationsUrl);
					Matcher m_bibtexUrl = BIBTEX_URL_PATTERN.matcher(pageContent3);

					if (m_bibtexUrl.find()){
						return "https://scholar.googleusercontent.com/scholar.bib?output=citation&scisf=4&q=info:" + m_citationsUrl.group(1) + ":scholar.google.com/&scisig=" + m_bibtexUrl.group(1);
					}else {
						throw new ScrapingException("couldn't find link to bibtex");
					}
				}else {
					throw new ScrapingException("couldn't find link to all citations");
				}
			}else {
				throw new ScrapingException("couldn't find link to all versions");
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}

	@Override
	public String getInfo() {
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
