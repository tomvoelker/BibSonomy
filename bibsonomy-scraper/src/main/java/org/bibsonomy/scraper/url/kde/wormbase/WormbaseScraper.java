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
package org.bibsonomy.scraper.url.kde.wormbase;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for http://www.wormbase.org
 * @author tst
 */
public class WormbaseScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Wormbase";
	private static final String SITE_URL = "http://www.wormbase.org/";
	private static final String INFO = "Scraper for papers from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "wormbase.org"), AbstractUrlScraper.EMPTY_PATTERN));
	
	private static final Pattern NAME_PATTERN = Pattern.compile("(WBPaper[0-9]*)");

	private static final String DOWNLOAD_URL = "http://www.textpresso.org/cgi-bin/wb/exportendnote?mode=singleentry&lit=C.%20elegans&id=";

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			// get id
			final Matcher matcherName = NAME_PATTERN.matcher(sc.getUrl().toString());
			if(matcherName.find()) {
				final String name = matcherName.group(1);
				String jsonUrl = "https://wormbase.org/rest/widget/paper/" + name + "/overview?download=1&content-type=application/json";
				JSONObject json = JSONObject.fromObject(WebUtils.getContentAsString(jsonUrl)).getJSONObject("fields");
				HashMap<String, String> bibtexFields = new HashMap<>();

				bibtexFields.put("year", json.getJSONObject("year").getString("data"));
				bibtexFields.put("title", json.getJSONObject("title").getString("data"));
				bibtexFields.put("abstract", json.getJSONObject("abstract").getString("data"));
				bibtexFields.put("publisher", json.getJSONObject("publisher").getString("data"));
				bibtexFields.put("editor", extractEditorListFromJson(json.getJSONObject("editors").get("data")));
				bibtexFields.put("pages", json.getJSONObject("pages").getString("data"));
				bibtexFields.put("journal", json.getJSONObject("journal").getString("data"));
				bibtexFields.put("author", extractAuthorListFromJson(json.getJSONObject("authors").get("data")));
				bibtexFields.put("doi", json.getJSONObject("doi").getString("data"));
				bibtexFields.put("volume", json.getJSONObject("volume").getString("data"));
				bibtexFields.put("url", sc.getUrl().toString());

				ArrayList<String> keysToRemove = new ArrayList<>();
				for (String key : bibtexFields.keySet()) {
					if (bibtexFields.get(key)==null||bibtexFields.get(key).equals("null")){
						keysToRemove.add(key);
					}
				}
				//removing all invalid entries
				for (String keyToRemove : keysToRemove) {
					bibtexFields.remove(keyToRemove);
				}

				String bibtexKey = json.getJSONObject("name").getJSONObject("data").getString("id");

				String entryType = "misc";
				String publicationType = json.getJSONObject("publication_type").getJSONArray("data").getString(0).trim().toLowerCase(Locale.ROOT);
				if (publicationType.contains("article")){
					entryType = BibTexUtils.ARTICLE;
				}else if (publicationType.contains("book")){
					entryType = BibTexUtils.BOOK;
				}

				String bibtex = "@" + entryType + "{" + bibtexKey + ",\n" + BibTexUtils.serializeMapToBibTeX(bibtexFields) + "\n}";
				sc.setBibtexResult(bibtex);
				return true;
			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}

		return false;

	}

	private static String extractAuthorListFromJson(Object personsObject){
		String personsString = null;
		if (!personsObject.equals("null")){
			JSONArray personsJsonArray = JSONArray.fromObject(personsObject);
			for (int i = 0; i < personsJsonArray.size(); i++) {
				String person = personsJsonArray.getJSONObject(i).getString("label");
				if (personsString==null){
					personsString = person;
				}else {
					personsString += " and " + person;
				}
			}
		}
		return personsString;
	}

	private static String extractEditorListFromJson(Object personsObject){
		String personsString = null;
		if (!personsObject.equals("null")){
			JSONArray personsJsonArray = JSONArray.fromObject(personsObject);
			for (int i = 0; i < personsJsonArray.size(); i++) {
				String person = personsJsonArray.getString(i);
				if (personsString==null){
					personsString = person;
				}else {
					personsString += " and " + person;
				}
			}
		}
		return personsString;
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
