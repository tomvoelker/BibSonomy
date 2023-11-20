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
package org.bibsonomy.scraper.url.kde.karlsruhe;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;
import org.bibsonomy.util.WebUtils;

/** Scraper for AIFB.
 * 
 * @author ccl
 *
 */
public class AIFBScraper extends GenericBibTeXURLScraper {
	
	private static final String SITE_NAME = "Institut AIFB Universität Karlsruhe";
	private static final String AIFB_SITE_NAME = "AIFB";
	private static final String AIFB_HOST_NAME = "http://www.aifb.kit.edu";
	private static final String AIFB_HOST = "aifb.kit.edu";
	private static final String AIFB_WEB = "/web/";
	private static final String info =	"This scraper parses institute, research group and " +
										"people-specific pages from the " +
										href("http://www.aifb.uni-karlsruhe.de/", SITE_NAME);

	private static final Pattern DOWNLOAD_HREF_PATTERN = Pattern.compile("<a href=\"(.*?)\".*?\">BibTeX</a>");

	private static final String WC = ".*";
	private static final String ARTICLE = "Article\\d+";
	private static final String INPROCEEDINGS = "Inproceedings\\d+";
	private static final String BOOK = "Book\\d+";
	private static final String INCOLLECTION = "Incollection\\d+";
	private static final String PROCEEDINGS = "Proceedings\\d+";
	private static final String PHDTHESIS = "Phdthesis\\d+";
	private static final String TECHREPORT = "Techreport\\d+";
	private static final String DELIVERABLE = "Deliverable\\d+";
	private static final String UNPUBLISHED = "Unpublished\\d+";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static {
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + ARTICLE)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + INPROCEEDINGS)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + BOOK)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + INCOLLECTION)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + PROCEEDINGS)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + PHDTHESIS)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + TECHREPORT)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + DELIVERABLE)));
		patterns.add(new Pair<>(Pattern.compile(WC + AIFB_HOST), Pattern.compile(AIFB_WEB + UNPUBLISHED)));
	}

	private static final Pattern EDITOR_PATTERN = Pattern.compile("editor = \"(.*)\",", Pattern.MULTILINE);

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		String pageContent = WebUtils.getContentAsString(url);
		Matcher href_matcher = DOWNLOAD_HREF_PATTERN.matcher(pageContent);
		if (href_matcher.find()){
			return AIFB_HOST_NAME + href_matcher.group(1);

		}
		return null;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		String cleanedBibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", scrapingContext.getUrl().toString());

		//the editors are sometimes seperated by commas instead of "and"
		Matcher m_editor = EDITOR_PATTERN.matcher(bibtex);
		if (!m_editor.find()){
			return cleanedBibtex;
		}
		String editors = m_editor.group(1);
		cleanedBibtex = cleanedBibtex.replace(editors, editors.replaceAll(", ", " and "));

		return cleanedBibtex;
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
	public String getSupportedSiteName() {
		return AIFB_SITE_NAME;
	}
	
	@Override
	public String getSupportedSiteURL() {
		return AIFB_HOST_NAME;
	}
}
