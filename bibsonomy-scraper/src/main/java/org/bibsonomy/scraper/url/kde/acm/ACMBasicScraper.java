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
package org.bibsonomy.scraper.url.kde.acm;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.CitedbyScraper;
import org.bibsonomy.scraper.ReferencesScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.CslToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.AbstractGenericFormatURLScraper;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

/**
 * Scrapes the ACM digital library
 *
 * @author rja
 * @author dzo
 */
public class ACMBasicScraper extends AbstractGenericFormatURLScraper implements ReferencesScraper, CitedbyScraper {
	private static final Log log = LogFactory.getLog(ACMBasicScraper.class);
	
	private static final String ACM_BASE_TAB_URL = "https://dl.acm.org/tab_";
	private static final String SITE_NAME = "ACM Digital Library";
	private static final String SITE_URL = "https://dl.acm.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final String CACM_DOMAIN = "cacm.acm.org";
	
	private static final List<Pair<Pattern,Pattern>> patterns = Arrays.asList(
		new Pair<>(
				Pattern.compile(".*" + CACM_DOMAIN),
				Pattern.compile("/magazines/*")
				),
				
		new Pair<>(
				Pattern.compile("dl.acm.org"),
				EMPTY_PATTERN
		)
	);

	private final CslToBibtexConverter cslToBibtexConverter = new CslToBibtexConverter();

	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		return "https://dl.acm.org/action/exportCiteProcCitation";
	}

	@Override
	protected List<NameValuePair> getDownloadData(URL url, String cookies) {
		final String doi = DOIUtils.extractDOI(url.getPath());

		return Arrays.asList(
						new BasicNameValuePair("dois", doi),
						new BasicNameValuePair("targetFile", "custom-bibtex"),
						new BasicNameValuePair("format", "bibTex")
		);
	}

	@Override
	protected boolean retrieveCookiesFromSite() {
		return true;
	}

	@Override
	protected String convert(final String downloadResult) {
		// this is a json containing the csl style and also the items to render,
		// so extract the csl entries from the json response
		final JSON parsedJson = JSONSerializer.toJSON(downloadResult);
		if (parsedJson instanceof JSONObject) {
			final JSONObject json = (JSONObject) parsedJson;
			final JSONObject cslEntries = json.getJSONArray("items").getJSONObject(0);
			final Optional<Object> firstKey = cslEntries.keySet().stream().findFirst();
			if (firstKey.isPresent()) {
				final Object key = firstKey.get();
				final JSONObject cslEntry = cslEntries.getJSONObject(key.toString());
				return this.cslToBibtexConverter.toBibtex(cslEntry);
			}
		}
		return null;
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
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.CitedbyScraper#scrapeCitedby(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeCitedby(ScrapingContext scrapingContext) throws ScrapingException {
		return scrapeMetaData(scrapingContext, "citings");
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.ReferencesScraper#scrapeReferences(org.bibsonomy.scraper.ScrapingContext)
	 */
	@Override
	public boolean scrapeReferences(ScrapingContext scrapingContext) throws ScrapingException {
		return scrapeMetaData(scrapingContext, "references");
	}

	private static boolean scrapeMetaData(ScrapingContext scrapingContext, final String kind) {
		final HttpClient client = WebUtils.getHttpClient();
		final String id = scrapingContext.getTmpMetadata().getId();
		try {
			final String uri = ACM_BASE_TAB_URL + kind +  ".cfm?id=" + id;
			final String reference = WebUtils.getContentAsString(client, uri, null, null, null);
			if (present(reference)) {
				scrapingContext.setReferences(reference);
				scrapingContext.setCitedBy(reference);
				return true;
			}
		} catch(final Exception e) {
			log.warn("error while scraping references by for " + scrapingContext.getUrl(), e);
		}
		return false;
	}
}