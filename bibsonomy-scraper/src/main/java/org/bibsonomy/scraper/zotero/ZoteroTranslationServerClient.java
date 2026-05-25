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
package org.bibsonomy.scraper.zotero;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * HTTP client for Zotero translation-server.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerClient {

	private static final Log log = LogFactory.getLog(ZoteroTranslationServerClient.class);

	private static final String WEB_ENDPOINT = "/web";
	private static final String SEARCH_ENDPOINT = "/search";
	private static final String EXPORT_ENDPOINT = "/export?format=bibtex";
	private static final String CONTENT_TYPE_TEXT = "text/plain; charset=UTF-8";
	private static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
	private static final int MAX_ERROR_BODY_LENGTH = 500;

	private final String baseUrl;
	private final HttpClient httpClient;

	/**
	 * @param config runtime configuration
	 */
	public ZoteroTranslationServerClient(final ZoteroTranslationServerConfig config) {
		this(config.getBaseUrl(), config.getConnectTimeout(), config.getSocketTimeout());
	}

	/**
	 * @param baseUrl translation server base URL
	 * @param connectTimeout connect timeout in milliseconds
	 * @param socketTimeout socket timeout in milliseconds
	 */
	public ZoteroTranslationServerClient(final String baseUrl, final int connectTimeout, final int socketTimeout) {
		this.baseUrl = baseUrl;
		final RequestConfig requestConfig = WebUtils.getDefaultRequestConfig()
						.setConnectTimeout(connectTimeout)
						.setSocketTimeout(socketTimeout)
						.setConnectionRequestTimeout(connectTimeout)
						.build();
		this.httpClient = WebUtils.getHttpClient(requestConfig);
	}

	/**
	 * Translate a web page URL.
	 *
	 * @param url URL to translate
	 * @return translation result or <code>null</code> if Zotero has no result
	 * @throws ScrapingException
	 */
	public ZoteroTranslationResult translateWebPage(final String url) throws ScrapingException {
		return this.translatePlain(WEB_ENDPOINT, url);
	}

	/**
	 * Translate an identifier query.
	 *
	 * @param query DOI, ISBN, PMID, arXiv ID, or similar identifier
	 * @return translation result or <code>null</code> if Zotero has no result
	 * @throws ScrapingException
	 */
	public ZoteroTranslationResult translateSearch(final String query) throws ScrapingException {
		return this.translatePlain(SEARCH_ENDPOINT, query);
	}

	private ZoteroTranslationResult translatePlain(final String endpoint, final String body) throws ScrapingException {
		if (!present(body)) {
			return null;
		}

		final ZoteroHttpResponse response = this.post(endpoint, body, CONTENT_TYPE_TEXT);
		if (response.isOk()) {
			return this.export(response.getBody(), false, 0);
		}

		if (response.getStatusCode() == HttpStatus.SC_MULTIPLE_CHOICES) {
			final int choiceCount = this.countChoices(response.getBody());
			if (WEB_ENDPOINT.equals(endpoint)) {
				return this.translateWebChoices(response.getBody(), choiceCount);
			}
			return this.translateSearchChoices(response.getBody(), choiceCount);
		}

		if (isNoResultStatus(response.getStatusCode())) {
			return null;
		}

		throw new ScrapingException("Zotero translation-server returned HTTP " + response.getStatusCode() + ": " + truncate(response.getBody()));
	}

	private ZoteroTranslationResult translateWebChoices(final String choicesJson, final int choiceCount) throws ScrapingException {
		final ZoteroHttpResponse selected = this.post(WEB_ENDPOINT, choicesJson, CONTENT_TYPE_JSON);
		if (selected.isOk()) {
			return this.export(selected.getBody(), true, choiceCount);
		}

		if (isNoResultStatus(selected.getStatusCode())) {
			return null;
		}

		throw new ScrapingException("Zotero multiple-choice selection failed with HTTP " + selected.getStatusCode() + ": " + truncate(selected.getBody()));
	}

	private ZoteroTranslationResult translateSearchChoices(final String choicesJson, final int choiceCount) throws ScrapingException {
		final Object parsed = this.parseJson(choicesJson);
		if (!(parsed instanceof JSONObject)) {
			return null;
		}

		final JSONArray selectedItems = new JSONArray();
		final JSONObject choices = (JSONObject) parsed;
		for (final Object key : choices.keySet()) {
			if (key == null) {
				continue;
			}

			final ZoteroHttpResponse response = this.post(SEARCH_ENDPOINT, key.toString(), CONTENT_TYPE_TEXT);
			if (response.isOk()) {
				this.addItems(selectedItems, this.parseJson(response.getBody()));
			} else {
				log.warn("Zotero text-search choice " + key + " returned HTTP " + response.getStatusCode());
			}
		}

		if (selectedItems.isEmpty()) {
			return null;
		}
		return this.export(selectedItems.toJSONString(), true, choiceCount);
	}

	private ZoteroTranslationResult export(final String itemsJson, final boolean multipleChoice, final int choiceCount) throws ScrapingException {
		if (!present(itemsJson)) {
			return null;
		}

		final ZoteroHttpResponse response = this.post(EXPORT_ENDPOINT, itemsJson, CONTENT_TYPE_JSON);
		if (response.isOk() && present(response.getBody())) {
			return new ZoteroTranslationResult(response.getBody(), multipleChoice, choiceCount);
		}

		if (isNoResultStatus(response.getStatusCode())) {
			return null;
		}

		throw new ScrapingException("Zotero BibTeX export failed with HTTP " + response.getStatusCode() + ": " + truncate(response.getBody()));
	}

	private ZoteroHttpResponse post(final String endpoint, final String body, final String contentType) throws ScrapingException {
		final HttpPost post = new HttpPost(this.baseUrl + endpoint);
		try {
			final StringEntity entity = new StringEntity(body, "UTF-8");
			entity.setContentType(contentType);
			post.setEntity(entity);
			post.setHeader("Accept", "application/json, text/plain, */*");
			final HttpResponse response = this.httpClient.execute(post);
			final HttpEntity responseEntity = response.getEntity();
			final String responseBody = responseEntity == null ? null : EntityUtils.toString(responseEntity, "UTF-8");
			return new ZoteroHttpResponse(response.getStatusLine().getStatusCode(), responseBody);
		} catch (final IOException ex) {
			throw new ScrapingException(ex);
		} finally {
			post.releaseConnection();
		}
	}

	private Object parseJson(final String json) throws ScrapingException {
		try {
			return new JSONParser().parse(json);
		} catch (final ParseException ex) {
			throw new ScrapingException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void addItems(final JSONArray selectedItems, final Object items) {
		if (items instanceof JSONArray) {
			selectedItems.addAll((JSONArray) items);
		} else if (items instanceof JSONObject) {
			selectedItems.add(items);
		}
	}

	private int countChoices(final String json) {
		try {
			final Object parsed = new JSONParser().parse(json);
			if (parsed instanceof JSONArray) {
				return ((JSONArray) parsed).size();
			}
			if (parsed instanceof JSONObject) {
				final JSONObject jsonObject = (JSONObject) parsed;
				final Object items = jsonObject.get("items");
				if (items instanceof JSONArray) {
					return ((JSONArray) items).size();
				}
				if (items instanceof Map) {
					return ((Map<?, ?>) items).size();
				}
				return jsonObject.size();
			}
		} catch (final ParseException ex) {
			log.warn("Could not parse Zotero multiple-choice response", ex);
		}
		return 0;
	}

	private static boolean isNoResultStatus(final int statusCode) {
		return statusCode == HttpStatus.SC_BAD_REQUEST
						|| statusCode == HttpStatus.SC_NOT_FOUND
						|| statusCode == HttpStatus.SC_NOT_IMPLEMENTED
						|| statusCode == HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;
	}

	private static String truncate(final String body) {
		if (!present(body) || body.length() <= MAX_ERROR_BODY_LENGTH) {
			return body;
		}
		return body.substring(0, MAX_ERROR_BODY_LENGTH);
	}

	private static class ZoteroHttpResponse {
		private final int statusCode;
		private final String body;

		private ZoteroHttpResponse(final int statusCode, final String body) {
			this.statusCode = statusCode;
			this.body = body;
		}

		private int getStatusCode() {
			return this.statusCode;
		}

		private String getBody() {
			return this.body;
		}

		private boolean isOk() {
			return this.statusCode >= HttpStatus.SC_OK && this.statusCode < HttpStatus.SC_MULTIPLE_CHOICES;
		}
	}
}
