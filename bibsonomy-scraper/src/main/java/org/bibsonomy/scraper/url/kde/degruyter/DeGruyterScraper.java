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
package org.bibsonomy.scraper.url.kde.degruyter;

import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Haile
 */
public class DeGruyterScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "De Gruyter";
	private static final String SITE_URL = "https://www.degruyter.com/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + "degruyter.com"), AbstractUrlScraper.EMPTY_PATTERN)
	);

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		URL redirectedUrl = WebUtils.getRedirectUrl(url);
		if (!present(redirectedUrl)){
			redirectedUrl = url;
		}
		URL downloadUrl = new URL(redirectedUrl.getProtocol(), redirectedUrl.getHost(), redirectedUrl.getPath().replaceAll("/html|\\.xml", "/machineReadableCitation/BibTeX"));
		return downloadUrl.toExternalForm();
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
		return URL_PATTERNS;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex.replaceAll(",\\nlastchecked = \\{[^}]*}", "");
	}
}