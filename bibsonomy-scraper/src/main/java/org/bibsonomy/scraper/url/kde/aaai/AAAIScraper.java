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
package org.bibsonomy.scraper.url.kde.aaai;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.UrlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * scrapes BibTeX from AAAI website
 *
 * TODO: fix wrong bibtex format
 *
 * @author hagen
 */
public class AAAIScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_NAME = "Association for the Advancement of Artificial Intelligence";

	private static final String SITE_URL = "https://www.aaai.org/";

	private static final String INFO = "Scraper for references from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String ARTICLE_VIEW_PATH_FRAGMENT = "index.php/.*/article/view.*";

	private static final List<Pair<Pattern,Pattern>> PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*aaai.org"), Pattern.compile(ARTICLE_VIEW_PATH_FRAGMENT))
	);

	private static final Pattern ID_PATTERN = Pattern.compile("\\d+");

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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		final String path = url.getPath();

		final Matcher matcher = ID_PATTERN.matcher(path);
		if (matcher.find()) {
			// find out the correct journal / conference
			final String[] paths = path.split("/index.php");
			final String[] subPaths = paths[1].split("/");
			return UrlUtils.getHostWithProtocol(url) + "/index.php/" + subPaths[1] + "/citationstylelanguage/download/bibtex?submissionId=" + matcher.group();
		}

		return null;
	}
	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		//fixing broken citekeys by replacing whitespaces with an underscore
		String fixedBibtex = "";
		String citeKey = "";
		String fixedCiteKey = "";
		//extracting citeKey
		int startOfCiteKey = bibtex.indexOf("{")+1;
		int endOfCiteKey = bibtex.indexOf(",");
		citeKey = bibtex.substring(startOfCiteKey, endOfCiteKey);

		fixedCiteKey = citeKey.replace(" ", "_");
		fixedBibtex = bibtex.replace(citeKey, fixedCiteKey);

		String bibtexWithoutHtml = fixedBibtex.replaceAll("\\<.*?>","");

		return bibtexWithoutHtml;
	}
}
