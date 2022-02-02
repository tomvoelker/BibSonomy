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
package org.bibsonomy.scraper.url.kde.citeseer;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author tst
 */
public class CiteseerScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "CiteSeerX";
	private static final String SITE_URL  = "http://citeseerx.ist.psu.edu/";

	private static final String INFO = "This scraper parses a publication page from the " +
									   "Scientific Literature Digital Library and Search Engine " + href(SITE_URL, SITE_NAME);
	/*
	 * The content of both hosts is exactly the same with the same querys.
	 */
	private static final String HOST1 = "citeseerx.ist.psu.edu";
	private static final String HOST2 = "citeseer.ist.psu.edu";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();
	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST1), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST2), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final Pattern DOI_PATTERN = Pattern.compile("(10\\.1\\.1\\.\\d{1,4}\\.\\d{1,4})");
	private static final Pattern bibtexPattern = Pattern.compile("<div id=\"bibtex\" class=\"block\">.*?<h3>BibTeX</h3>.*?<p>(.+?)</p>.*?</div>", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern abstractPattern = Pattern.compile("<h3>Abstract</h3>.*?<p>(.*?)</p>", Pattern.MULTILINE | Pattern.DOTALL);

	private static final String ARTICLE_PATH = "/viewdoc/summary?doi=";
	
	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		/*
		 * There seem to be problems with the url. We extract the doi and append it to a working url-base.
		 * Also, the doi doesn't seem to follow the official format
		 */
		Matcher m_doi = DOI_PATTERN.matcher(sc.getUrl().toString());
		if (m_doi.find()){
			try {
				URL url = new URL("https://" + sc.getUrl().getHost() + ARTICLE_PATH + m_doi.group(1));
				sc.setUrl(url);
				//should it throw an exception we just use the url in the scrapingContext
			} catch (MalformedURLException ignored) {}
		}

		// TODO: why do we need this check?
		// check for selected bibtex snippet
		if (present(sc.getSelectedText())) {
			sc.setBibtexResult(sc.getSelectedText());
			sc.setScraper(this);
			return true;
		}

		// no snippet selected
		String page = sc.getPageContent();
		
		// search BibTeXsnippet in html
		final Matcher matcher = bibtexPattern.matcher(page);
		
		if (matcher.find()) {
			String bibtex = matcher.group(1).replace("<br/>", "\n").replace("&nbsp;", " ").replace(",,", ",");
			
			/*
			 * search abstract 
			 */
			final Matcher abstractMatcher = abstractPattern.matcher(page);
			if (abstractMatcher.find()) {
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractMatcher.group(1));
			}
			
			// append url
			bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
			
			sc.setBibtexResult(bibtex);
			return true;
		}
		
		throw new PageNotSupportedException("no bibtex snippet available");
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
