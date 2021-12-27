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
package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * abstract scraper for a citation manager that is used by some libraries. You can identify using the final download url
 * for the export (should be /action/downloadCitation).
 *
 * @author dzo
 */
public abstract class CitMgrScraper extends AbstractUrlScraper {

	@Override
	protected final boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		URL url = scrapingContext.getUrl();

		try {
			final String doi = DOIUtils.getDoiFromURL(new URL(url.getProtocol(), url.getHost(), url.getPath()));
			if (!present(doi)) {
				throw new ScrapingException("can't get doi from " + url);
			}

			final String downloadUrl = this.getDownloadSiteUrl(url) + getDownloadURLPath();
			final List<NameValuePair> postData = new LinkedList<>();
			postData.add(new BasicNameValuePair("doi", doi));

			Map<String, String> additionalPostData = this.getPostData();

			for (String s : additionalPostData.keySet()) {
				postData.add(new BasicNameValuePair(s, additionalPostData.get(s)));
			}

			String bibtex = WebUtils.getContentAsString(downloadUrl, null, postData, url.toExternalForm());
			if (!present(bibtex)){
				throw new ScrapingException("can't get bibtex from " + url);
			}
			bibtex = postProcessScrapingResult(scrapingContext, bibtex);
			scrapingContext.setBibtexResult(bibtex);

			return true;
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
	}

	protected String postProcessScrapingResult(ScrapingContext scrapingContext, String bibtex) {
		return bibtex;
	}

	protected String getDownloadSiteUrl(final URL url) {
		if (url != null) {
			return url.getProtocol() + "://" + url.getHost();
		}
		return this.getSupportedSiteURL();
	}


	protected String getDownloadURLPath() {
		return "/action/downloadCitation";
	}

	protected Map<String, String> getPostData() {
		HashMap<String, String> postData = new HashMap<>();
		postData.put("format", "bibtex");
		postData.put("include", "abs");
		postData.put("submit", "Download");
		return postData;
	}


}
