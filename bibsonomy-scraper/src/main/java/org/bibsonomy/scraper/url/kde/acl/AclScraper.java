/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.acl;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

/**
 * Scraper for aclweb.org, given URL must be show on a PDF
 * 
 * TODO: Problem is that bibtex is only for few papers available 
 * TODO: add
 * @author tst
 */
public class AclScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "Association for Computational Linguistics";

	private static final String SITE_URL = "http://aclweb.org/";

	private static final String INFO = "Scraper for (PDF) references from " + href(SITE_URL, SITE_NAME) + ".";

	private static final String ERROR_CODE_300 = "<TITLE>300 Multiple Choices</TITLE>";

	private static final Pattern hostPattern = Pattern.compile(".*" + "aclweb.org");
	private static final Pattern pathPattern = Pattern.compile("^" + "/anthology-new" + ".*\\.pdf$");
	private static final List<Pair<Pattern, Pattern>> patterns = Collections.singletonList(new Pair<Pattern, Pattern>(hostPattern, pathPattern));

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public boolean scrapeInternal(final ScrapingContext sc)throws ScrapingException {
		sc.setScraper(this);
		String downloadUrl = sc.getUrl().toString();

		// replace .pdf with .bib
		downloadUrl = downloadUrl.substring(0, downloadUrl.length()-4) + ".bib";
		
		try {
			String bibtex = WebUtils.getContentAsString(downloadUrl);
			if (present(bibtex)) {
				if (bibtex.contains(ERROR_CODE_300)) {
					throw new PageNotSupportedException("This aclweb.org page is not supported. BibTeX is not available.");
				}

				// append url
				bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
				
				// add downloaded bibtex to result 
				sc.setBibtexResult(bibtex);
				return true;
			}
		} catch (final MalformedURLException ex) {
			throw new InternalFailureException(ex);
		} catch (final IOException e) {
			throw new InternalFailureException(e);
		}
		
		throw new ScrapingFailureException("getting bibtex failed");
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
