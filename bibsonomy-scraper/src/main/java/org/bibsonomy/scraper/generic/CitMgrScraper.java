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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * abstract scraper for a citation manager that is used by some libraries. You can identify using the final download url
 * for the export (should be /action/downloadCitation).
 *
 * @author dzo
 */
public abstract class CitMgrScraper extends AbstractUrlScraper {

	private static final Pattern DOUBLE_COMMA_FIX = Pattern.compile("},,\n");

	private static String repairBibTeX(final String bibTeX) {
		return DOUBLE_COMMA_FIX.matcher(bibTeX).replaceFirst("},\n");
	}

	@Override
	protected final boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		final URL url = scrapingContext.getUrl();

		try {
			final String doi = this.getDOI(url);
			if (!present(doi)) {
				throw new ScrapingFailureException("can't get doi from url");
			}

			final String downloadUrl = this.getDownloodSiteUrl(url) + "action/downloadCitation";
			final List<NameValuePair> postData = new LinkedList<>();
			postData.add(new BasicNameValuePair("doi", doi));
			postData.add(new BasicNameValuePair("downloadFileName", "pericles_1467981741"));
			postData.add(new BasicNameValuePair("format", "bibtex"));
			postData.add(new BasicNameValuePair("direct", "other-type"));
			postData.add(new BasicNameValuePair("include", "abs"));
			postData.add(new BasicNameValuePair("submit", "Download"));

			final String bibtex = WebUtils.getContentAsString(downloadUrl, null, postData, url.toExternalForm());
			if (present(bibtex)) {
				scrapingContext.setBibtexResult(repairBibTeX(bibtex.trim()));
			}
			return true;
		} catch (final IOException | URISyntaxException e) {
			throw new InternalFailureException(e);
		}
	}

	protected String getDownloodSiteUrl(final URL url) throws ScrapingFailureException {
		return this.getSupportedSiteURL();
	}

	protected String getDOI(URL url) throws URISyntaxException {
		final String doi = DOIUtils.extractDOI(url.getPath());
		return UrlUtils.decodePathSegment(doi);
	}
}
