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
package org.bibsonomy.scraper.url.kde.springer;

import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.id.kde.doi.ContentNegotiationDOIScraper;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for springer.com
 *
 * @author tst
 */
public class SpringerScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Springer";
	private static final String SITE_URL = "https://www.springer.com/";
	private static final String INFO = "Scraper for books from " + href(SITE_URL, SITE_NAME) + ".";

	/** Host */
	private static final String HOST = "springer.com";
	private static final String SPRINGER_CITATION_HOST = "link.springer.com";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static {
		patterns.add(new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("computer")));
		patterns.add(new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("book")));
		patterns.add(new Pair<>(Pattern.compile(".*" + HOST), Pattern.compile("content")));
		patterns.add(new Pair<>(Pattern.compile(".*" + SPRINGER_CITATION_HOST), Pattern.compile("computer")));
		patterns.add(new Pair<>(Pattern.compile(".*" + SPRINGER_CITATION_HOST), Pattern.compile("book")));
		patterns.add(new Pair<>(Pattern.compile(".*" + SPRINGER_CITATION_HOST), Pattern.compile("content")));
	}

	Pattern HTML_DOI_PATTERN = Pattern.compile("<dt>DOI</dt>\\s*<dd>(.*)</dd>");

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		final String url = sc.getUrl().toExternalForm().replace(".pdf", "");

		try {
			String  doiURL = "https://doi.org/";
			String doi = DOIUtils.getDoiFromURL(new URL(url));

			if (present(doi)){
				doiURL += doi;
			}else {
				String html = WebUtils.getContentAsString(url);
				Matcher m_doiInHtml = HTML_DOI_PATTERN.matcher(html);
				if (m_doiInHtml.find()) doiURL += m_doiInHtml.group(1);
			}

			if (present(doiURL)){
				ScrapingContext context = new ScrapingContext(new URL(doiURL), null);
				new ContentNegotiationDOIScraper().scrape(context);
				sc.setBibtexResult(context.getBibtexResult());
				sc.setDoiURL(new URL(doiURL));
				return true;
			}else {
				return false;
			}
		} catch (final IOException ex) {
			throw new InternalFailureException(ex);
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
