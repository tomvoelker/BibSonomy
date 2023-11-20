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

import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a scraper that uses a regex to find the a bibtex link on the page
 *
 * @author dzo
 */
public abstract class BibTeXLinkOnPageScraper extends GenericBibTeXURLScraper {

	private static final Pattern BIBTEX_PATTERN = Pattern.compile("<a.*href=\"([^\"]+)\".*>BibTeX</a>");

	@Override
	protected String getDownloadURL(final URL url, final String cookies) throws ScrapingException, IOException {
		try {
			final String content = WebUtils.getContentAsString(url, cookies);
			final Matcher m = BIBTEX_PATTERN.matcher(content);
			if (m.find()) {
				final String bibtexUrl = m.group(1);
				// if the url is a relative url
				if (bibtexUrl.startsWith("/")) {
					return UrlUtils.getHostWithProtocol(url) + bibtexUrl;
				}
				return bibtexUrl;
			}
		} catch (final IOException e) {
			throw new ScrapingException(e);
		}
		return null;
	}
}
