/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.scraper.url.kde.apa;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig.Builder;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;

/**
 * @author hagen
 */
public class APAScraper extends AbstractUrlScraper {

	private static final String SITE_NAME = "American Psychological Association";
	private static final String SITE_URL = "http://www.apa.org/";
	private static final String INFO = "This scraper parses a publication page from " + href(SITE_URL, SITE_NAME)+".";

	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = new ArrayList<Pair<Pattern,Pattern>>();

	static {
		URL_PATTERNS.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "psycnet.apa.org"), EMPTY_PATTERN));
	}

	private static final Pattern BUY_OPTION_LOCATION_PATTERN = Pattern.compile("fa=buy.*?id=([\\d\\-]++)");

	private static final Pattern UIDS_PAGE_PATTERN = Pattern.compile("<input[^>]*?id=\"srhLstUIDs\"[^>]*?value=\"([^\"]++)");


	private static final RisToBibtexConverter RIS2BIB = new RisToBibtexConverter();


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
		return INFO;
	}

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {

		//Welcome to the story of scraping APA PsycNET
		scrapingContext.setScraper(this);

		//We have to proof the visit of several locations
		final Builder defaultRequestConfig = WebUtils.getDefaultRequestConfig();
		//we have to allow circular redirects to avoid an exception when we get temporary redirected to the login page
		defaultRequestConfig.setCircularRedirectsAllowed(true);
		final HttpClient client = WebUtils.getHttpClient(defaultRequestConfig.build());
		// infinite redirect loops already prevented in WebUtils.getHttpClient()

		//This id is needed to build RIS download link
		String lstUIDs = null;

		//While buy action, the id is contained in the URL requested to scrape
		Matcher m = BUY_OPTION_LOCATION_PATTERN.matcher(scrapingContext.getUrl().toExternalForm());
		if (m.find()) {

			//Pattern matches requested URL
			lstUIDs = m.group(1);

		} else {

			//If scraping request is not during buy action, the id is contained in the page requested to scrape
			String page;
			try {
				page = WebUtils.getContentAsString(client, scrapingContext.getUrl().toExternalForm());
			} catch (final HttpException ex) {
				throw new ScrapingException(ex);
			} catch (IOException ex) {
				throw new ScrapingException(ex);
			}
			//Is the page present?
			if (!present(page)) throw new ScrapingException("Could not get the page requested to scrape");

			//Search id in page
			m = UIDS_PAGE_PATTERN.matcher(page);
			if (m.find()) {
				lstUIDs = m.group(1);
			}
		}
		String ris = null;
		try {

			//Is the id present?
			if (!present(lstUIDs)) throw new ScrapingException("could not find lstUIDs");

			// Build link to RIS download
			final URL risURL = new URL("http://psycnet.apa.org/index.cfm?fa=search.export&id=&lstUids=" + lstUIDs);

			// download RIS exactly two times, because the first request will finally be redirected to a login page
			for (int i = 0; i < 2; i++) {
				ris = WebUtils.getContentAsString(client, risURL.toURI(), null);
				if (ris.contains("Provider: American Psychological Association")) break;
			}
		} catch (final IOException ex) {
			throw new ScrapingException(ex);
		} catch (HttpException ex) {
			throw new ScrapingException(ex);
		} catch (URISyntaxException ex) {
			throw new ScrapingException(ex);
		}

		// convert RIS to BibTeX
		if (!present(ris)) {
			throw new ScrapingException("Could not download citation");
		}
		System.out.println(ris);
		final String bibtex = RIS2BIB.toBibtex(ris);
		System.out.println(bibtex);
		if (!present(bibtex)) {
			throw new ScrapingException("Something went wrong while converting RIS to BibTeX");
		}
		scrapingContext.setBibtexResult(bibtex);

		//success
		return true;
	}

}
