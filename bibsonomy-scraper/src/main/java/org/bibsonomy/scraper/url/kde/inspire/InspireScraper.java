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
package org.bibsonomy.scraper.url.kde.inspire;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper for INSPIRE. The upgrade of SPIRES.
 * 
 * @author clemens
 */
public class InspireScraper extends AbstractUrlScraper implements ReferencesScraper {
	private static final Log log = LogFactory.getLog(InspireScraper.class);

	private static final String SITE_NAME = "INSPIRE";
	private static final String SITE_URL = "http://inspirehep.net/";
	private static final String INFO = "Gets publications from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + "inspirehep.net"), AbstractUrlScraper.EMPTY_PATTERN)
	);

	private static final Pattern HTML_REFERENCES_PATTERN = Pattern.compile("(?s)<div id=\'referenceinp_link_box\'>(.*)<div id='referenceinp_link_box'>");


	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		try {
			String downloadUrl = "https://inspirehep.net/api/" + sc.getUrl().getPath();
			HttpGet get = new HttpGet(downloadUrl);
			get.setHeader("Accept", "application/x-bibtex");
			String bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
			sc.setBibtexResult(bibtex);
			return true;

		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}

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
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext sc) throws ScrapingException {
		try{
			final Matcher m = HTML_REFERENCES_PATTERN.matcher(WebUtils.getContentAsString(sc.getUrl().toString() + "/references"));
			if(m.find()) {
				sc.setReferences(m.group(1));
				return true;
			}
		}catch(IOException e) {
			log.error("error while getting references" + sc.getUrl(), e);
		}
		return false;
	}
}
