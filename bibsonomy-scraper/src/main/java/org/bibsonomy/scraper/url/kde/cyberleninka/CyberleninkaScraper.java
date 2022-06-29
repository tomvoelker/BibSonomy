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
package org.bibsonomy.scraper.url.kde.cyberleninka;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mohammed Abed
 */
public class CyberleninkaScraper extends AbstractUrlScraper {
	private static final String SITE_NAME = "Compare billion project of the Russian Federation Ministry of Culture and Public Initiative";
	private static final String SITE_URL = "http://cyberleninka.ru/";
	private static final String INFO = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String HOST = "cyberleninka.ru";
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN)
	);

	private static final Pattern AUTHOR_PATTERN = Pattern.compile("<meta name=\"citation_author\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern TITLE_PATTERN = Pattern.compile("<meta name=\"citation_title\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern YEAR_PATTERN = Pattern.compile("<meta name=\"citation_publication_date\" content=\"(\\d+)\"\\s*/?>");
	private static final Pattern JOURNAL_PATTERN = Pattern.compile("<meta name=\"citation_journal_title\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern PUBLISHER_PATTERN = Pattern.compile("<meta name=\"citation_publisher\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern ISSUE_PATTERN = Pattern.compile("<meta name=\"citation_issue\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern VOLUME_PATTERN = Pattern.compile("<meta name=\"citation_volume\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern ISSN_PATTERN = Pattern.compile("<meta name=\"citation_issn\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern KEYWORDS_PATTERN = Pattern.compile("<meta name=\"citation_keywords\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern PAGES_PATTERN = Pattern.compile("<meta name=\"eprints.pagerange\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"eprints.abstract\" content=\"(.*?)\"\\s*/?>");
	private static final Pattern URL_PATTERN = Pattern.compile("<link rel=\"canonical\" href=\"(.*?)\"\\s*/?>");

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		HashMap<String, String> bibtexTokens = new HashMap<>();
		final String pageContent = sc.getPageContent();

		Matcher m_title = TITLE_PATTERN.matcher(pageContent);
		if (m_title.find()){
			bibtexTokens.put("title", m_title.group(1));
		}

		Matcher m_author = AUTHOR_PATTERN.matcher(pageContent);
		while (m_author.find()){
			StringUtils.appendIfPresent(bibtexTokens, "author", m_author.group(1), " and ");
		}

		Matcher m_year = YEAR_PATTERN.matcher(sc.getPageContent());
		if (m_year.find()){
			bibtexTokens.put("year", m_year.group(1));
		}

		Matcher m_journal = JOURNAL_PATTERN.matcher(sc.getPageContent());
		if (m_journal.find()){
			bibtexTokens.put("journal", m_journal.group(1));
		}

		Matcher m_publisher = PUBLISHER_PATTERN.matcher(sc.getPageContent());
		if (m_publisher.find()){
			bibtexTokens.put("publisher", m_publisher.group(1));
		}

		Matcher m_issue = ISSUE_PATTERN.matcher(sc.getPageContent());
		if (m_issue.find()){
			bibtexTokens.put("issue", m_issue.group(1));
		}

		Matcher m_volume = VOLUME_PATTERN.matcher(sc.getPageContent());
		if (m_volume.find()){
			bibtexTokens.put("volume", m_volume.group(1));
		}

		Matcher m_issn = ISSN_PATTERN.matcher(sc.getPageContent());
		if (m_issn.find()){
			bibtexTokens.put("issn", m_issn.group(1));
		}

		Matcher m_keywords = KEYWORDS_PATTERN.matcher(sc.getPageContent());
		if (m_keywords.find()){
			bibtexTokens.put("keywords", m_keywords.group(1));
		}

		Matcher m_pages = PAGES_PATTERN.matcher(sc.getPageContent());
		if (m_pages.find()){
			bibtexTokens.put("pages", m_pages.group(1));
		}

		Matcher m_abstract = ABSTRACT_PATTERN.matcher(sc.getPageContent());
		if (m_abstract.find()){
			bibtexTokens.put("abstract", m_abstract.group(1));
		}

		Matcher m_url = URL_PATTERN.matcher(sc.getPageContent());
		if (m_url.find()){
			bibtexTokens.put("url", m_url.group(1));
		}

		//finding all map-entries with missing or undefined values
		ArrayList<String> keysToRemove = new ArrayList<>();
		for (String key : bibtexTokens.keySet()) {
			if (bibtexTokens.get(key).equals("")){
				keysToRemove.add(key);
			}
		}
		//removing all invalid entries
		for (String keyToRemove : keysToRemove) {
			bibtexTokens.remove(keyToRemove);
		}

		String bibtexKey;
		if (bibtexTokens.containsKey("author")||!bibtexTokens.containsKey("doi")){
			bibtexKey = BibTexUtils.generateBibtexKey(
							StringUtils.cyrillicToLatin(bibtexTokens.get("author")),
							null,
							bibtexTokens.get("year"),
							StringUtils.cyrillicToLatin(bibtexTokens.get("title")));
		}else {
			bibtexKey = bibtexTokens.get("doi");
		}

		String bibtex = "@article" + "{" + bibtexKey + ",\n" + BibTexUtils.serializeMapToBibTeX(bibtexTokens) + "\n}";




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
		return PATTERNS;
	}
}
