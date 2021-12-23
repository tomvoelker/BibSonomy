package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The scraper is for sites where the download-url ends with action/downloadCitationSecure.
 */

public abstract class CitationManager4Scraper extends AbstractUrlScraper {
	private static final RisToBibtexConverter conv = new RisToBibtexConverter();
	private static final Pattern URL_OBJECT_URI_PATTERN = Pattern.compile("(S\\d{4}-\\d{4}\\(\\d{2}\\)\\d{5}-\\d)");
	private static final String DOWNLOAD_PATH = "action/downloadCitationSecure";

	@Override
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);
		//we need the cookies from the first url. If we follow the redirects, we don't get the needed cookies.
		HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
		String url = sc.getUrl().toString();
		String downloadUrl = this.getSupportedSiteURL() + DOWNLOAD_PATH;

		try {
			Matcher m_objectURI = URL_OBJECT_URI_PATTERN.matcher(url);
			if (m_objectURI.find()){
				//extracting the objectUri from the url. The objectUri always starts with "pii:S" and after that only consists of numbers.
				String objectUri = "pii:S" + m_objectURI.group(1).replaceAll("[^\\d]", "");
				/*
				the url redirects to https://secure.jbs.elsevierhealth.com/action/getSharedSiteSession where the session cookies can be obtained.
				We can't get these cookies directly.
				 */
				String cookies = WebUtils.getCookies(client, new URL("https://secure.jbs.elsevierhealth.com/action/getSharedSiteSession?rc=0&redirect=" + UrlUtils.safeURIEncode(url)));

				HttpPost post = new HttpPost(downloadUrl);
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
