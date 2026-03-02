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
package org.bibsonomy.scraper.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.junit.Test;

/**
 * @author rja
 */
public class UnAPIScraperTest {

	@Test
	public void testScrape() {
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
			final int port = server.getAddress().getPort();
			final String unapiUrl = "http://127.0.0.1:" + port + "/unapi";
			server.createContext("/record", exchange -> {
				final String body = "<html><head><link rel=\"unapi-server\" href=\"" + unapiUrl +
					"\" /></head><body><abbr class=\"unapi-id\" title=\"record-1\"></abbr></body></html>";
				send(exchange, 200, body, "text/html; charset=UTF-8");
			});
			server.createContext("/unapi", exchange -> send(exchange, 200,
				"@article{test, title={Synthetic local unAPI record}}\n",
				"text/plain; charset=UTF-8"));
			server.start();

			final UnAPIScraper scraper = new UnAPIScraper();
			final URL url = new URL("http://127.0.0.1:" + port + "/record");
			final ScrapingContext scrapingContext = new ScrapingContext(url);
			scraper.scrape(scrapingContext);

			final String bibtexResult = scrapingContext.getBibtexResult();
			assertNotNull(bibtexResult);
			assertTrue(bibtexResult.contains("@article"));
		} catch (final IOException | ScrapingException ex) {
			fail(ex.getMessage());
		} finally {
			if (server != null) {
				server.stop(0);
			}
		}
	}

	private static void send(final HttpExchange exchange, final int status, final String body, final String contentType)
		throws IOException {
		final byte[] payload = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", contentType);
		exchange.sendResponseHeaders(status, payload.length);
		try (final OutputStream out = exchange.getResponseBody()) {
			out.write(payload);
		}
	}

}
