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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tests the Zotero translation-server HTTP protocol handling.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerClientTest {

	private static final String ITEM_JSON = "[{\"itemType\":\"journalArticle\",\"title\":\"Title\"}]";
	private static final String WEB_CHOICES_JSON = "{\"url\":\"https://example.org/search\",\"session\":\"abc\",\"items\":{\"0\":\"A\",\"1\":\"B\"}}";
	private static final String WEB_SELECTED_JSON = "[{\"itemType\":\"journalArticle\",\"title\":\"A\"},{\"itemType\":\"journalArticle\",\"title\":\"B\"}]";
	private static final String SEARCH_CHOICES_JSON = "{\"10.1234/example-a\":{\"title\":\"A\"},\"10.1234/example-b\":{\"title\":\"B\"}}";
	private static final String BIBTEX_ONE = "@article{key,\n  title = {Title}\n}\n";
	private static final String BIBTEX_TWO = "@article{a,\n  title = {A}\n}\n\n@article{b,\n  title = {B}\n}\n";

	private HttpServer server;
	private String baseUrl;
	private List<RecordedRequest> requests;
	private int webStatus;
	private String webBody;
	private String webSelectedBody;
	private int searchStatus;
	private String searchBody;
	private String exportBody;

	/**
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		this.requests = new ArrayList<RecordedRequest>();
		this.webStatus = 200;
		this.webBody = ITEM_JSON;
		this.webSelectedBody = WEB_SELECTED_JSON;
		this.searchStatus = 200;
		this.searchBody = ITEM_JSON;
		this.exportBody = BIBTEX_ONE;
		this.server = HttpServer.create(new InetSocketAddress(0), 0);
		this.server.createContext("/", exchange -> this.handle(exchange));
		this.server.start();
		this.baseUrl = "http://127.0.0.1:" + this.server.getAddress().getPort();
	}

	/**
	 *
	 */
	@After
	public void tearDown() {
		this.server.stop(0);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testTranslateWebPageExportsBibTeX() throws Exception {
		final ZoteroTranslationServerClient client = new ZoteroTranslationServerClient(this.baseUrl, 1000, 1000);

		final ZoteroTranslationResult result = client.translateWebPage("https://example.org/article");

		assertNotNull(result);
		assertEquals(BIBTEX_ONE, result.getBibTeX());
		assertFalse(result.isMultipleChoice());
		assertEquals(0, result.getChoiceCount());
		assertEquals("/web", this.requests.get(0).path);
		assertEquals("https://example.org/article", this.requests.get(0).body);
		assertEquals("/export", this.requests.get(1).path);
		assertEquals(ITEM_JSON, this.requests.get(1).body);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testTranslateWebPageSelectsAllMultipleChoices() throws Exception {
		this.webStatus = 300;
		this.webBody = WEB_CHOICES_JSON;
		this.exportBody = BIBTEX_TWO;
		final ZoteroTranslationServerClient client = new ZoteroTranslationServerClient(this.baseUrl, 1000, 1000);

		final ZoteroTranslationResult result = client.translateWebPage("https://example.org/search");

		assertNotNull(result);
		assertEquals(BIBTEX_TWO, result.getBibTeX());
		assertTrue(result.isMultipleChoice());
		assertEquals(2, result.getChoiceCount());
		assertEquals("/web", this.requests.get(1).path);
		assertTrue(this.requests.get(1).contentType.startsWith("application/json"));
		assertEquals(WEB_CHOICES_JSON, this.requests.get(1).body);
		assertEquals(WEB_SELECTED_JSON, this.requests.get(2).body);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testTranslateSearchChoicesUsesReturnedIdentifiers() throws Exception {
		this.searchStatus = 300;
		this.searchBody = SEARCH_CHOICES_JSON;
		this.exportBody = BIBTEX_TWO;
		final ZoteroTranslationServerClient client = new ZoteroTranslationServerClient(this.baseUrl, 1000, 1000);

		final ZoteroTranslationResult result = client.translateSearch("query without direct identifier");

		assertNotNull(result);
		assertEquals(BIBTEX_TWO, result.getBibTeX());
		assertTrue(result.isMultipleChoice());
		assertEquals(2, result.getChoiceCount());
		final Set<String> retriedIdentifiers = new HashSet<String>();
		retriedIdentifiers.add(this.requests.get(1).body);
		retriedIdentifiers.add(this.requests.get(2).body);
		assertTrue(retriedIdentifiers.contains("10.1234/example-a"));
		assertTrue(retriedIdentifiers.contains("10.1234/example-b"));
	}

	private void handle(final HttpExchange exchange) throws IOException {
		final String path = exchange.getRequestURI().getPath();
		final String body = readRequestBody(exchange);
		final String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
		this.requests.add(new RecordedRequest(path, body, contentType));

		if ("/web".equals(path)) {
			if (contentType != null && contentType.startsWith("application/json")) {
				write(exchange, 200, "application/json", this.webSelectedBody);
			} else {
				write(exchange, this.webStatus, "application/json", this.webBody);
			}
			return;
		}

		if ("/search".equals(path)) {
			if (this.searchStatus == 300 && this.requests.size() > 1) {
				write(exchange, 200, "application/json", ITEM_JSON);
			} else {
				write(exchange, this.searchStatus, "application/json", this.searchBody);
			}
			return;
		}

		if ("/export".equals(path)) {
			write(exchange, 200, "text/plain", this.exportBody);
			return;
		}

		write(exchange, 404, "text/plain", "not found");
	}

	private static String readRequestBody(final HttpExchange exchange) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = exchange.getRequestBody().read(buffer)) != -1) {
			outputStream.write(buffer, 0, read);
		}
		return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
	}

	private static void write(final HttpExchange exchange, final int status, final String contentType, final String body) throws IOException {
		final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
		exchange.sendResponseHeaders(status, bytes.length);
		exchange.getResponseBody().write(bytes);
		exchange.close();
	}

	private static class RecordedRequest {
		private final String path;
		private final String body;
		private final String contentType;

		private RecordedRequest(final String path, final String body, final String contentType) {
			this.path = path;
			this.body = body;
			this.contentType = contentType;
		}
	}
}
