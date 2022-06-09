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
package org.bibsonomy.scraper.url.kde.iop;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * SCraper for http://www.iop.org and http://www.ioscience.iop.org/article
 * @author tst
 */
public class IOPScraper extends AbstractUrlScraper {
	/* URL parts */
	private static final String SITE_NAME = "IOP";
	private static final String SITE_URL = "https://iopscience.iop.org";
	private static final String INFO = "Scraper for electronic journals from " + href(SITE_URL, SITE_NAME);
	private static final String NEW_IOP_HOST = "iopscience.iop.org";

	private static final List<Pair<Pattern, Pattern>> patterns = Arrays.asList(
			new Pair<>(Pattern.compile(".*" + NEW_IOP_HOST), Pattern.compile("/article" + ".*"))
	);

	private static final String DOWNLOAD_URL_PATH = "https://iopscience.iop.org/export?";

	private static final List<String> USER_AGENTS = Arrays.asList(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1 Mobile/15E148 Safari/604.1",
					"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1"
	);

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			final URL url = scrapingContext.getUrl();
			// gets the parameters for the get Method from the url, which should be scraped
			String doi = DOIUtils.getDoiFromURL(url);
			if (!present(doi)){
				throw new ScrapingException("doi not found in " + url);
			}
			// doi contains /meta if the url itself contains /meta
			doi = doi.replaceAll("/meta", "");

			final String downloadUrlParams = "doi=" + doi + "&type=article&exportFormat=iopexport_bib&exportType=abs&navsubmit=Export+abstract";
			final String downloadUrl = DOWNLOAD_URL_PATH + downloadUrlParams;

			HttpGet get = new HttpGet(downloadUrl);


			//IOP blocks userAgents, which send to many reqeusts. So we first try out three different userAgents and if  non work we use a random value.
			String bibtex = null;
			for (String userAgent : USER_AGENTS) {
				try {
					get.setHeader("User-Agent", userAgent);
					bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
					if (present(bibtex)){
						break;
					}
				}catch (ClientProtocolException ignored){}
			}
			if (!present(bibtex)){
				get.setHeader("User-Agent", String.valueOf(Math.random()));
				bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), get);
			}
			if (!present(bibtex)){
				throw new ScrapingException("can't get bibtex from " + downloadUrl);
			}

			scrapingContext.setBibtexResult(bibtex);
			return true;
		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}
	}

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
}