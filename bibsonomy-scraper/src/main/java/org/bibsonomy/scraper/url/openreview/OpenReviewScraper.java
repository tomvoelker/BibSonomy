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
package org.bibsonomy.scraper.url.openreview;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scraper for openreview
 *
 * @author dzo
 */
public class OpenReviewScraper extends AbstractUrlScraper {

	private static final String HOST = "openreview.net";
	private static final String SITE_URL = "https://" + HOST;

	private static final String SITE_NAME = "OpenReview.net";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String FORUM_PATH = "/forum";
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Arrays.asList(
					new Pair<>(Pattern.compile(HOST), Pattern.compile(FORUM_PATH)),
					new Pair<>(Pattern.compile(HOST), Pattern.compile("/pdf"))
	);


	private static final Pattern BIBTEX_PATTERN = Pattern.compile("\"_bibtex\":\"(.+?)\"", Pattern.DOTALL);
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("Abstract<!-- -->:</strong>\\s+<span class=\"note-content-value\">(.+?)</span>", Pattern.DOTALL);

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
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
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		final URL urlToScrape = scrapingContext.getUrl();

		// maybe we were called using the pdf url so we use only the query with the id to build the forum path
		try {
			final URL urlToLoad = new URL(SITE_URL + FORUM_PATH + "?" + urlToScrape.getQuery());

			final String content = WebUtils.getContentAsString(urlToLoad);
			final Matcher matcher = BIBTEX_PATTERN.matcher(content);
			if (matcher.find()) {
				String bibtex = matcher.group(1).replaceAll("\\\\n", "\n");
				final Matcher abstractMatcher = ABSTRACT_PATTERN.matcher(content);
				if (abstractMatcher.find()) {
					final String paperAbstract = abstractMatcher.group(1);
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", paperAbstract);
				}
				scrapingContext.setBibtexResult(bibtex);
			}
			return true;
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
	}
}
