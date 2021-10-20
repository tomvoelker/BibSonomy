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
package org.bibsonomy.scraper.url.kde.ahajournals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericRISURLScraper;
import org.bibsonomy.util.WebUtils;

/**
 * @author Mohammed Abed
 */
public class AhaJournalsScraper extends GenericRISURLScraper {

	private static final String SITE_NAME = "Aha Journals";
	private static final String SITE_URL = "http://circ.ahajournals.org/";
	private static final String NEW_SITE_URL = "https://www.ahajournals.org/";
	private static final String info = "This scraper parses a publication page of citations from " + href(SITE_URL, SITE_NAME) + ".";
	private static final String AHA_JOURNALS_HOST = "circ.ahajournals.org";
	private static final String NEW_AHA_JOURNALS_HOST = "ahajournals.org";
	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + AHA_JOURNALS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + NEW_AHA_JOURNALS_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}
	private static final Pattern PATTERN_FROM_URL = Pattern.compile(".*/(.*/[circ||CIRC].*)");

	private static final String DOWNLOAD_URL = "https://www.ahajournals.org/action/downloadCitation";
	
	@Override
	protected String getDownloadURL(final URL url, String cookies) throws ScrapingException, IOException {
		return DOWNLOAD_URL;
	}
	protected List<NameValuePair> getDownloadData(final URL url, final String cookies) {
		//if old host is called redirects to new host
		URL redirectedUrl = WebUtils.getRedirectUrl(url);

		String rawPath = null;
		try {
			  rawPath = redirectedUrl.toURI().getRawPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		final Matcher m_doi = PATTERN_FROM_URL.matcher(rawPath);
		String doi = "";
		if(m_doi.find()) doi= m_doi.group(1);

		final List<NameValuePair> postData = new ArrayList<>();
		postData.add(new BasicNameValuePair("doi",doi ));
		//ris can be changed to bibtex, but returned bibtex is malformed
		postData.add(new BasicNameValuePair("format", "ris"));

		return postData;
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

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
}
