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
package org.bibsonomy.scraper.url.kde.mendeley;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Haile
 */
public class MendeleyScraper extends AbstractUrlScraper{

	private static final String SITE_NAME = "Mendeley";
	private static final String SITE_URL = "http://mendeley.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(
					new Pair<Pattern, Pattern>(Pattern.compile(".*" + "mendeley.com"), AbstractUrlScraper.EMPTY_PATTERN));

	private static final Pattern TITLE_PATTERN = Pattern.compile("<meta name=\"dc.title\" content=\"(.*?)\" />");
	private static final Pattern YEAR_PATTERN = Pattern.compile("<meta name=\"dc.date\" content=\"(\\d+)\" />");
	private static final Pattern AUTHOR_PATTERN = Pattern.compile("<meta name=\"dc.creator\" content=\"(.*?)\" />");
	private static final Pattern DOI_PATTERN = Pattern.compile("<meta name=\"dc.identifier\" content=\"(.*?)\" />");
	private static final Pattern FIRST_PAGE_PATTERN = Pattern.compile("<meta name=\"citation_firstpage\" content=\"(\\d+)\" />");
	private static final Pattern LAST_PAGE_PATTERN = Pattern.compile("<meta name=\"citation_lastpage\" content=\"(\\d+)\" />");
	private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<meta name=\"description\" content=\"(.*?)\" />");
	private static final Pattern URL_PATTERN = Pattern.compile("<meta property=\"og:url\" content=\"(.*?)\" />");
	private static final Pattern ISSN_PATTERN = Pattern.compile("<meta name=\"dc.issn\" content=\"(.*?)\" />");
	private static final Pattern ISSUE_PATTERN = Pattern.compile("<meta name=\"citation_issue\" content=\"(.*?)\" />");
	private static final Pattern VOLUME_PATTERN = Pattern.compile("<meta name=\"citation_volume\" content=\"(.*?)\" />");
	private static final Pattern KEYWORDS_PATTERN = Pattern.compile("<meta name=\"keywords\" content=\"(.*?)\" />");
	private static final Pattern PUBLISHED_IN_PATTERN = Pattern.compile("<meta name=\"prism.publicationName\" content=\"(.*?)\" />");
	private static final Pattern TYPE_PATTERN = Pattern.compile("<span class=\"document-metadata-type__StyledDocumentMetadataType-sc-1kmejq0-0 eMIxhy\">(.*?)</span>", Pattern.DOTALL);

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		HashMap<String, String> bibtexTokens = new HashMap<>();
		final String pageContent = sc.getPageContent();

		Matcher m_title = TITLE_PATTERN.matcher(pageContent);
		if (m_title.find()){
			bibtexTokens.put("title", m_title.group(1));
		}

		Matcher m_year = YEAR_PATTERN.matcher(pageContent);
		if (m_year.find()){
			bibtexTokens.put("year", m_year.group(1));
		}

		Matcher m_author = AUTHOR_PATTERN.matcher(pageContent);
		while (m_author.find()){
			StringUtils.appendIfPresent(bibtexTokens, "author", m_author.group(1), " and ");
		}

		Matcher m_doi = DOI_PATTERN.matcher(pageContent);
		if (m_doi.find()&& DOIUtils.isDOI(m_doi.group(1))){
			bibtexTokens.put("doi", m_doi.group(1));
		}

		Matcher m_firstpage = FIRST_PAGE_PATTERN.matcher(pageContent);
		Matcher m_lastpage = LAST_PAGE_PATTERN.matcher(pageContent);
		if (m_firstpage.find()&&m_lastpage.find()){
			bibtexTokens.put("pages", m_firstpage.group(1) + "-" + m_lastpage.group(1));
		}

		Matcher m_abstract = ABSTRACT_PATTERN.matcher(pageContent);
		if (m_abstract.find()){
			bibtexTokens.put("abstract", m_abstract.group(1));
		}

		Matcher m_url = URL_PATTERN.matcher(pageContent);
		if (m_url.find()){
			bibtexTokens.put("url", m_url.group(1));
		}

		Matcher m_issn = ISSN_PATTERN.matcher(pageContent);
		if (m_issn.find()){
			bibtexTokens.put("issn", m_issn.group(1));
		}

		Matcher m_volume = VOLUME_PATTERN.matcher(pageContent);
		if (m_volume.find()){
			bibtexTokens.put("volume", m_volume.group(1));
		}

		Matcher m_issue = ISSUE_PATTERN.matcher(pageContent);
		if (m_issue.find()){
			bibtexTokens.put("issue", m_issue.group(1));
		}

		Matcher m_keywords = KEYWORDS_PATTERN.matcher(pageContent);
		if (m_keywords.find()){
			bibtexTokens.put("keywords", m_keywords.group(1));
		}

		String entryType = "misc";
		Matcher m_type = TYPE_PATTERN.matcher(pageContent);
		if (m_type.find()){
			String type = m_type.group(1);
			switch (type.toLowerCase(Locale.ROOT)){
				case "book":
					entryType = "book";
					break;
				case "book chapter":
					entryType = "inbook";
					break;
				case "conference proceedings":
					entryType = "inproceedings";
					break;
				case "thesis":
					entryType = "phdthesis";
					break;
				case "journal article":
				case "newspaper article":
				case "magazine article":
				case "article":
					entryType = "article";
					break;
			}

			Matcher m_publishedIn = PUBLISHED_IN_PATTERN.matcher(pageContent);
			if (m_publishedIn.find()){
				if (entryType.equalsIgnoreCase("inbook")||entryType.equalsIgnoreCase("inproceedings")){
					bibtexTokens.put("booktitle", m_publishedIn.group(1));
				}else if (entryType.equalsIgnoreCase("article")){
					bibtexTokens.put("journal", m_publishedIn.group(1));
				}
			}

		}
		//finding all map-entries with missing or undefined values
		ArrayList<String> keysToRemove = new ArrayList<>();
		for (String key : bibtexTokens.keySet()) {
			if (bibtexTokens.get(key).equals("")||bibtexTokens.get(key).equals("undefined")){
				keysToRemove.add(key);
			}
		}
		//removing all invalid entries
		for (String keyToRemove : keysToRemove) {
			bibtexTokens.remove(keyToRemove);
		}
		String bibtexKey;
		if (bibtexTokens.containsKey("author")||!bibtexTokens.containsKey("doi")){
			bibtexKey = BibTexUtils.generateBibtexKey(bibtexTokens.get("author"), null, bibtexTokens.get("year"), bibtexTokens.get("title"));
		}else {
			bibtexKey = bibtexTokens.get("doi");
		}

		String bibtex = "@" + entryType + "{" + bibtexKey + ",\n" + BibTexUtils.serializeMapToBibTeX(bibtexTokens) + "\n}";
		sc.setBibtexResult(bibtex);
		return true;
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
	public String getInfo() {
		return INFO;
	}	
}
