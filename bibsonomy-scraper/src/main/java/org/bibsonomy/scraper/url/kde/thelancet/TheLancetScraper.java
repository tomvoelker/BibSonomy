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
package org.bibsonomy.scraper.url.kde.thelancet;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Haile
 */
public class TheLancetScraper extends AbstractUrlScraper {
	private static final Log log = LogFactory.getLog(TheLancetScraper.class);
	
	private static final String SITE_NAME = "THE LANCET";
	private static final String SITE_URL = "http://www.thelancet.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "thelancet.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final String  DOWNLOAD_URL = "https://www.thelancet.com/action/downloadCitationSecure";
	private static final Pattern OBJECT_URI_PATTERN = Pattern.compile("article/PIIS(.*?)/");
	private static final RisToBibtexConverter conv = new RisToBibtexConverter();

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteName()
	 */
	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.UrlScraper#getSupportedSiteURL()
	 */
	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.Scraper#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.scraper.AbstractUrlScraper#getUrlPatterns()
	 */
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		//we need the cookies from the first url. If we follow the redirects, we don't get the needed cookies.
		HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
		String url = sc.getUrl().toString();

		try {
			Matcher m_objectURI = OBJECT_URI_PATTERN.matcher(url);
			if (m_objectURI.find()){
				//extracting the objectUri from the url. The objectUri always starts with "pii:S" and after that only consists of numbers.
				String objectUri = "pii:S" + m_objectURI.group(1).replaceAll("[^\\d]", "");
				/*
				the url redirects to https://secure.jbs.elsevierhealth.com/action/getSharedSiteSession where the session cookies can be obtained.
				We can't get these cookies directly.
				 */
				String cookies = WebUtils.getCookies(client, new URL("https://secure.jbs.elsevierhealth.com/action/getSharedSiteSession?rc=0&redirect=" + UrlUtils.safeURIEncode(url)));

				HttpPost post = new HttpPost(DOWNLOAD_URL);
				post.setHeader("Cookie", cookies);
				ArrayList<NameValuePair> postData = new ArrayList<>();
				postData.add(new BasicNameValuePair("objectUri", objectUri));
				postData.add(new BasicNameValuePair("include", "abs"));
				postData.add(new BasicNameValuePair("direct", "true"));
				post.setEntity(new UrlEncodedFormEntity(postData));

				String responseRis = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);
				if (!present(responseRis)){
					throw new ScrapingException("response was empty");
				}
				String bibtex = conv.toBibtex(responseRis);
				sc.setBibtexResult(bibtex);
				return true;

			}else {
				throw new ScrapingException("can't find objectUri in URL: " + url);
			}
		} catch (IOException | HttpException e) {
			throw new ScrapingException(e);
		}
	}

}
