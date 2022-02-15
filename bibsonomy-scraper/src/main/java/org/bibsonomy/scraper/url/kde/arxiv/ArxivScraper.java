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
package org.bibsonomy.scraper.url.kde.arxiv;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bibsonomy.util.ValidationUtils.present;


/**
 * Scraper for arXiv.
 * 
 * @author rja
 */
public class ArxivScraper extends AbstractUrlScraper {
	private static final String info = "This scraper parses a publication page from " + href(ArxivUtils.SITE_URL, ArxivUtils.SITE_NAME)+".";

	protected static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<>(Pattern.compile(ArxivUtils.ARXIV_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern DOI_PATTERN = Pattern.compile("<meta name=\"citation_doi\" content=\"(.*?)\"/>");

	@Override
	public boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		final URL url = sc.getUrl();
		final String selection = sc.getSelectedText();
		final String identifier;
		// get arxiv identifier from URL or selection
		if (!present(selection) && ArxivUtils.isArxivUrl(url)) {
			identifier = ArxivUtils.extractArxivIdentifier(url.toString());
		} else {
			identifier = ArxivUtils.extractArxivIdentifier(selection);
		}

		String bibtex;
		if (present(identifier)) {
			try {
				// first we try to scrape arxiv directly.
				final String exportURL = ArxivUtils.SITE_URL + "bibtex/" +identifier;
				bibtex = WebUtils.getContentAsString(exportURL);
			}catch (IOException io){
				try {
					//if scraping directly does not work we extract the doi and scrape api.crossref.org
					String articleUrl = ArxivUtils.SITE_URL + "abs/" + identifier;
					String pageContent = WebUtils.getContentAsString(articleUrl);
					Matcher m_doi = DOI_PATTERN.matcher(pageContent);

					if (m_doi.find()){
						HttpGet get = new HttpGet("https://api.crossref.org/v1/works/" + UrlUtils.safeURIEncode(m_doi.group(1)) + "/transform");
						get.setHeader("Accept", "text/bibliography; style=bibtex");
						bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
					}else {
						throw new ScrapingException("The content of the Get-Request to " + articleUrl + " did not contain a doi");
					}
				} catch (IOException | HttpException e) {
					throw new ScrapingException(e);
				}
			}
		}else {
			throw new ScrapingFailureException("no arxiv id found in URL");
		}

		if (present(bibtex)){
			sc.setBibtexResult(bibtex);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		return ArxivUtils.isArxivUrl(scrapingContext.getUrl())
				|| ArxivUtils.isArxivUrl(scrapingContext.getSelectedText())
				|| ArxivUtils.containsStrictArxivIdentifier(scrapingContext.getSelectedText());
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
		return ArxivUtils.SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return ArxivUtils.SITE_URL;
	}
}
