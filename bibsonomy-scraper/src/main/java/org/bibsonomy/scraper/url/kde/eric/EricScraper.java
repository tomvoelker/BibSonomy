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
package org.bibsonomy.scraper.url.kde.eric;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for papers from http://www.eric.ed.gov/
 * @author tst
 */
public class EricScraper extends AbstractUrlScraper {

	private static final String SITE_URL = "http://www.eric.ed.gov/";
	private static final String SITE_NAME = "Education Resources Information Center";
	private static final String INFO = "Scraper for publications from the " + href(SITE_URL, SITE_NAME)+".";

	private static final String ERIC_HOST = "eric.ed.gov";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + ERIC_HOST), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern TITLE_PATTERN = Pattern.compile("<meta name=\"citation_title\" content=\"(.*?)\" />");
	private static final Pattern AUTHOR_PATTERN = Pattern.compile("<meta name=\"citation_author\" content=\"(.*?)\" />");
	private static final Pattern VOLUME_PATTERN = Pattern.compile("<meta name=\"citation_volume\" content=\"(.*?)\" />");
	private static final Pattern ISSUE_PATTERN = Pattern.compile("<meta name=\"citation_issue\" content=\"(.*?)\" />");
	private static final Pattern FIRST_PAGE_PATTERN = Pattern.compile("<meta name=\"citation_firstpage\" content=\"(.*?)\" />");
	private static final Pattern LAST_PAGE_PATTERN = Pattern.compile("<meta name=\"citation_lastpage\" content=\"(.*?)\" />");
	private static final Pattern KEYWORDS_PATTERN = Pattern.compile("<meta name=\"citation_keywords\" content=\"(.*?)\" />");
	private static final Pattern JOURNAL_PATTERN = Pattern.compile("<meta name=\"citation_journal_title\" content=\"(.*?)\" />");
	private static final Pattern YEAR_PATTERN = Pattern.compile("<meta name=\"citation_publication_date\" content=\"(\\d{4}).*?\" />");
	private static final Pattern ISSN_PATTERN = Pattern.compile("<meta name=\"citation_issn\" content=\"(.*?)\" />");
	private static final Pattern PUBLISHER_PATTERN = Pattern.compile("<meta name=\"citation_publisher\" content=\"(.*?)\" />");
	private static final Pattern ISBN_PATTERN = Pattern.compile("<meta name=\"citation_isbn\" content=\"(.*?)\" />");


	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<div class=\"abstract\">(.*?)</div>");

	private static final Pattern KEY_PATTERN = Pattern.compile("<meta name=\"eric #\" content=\"(.*?)\" />");
	private static final Pattern ENTRY_TYPE_PATTERN = Pattern.compile("<div><strong>Publication Type:</strong>(.*?)</div>");


	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		String pageContent = sc.getPageContent();
		HashMap<String, String> bibtexFields = new HashMap<>();

		Matcher m_title = TITLE_PATTERN.matcher(pageContent);
		if (m_title.find()){
			bibtexFields.put("title", m_title.group(1));
		}

		Matcher m_author = AUTHOR_PATTERN.matcher(pageContent);
		while (m_author.find()){
			StringUtils.appendIfPresent(bibtexFields, "author", m_author.group(1), " and ");
		}

		Matcher m_volume = VOLUME_PATTERN.matcher(pageContent);
		if (m_volume.find()){
			bibtexFields.put("volume", m_volume.group(1));
		}

		Matcher m_issue = ISSUE_PATTERN.matcher(pageContent);
		if (m_issue.find()){
			bibtexFields.put("issue", m_issue.group(1));
		}

		Matcher m_firstPage = FIRST_PAGE_PATTERN.matcher(pageContent);
		Matcher m_lastPage = LAST_PAGE_PATTERN.matcher(pageContent);
		if (m_firstPage.find()&&m_lastPage.find()){
			bibtexFields.put("pages", m_firstPage.group(1) + " - " + m_lastPage.group(1));
		}

		Matcher m_keywords = KEYWORDS_PATTERN.matcher(pageContent);
		if (m_keywords.find()){
			bibtexFields.put("keywords", m_keywords.group(1));
		}

		Matcher m_journal = JOURNAL_PATTERN.matcher(pageContent);
		if (m_journal.find()){
			bibtexFields.put("journal", m_journal.group(1));
		}

		Matcher m_year = YEAR_PATTERN.matcher(pageContent);
		if (m_year.find()){
			bibtexFields.put("year", m_year.group(1));
		}

		Matcher m_issn = ISSN_PATTERN.matcher(pageContent);
		if (m_issn.find()){
			bibtexFields.put("issn", m_issn.group(1));
		}

		Matcher m_abstract = ABSTRACT_PATTERN.matcher(pageContent);
		if (m_abstract.find()){
			bibtexFields.put("abstract", m_abstract.group(1));
		}

		Matcher m_isbn = ISBN_PATTERN.matcher(pageContent);
		if (m_isbn.find()){
			bibtexFields.put("isbn", m_isbn.group(1));
		}

		Matcher m_publisher = PUBLISHER_PATTERN.matcher(pageContent);
		if (m_publisher.find()){
			bibtexFields.put("isbn", m_publisher.group(1));
		}

		bibtexFields.put("url", sc.getUrl().toString());

		String bibtexKey;
		Matcher m_key = KEY_PATTERN.matcher(pageContent);
		if (m_key.find()){
			bibtexKey = m_key.group(1);
		}else {
			bibtexKey = BibTexUtils.generateBibtexKey(bibtexFields.get("author"), null, bibtexFields.get("year"), bibtexFields.get("title"));
		}

		String entryType = BibTexUtils.MISC;
		Matcher m_entryType = ENTRY_TYPE_PATTERN.matcher(pageContent);
		if (m_entryType.find()){
			String publicationType = m_entryType.group(1).trim();
			if (publicationType.contains("Journal Articles")){
				entryType = BibTexUtils.ARTICLE;
			}else if (publicationType.contains("Dissertations/Theses - Doctoral Dissertations")){
				entryType = BibTexUtils.PHD_THESIS;
			}else if (publicationType.contains("Speeches/Meeting Papers")){
				entryType = BibTexUtils.CONFERENCE;
			}else if (publicationType.contains("Books")){
				entryType = BibTexUtils.BOOK;
			}else if (publicationType.contains("Proceedings")){
				entryType = BibTexUtils.PROCEEDINGS;
			}
		}

		String bibtex = "@" + entryType + "{" + bibtexKey + ",\n" + BibTexUtils.serializeMapToBibTeX(bibtexFields) + "\n}";
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

}
