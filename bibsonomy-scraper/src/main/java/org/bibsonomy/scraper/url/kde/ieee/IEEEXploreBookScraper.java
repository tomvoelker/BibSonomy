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
package org.bibsonomy.scraper.url.kde.ieee;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.url.kde.worldcat.WorldCatScraper;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Scraper for IEEE Explore
 * @author rja
 */
public class IEEEXploreBookScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "IEEEXplore Books";
	private static final String SITE_URL = "https://ieeexplore.ieee.org/";
	private static final String IEEE_HOST        = "ieeexplore.ieee.org";
	private static final String IEEE_BOOK_PATH   = "book";

	private static final String info = "This scraper creates a BibTeX entry for the books at " + href(SITE_URL, SITE_NAME);

	private static final Pattern META_DATA_PATTERN = Pattern.compile("<script type=\"text/javascript\">\\s*xplGlobal\\.document\\.metadata=(.*?);\\s*</script>");

	private static final List<Pair<Pattern,Pattern>> patterns = new LinkedList<>(Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + IEEE_HOST), Pattern.compile(IEEE_BOOK_PATH + ".*"))
	));

	@Override
	public boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			String pageContent = WebUtils.getContentAsString(sc.getUrl());
			Matcher m_metaData = META_DATA_PATTERN.matcher(pageContent);

			if (m_metaData.find()){
				JSONObject metaDataJson = JSONObject.fromObject(m_metaData.group(1));
				JSONArray ISBNsJson = metaDataJson.getJSONArray("isbn");

				//tries all isbns until a bibtex is returned
				for (int i = 0; i < ISBNsJson.size(); i++) {
					try {
						String isbn = ISBNsJson.getJSONObject(i).getString("value");
						String bibtex = WorldCatScraper.getBibtexByISBNAndReplaceURL(isbn, sc.getUrl().toString());
						sc.setBibtexResult(bibtex);
						return true;
					}catch (IOException |ScrapingException ignored){}
				}

			}
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
		return false;
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
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

}