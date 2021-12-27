package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.id.DOIUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Scraper for sites where the client firsts sends a POST-request to the path "/citation/download" with a json of form:
 * "{"contentType":"0","formatType":"2","referenceType":"","urlId":"10.1117/1.JRS.9.097099"}".
 * After that the clients sends a GET-request to the path "/citation/download/" + response of the POST-request
 *
 */
public abstract class CitationManager3Scraper extends AbstractUrlScraper {
	private static final String DOWNLOAD_URLID_PATH = "/citation/download";
	private static final Pattern URL_ID_PATTERN = Pattern.compile("/([A-z]*/\\d*)\\.full");

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);
		try {
			URL url = scrapingContext.getUrl();
			// removes the parameters of the url
			url = new URL(url.getProtocol() + "://" + url.getHost() + url.getPath());

			// the urlId is always a doi. First we try to extract it with DOIUtils, but it has problems if .full follows the doi
			//so we use a extra regex for these cases
			String id = DOIUtils.getDoiFromURL(url);
			if (!present(id)){
				Matcher m_id = URL_ID_PATTERN.matcher(url.toExternalForm());
				if (m_id.find())id = m_id.group(1);
			}else {
				id = id.replaceAll("\\.full|\\.short", "");
			}
			if (!present(id)){
				throw new ScrapingException("id of " + url + " was not found");
			}

			String downloadUrlId = "https://" + url.getHost() + DOWNLOAD_URLID_PATH;
			HttpPost post = new HttpPost(downloadUrlId);
			post.setHeader("Content-Type", "application/json; charset=UTF-8");
			StringEntity postBody = new StringEntity("{\"contentType\":\"1\",\"formatType\":\"2\",\"referenceType\":\"\",\"urlid\":\"" + id + "\"}");
			post.setEntity(postBody);
			String urlId = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);
			if (!present(urlId)){
				throw new ScrapingException("Post to " + downloadUrlId + " with body " + postBody + "did not return urlId");
			}else {
				urlId = urlId.replaceAll("\n", "");
			}

			String fullDownloadUrl = "https://" + url.getHost() + DOWNLOAD_URLID_PATH + "/" + URLEncoder.encode(urlId, "UTF-8");
			String bibtex = WebUtils.getContentAsString(fullDownloadUrl);
			if (!present(bibtex)){
				throw new ScrapingException("bibtex was not returned from " + fullDownloadUrl);
			}

			scrapingContext.setBibtexResult(bibtex);
			return true;
		} catch (HttpException | IOException e) {
			throw new ScrapingException(e);
		}
	}
}
