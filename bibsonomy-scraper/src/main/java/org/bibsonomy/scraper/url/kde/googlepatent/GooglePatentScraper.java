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
package org.bibsonomy.scraper.url.kde.googlepatent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

/**
 * @author Mohammed Abed
 */
public class GooglePatentScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Google Patente";
	private static final String SITE_URL = "http://www.google.com/patents";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String OLD_GOOGLE_PATENT_HOST = "google.com";
	private static final String NEW_GOOGLE_PATENT_HOST = "patents.google.com";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();
	static {
		patterns.add(new Pair<>(Pattern.compile(OLD_GOOGLE_PATENT_HOST), Pattern.compile("/patents/")));
		patterns.add(new Pair<>(Pattern.compile(NEW_GOOGLE_PATENT_HOST), Pattern.compile("/patent/")));
	}


	private static final Pattern TITLE_PATTERN = Pattern.compile("<meta name=\"DC.title\" content=\"(.*?)\">", Pattern.DOTALL);
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"DC.description\" content=\"(.*?)\">", Pattern.DOTALL);
	private static final Pattern AUTHOR_PATTERN = Pattern.compile("<meta name=\"DC.contributor\" content=\"(.*?)\" scheme=\"inventor\">", Pattern.MULTILINE);
	private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("<meta name=\"DC.date\" content=\"(\\d{4})-(\\d{2})-\\d{2}\">", Pattern.DOTALL);
	private static final Pattern URL_PATTERN = Pattern.compile("<link rel=\"canonical\" href=\"(.*?)\">");


	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String pageContent = sc.getPageContent();
		HashMap<String, String> bibtexTokens = new HashMap<>();

		Matcher m_url = URL_PATTERN.matcher(pageContent);

		if (m_url.find()){
			bibtexTokens.put("url", m_url.group(1));
		}

		Matcher m_title = TITLE_PATTERN.matcher(pageContent);
		if (m_title.find()){
			bibtexTokens.put("title", m_title.group(1).trim());
		}

		Matcher m_abstract = ABSTRACT_PATTERN.matcher(pageContent);
		if (m_abstract.find()){
			bibtexTokens.put("abstract", m_abstract.group(1).trim());
		}

		Matcher m_author = AUTHOR_PATTERN.matcher(pageContent);
		while (m_author.find()){
			if (!bibtexTokens.containsKey("author")){
				bibtexTokens.put("author", m_author.group(1).trim());
			}else {
				bibtexTokens.put("author", bibtexTokens.get("author") + " and " + m_author.group(1).trim());
			}
		}

		Matcher m_yearMonth = YEAR_MONTH_PATTERN.matcher(pageContent);
		if (m_yearMonth.find()){
			bibtexTokens.put("year", m_yearMonth.group(1).trim());
			bibtexTokens.put("month", m_yearMonth.group(2).trim());
		}

		String bibtexKey = BibTexUtils.generateBibtexKey(bibtexTokens.get("author"), null, bibtexTokens.get("year"), bibtexTokens.get("title"));
		String bibtex = "@patent{" + bibtexKey + ",\n" + BibTexUtils.serializeMapToBibTeX(bibtexTokens) + "\n}";
		sc.setBibtexResult(bibtex);

		return true;
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
		return patterns;
	}

}
