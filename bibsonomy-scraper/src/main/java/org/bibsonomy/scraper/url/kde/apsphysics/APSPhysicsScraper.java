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
package org.bibsonomy.scraper.url.kde.apsphysics;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APSPhysicsScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "APS Physics";
	private static final String SITE_URL = "https://journals.aps.org/";
	private static final String SITE_HOST = "journals.aps.org";
	private static final String INFO = "For selected BibTeX snippets and articles from " + href(SITE_URL , SITE_NAME)+".";
	private static final Pattern PATTERN_ABSTRACT = Pattern.compile("<meta content=\"([^<]*?)\" name=\"description\" />");


	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + SITE_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
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
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String urlPath = url.getPath();
		urlPath = urlPath.replaceAll("abstract|references|pdf|cited-by|supplemental", "export");
		return "https://"+ url.getHost() +"/" + urlPath + "?type=bibtex&download=true";
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String bibtex) {
		try {
			String pageContent = WebUtils.getContentAsString(sc.getUrl().toString().replaceAll("references|pdf|cited-by|supplemental|export", "abstract"));
			Matcher m_abstract = PATTERN_ABSTRACT.matcher(pageContent);
			if (m_abstract.find()){
				String abstractOfBibtex = m_abstract.group(1);
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "abstract", abstractOfBibtex);
			}
		} catch (IOException ignored) {}
		return bibtex;
	}



}
