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
package org.bibsonomy.scraper.url.kde.oapen;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class OapenScraper extends AbstractUrlScraper {


	private static final String SITE_URL = "https://oapen.org/";
	private static final String SITE_NAME = "Oapen";
	private static final String info = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME);

	private static final List<Pair<Pattern, Pattern>> PATTERNS = new LinkedList<>();

	static {
		PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*library.oapen.org"), Pattern.compile("handle.*")));
	}


	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			String metadataUrl = "https://library.oapen.org/rest" + sc.getUrl().getPath() + "?expand=metadata";
			HashMap<String, String> bibtexTokens = new HashMap<>();
			JSONObject json = JSONObject.fromObject(WebUtils.getContentAsString(metadataUrl));
			String bibtexKey = json.getString("handle");

			JSONArray metadata = json.getJSONArray("metadata");
			for (Object obj : metadata) {
				JSONObject jsonObject = JSONObject.fromObject(obj);
				String value = jsonObject.getString("value");
				switch (jsonObject.getString("key")){
					case "dc.contributor.author":
						value = value.replaceAll(",$", "");
						concIfPresent(bibtexTokens, "author", value, " and ");
						break;
					case "dc.contributor.editor":
						value = value.replaceAll(",$", "");
						concIfPresent(bibtexTokens, "editor", value, " and ");
						break;
					case "dc.date.issued":
						bibtexTokens.put("year", value);
						break;
					case "dc.identifier.uri":
						bibtexTokens.put("url", value);
						break;
					case "dc.description.abstract":
						bibtexTokens.put("abstract", value);
						break;
					case "dc.title":
						bibtexTokens.put("title", value);
						break;
					case "dc.type":
						bibtexTokens.put("type", value);
						break;
					case "dc.subject.other":
						concIfPresent(bibtexTokens, "keywords", value, ", ");
						break;
					case "oapen.identifier.doi":
						bibtexTokens.put("doi", value);
						break;
					case "publisher.name":
						bibtexTokens.put("publisher", value);
						break;
					case "dc.relation.ispartofseries":
						bibtexTokens.put("series", value);
						break;
				}
			}
			String bibtexEntryType = "misc";
			for (String entrytype : BibTexUtils.ENTRYTYPES) {
				if (entrytype.equalsIgnoreCase(bibtexTokens.get("type"))){
					bibtexEntryType = entrytype;
				}
			}
			String bibtex = "@" + bibtexEntryType + "{"+ bibtexKey +",\n" + BibTexUtils.serializeMapToBibTeX(bibtexTokens) + "\n}";

			sc.setBibtexResult(bibtex);
			return true;
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}


	private void concIfPresent(Map<String, String> tokens, String key, String value, String delimiter){
		if (!tokens.containsKey(key)){
			tokens.put(key, value);
		}else {
			tokens.put(key, tokens.get(key) + delimiter + value);
		}
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
		return info;
	}

}
