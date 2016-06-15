/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
package org.bibsonomy.scraper.url.kde.acl;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

/**
 * Scraper for aclweb.org, given URL must be show on a PDF
 * 
 * TODO: Problem is that bibtex is only for few papers available 
 * TODO: add
 * @author tst
 */
public class AclScraper extends GenericBibTeXURLScraper {

	private static final String SITE_NAME = "Association for Computational Linguistics";

	private static final String SITE_URL = "http://aclweb.org/";

	private static final String INFO = "Scraper for (PDF) references from " + href(SITE_URL, SITE_NAME) + ".";

	private static final Pattern hostPattern = Pattern.compile(".*" + "aclweb.org");
	private static final Pattern pathPattern = Pattern.compile("^" + "/anthology-new" + ".*\\.pdf$");
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(hostPattern, pathPattern));

	@Override
	public String getInfo() {
		return INFO;
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

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.generic.SimpleGenericURLScraper#getBibTeXURL(java.net.URL)
	 */
	@Override
	public String getDownloadURL(URL url, String cookies) throws ScrapingException {
		String downloadUrl = url.toString();
		// replace .pdf with .bib
		downloadUrl = downloadUrl.substring(0, downloadUrl.length()-4) + ".bib";
		return downloadUrl;
	}

	@Override
	protected String postProcessScrapingResult(ScrapingContext sc, String result){
		 return BibTexUtils.addFieldIfNotContained(result, "url", sc.getUrl().toString());
	}
}
