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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.id.DOIUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * Scraper backed by a Zotero translation-server instance.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerScraper implements Scraper {

	private static final String INFO = "Uses a Zotero translation-server instance to extract bibliographic metadata and export BibTeX.";
	private static final int MAX_SELECTION_LENGTH = 1024;
	private static final Pattern PMID_PATTERN = Pattern.compile("^\\s*(?:pmid:?\\s*)?(\\d{6,9})\\s*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern ARXIV_PATTERN = Pattern.compile("(?i)(?:arxiv:?\\s*)?\\b([0-9]{4}\\.[0-9]{4,5}(?:v[0-9]+)?|[a-z\\-]+(?:\\.[a-z]{2})?/[0-9]{7}(?:v[0-9]+)?)\\b");
	private static final Pattern ACL_PDF_PATTERN = Pattern.compile("(?i)^https?://(?:www\\.aclweb\\.org/anthology|aclanthology\\.org)/([^?#]+)\\.pdf(?:[?#].*)?$");

	private final ZoteroTranslationServerConfig config;
	private final ZoteroTranslationServerClient client;

	/**
	 * Uses configuration from system properties and environment variables.
	 */
	public ZoteroTranslationServerScraper() {
		this(ZoteroTranslationServerConfig.fromEnvironment());
	}

	/**
	 * @param config runtime configuration
	 */
	public ZoteroTranslationServerScraper(final ZoteroTranslationServerConfig config) {
		this(config, new ZoteroTranslationServerClient(config));
	}

	/**
	 * @param client Zotero translation server client
	 */
	public ZoteroTranslationServerScraper(final ZoteroTranslationServerClient client) {
		this(null, client);
	}

	private ZoteroTranslationServerScraper(final ZoteroTranslationServerConfig config, final ZoteroTranslationServerClient client) {
		this.config = config;
		this.client = client;
	}

	@Override
	public boolean scrape(final ScrapingContext scrapingContext) throws ScrapingException {
		if (!this.supportsScrapingContext(scrapingContext)) {
			return false;
		}

		scrapingContext.getTmpMetadata().setZoteroMultipleChoiceCount(0);
		ZoteroTranslationResult result = null;
		final String selectedQuery = this.extractSearchQuery(scrapingContext.getSelectedText());
		if (present(selectedQuery)) {
			result = this.client.translateSearch(selectedQuery);
		}

		final URL url = scrapingContext.getUrl();
		if (result == null && present(url)) {
			result = this.client.translateWebPage(url.toString());
		}

		if (result == null && present(url)) {
			final String normalizedUrl = normalizeUrl(url.toString());
			if (present(normalizedUrl) && !url.toString().equals(normalizedUrl)) {
				result = this.client.translateWebPage(normalizedUrl);
			}
		}

		if (result == null && present(url)) {
			final String query = this.extractSearchQuery(url.toString());
			if (present(query)) {
				result = this.client.translateSearch(query);
			}
		}

		if (result == null || !present(result.getBibTeX())) {
			return false;
		}

		scrapingContext.setBibtexResult(result.getBibTeX());
		scrapingContext.setScraper(this);
		if (result.isMultipleChoice()) {
			scrapingContext.getTmpMetadata().setZoteroMultipleChoiceCount(result.getChoiceCount());
		}
		return true;
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Collection<Scraper> getScraper() {
		return Collections.<Scraper> singleton(this);
	}

	@Override
	public boolean supportsScrapingContext(final ScrapingContext scrapingContext) {
		if (scrapingContext == null) {
			return false;
		}
		if (this.config != null && !this.config.isZoteroEnabled()) {
			return false;
		}
		return present(scrapingContext.getUrl()) || present(this.extractSearchQuery(scrapingContext));
	}

	private static String normalizeUrl(final String url) {
		if (!present(url)) {
			return null;
		}

		final Matcher aclPdfMatcher = ACL_PDF_PATTERN.matcher(url);
		if (aclPdfMatcher.matches()) {
			return "https://aclanthology.org/" + aclPdfMatcher.group(1) + "/";
		}

		return normalizeDblpUrl(url);
	}

	private static String normalizeDblpUrl(final String url) {
		try {
			final URL parsedUrl = new URL(url);
			if (!isDblpHost(parsedUrl.getHost())) {
				return url;
			}

			final String path = parsedUrl.getPath();
			if (!present(path) || !path.startsWith("/rec/")) {
				return url;
			}

			String recordPath = path.substring("/rec/".length());
			recordPath = recordPath.replaceFirst("^(?:bibtex|bib1|bib2|rdf|ris)/", "");
			recordPath = recordPath.replaceFirst("\\.(?:html|xml|rdf|ris|bib)$", "");
			if (!present(recordPath)) {
				return url;
			}
			return "https://dblp.org/rec/" + recordPath + ".html?view=bibtex";
		} catch (final MalformedURLException ex) {
			return url;
		}
	}

	private static boolean isDblpHost(final String host) {
		return "dblp.org".equals(host)
						|| "dblp.uni-trier.de".equals(host)
						|| "dblp2.uni-trier.de".equals(host)
						|| "dblp.dagstuhl.de".equals(host);
	}

	private String extractSearchQuery(final ScrapingContext scrapingContext) {
		if (scrapingContext == null) {
			return null;
		}

		String query = this.extractSearchQuery(scrapingContext.getSelectedText());
		if (!present(query) && present(scrapingContext.getUrl())) {
			query = this.extractSearchQuery(scrapingContext.getUrl().toString());
		}
		return query;
	}

	private String extractSearchQuery(final String text) {
		if (!present(text) || text.length() > MAX_SELECTION_LENGTH) {
			return null;
		}

		final String doi = DOIUtils.extractDOI(text);
		if (present(doi)) {
			return doi;
		}

		final String isbn = ISBNUtils.extractISBN(text);
		if (present(isbn)) {
			return isbn;
		}

		final Matcher pmidMatcher = PMID_PATTERN.matcher(text);
		if (pmidMatcher.matches()) {
			return "pmid:" + pmidMatcher.group(1);
		}

		final Matcher arxivMatcher = ARXIV_PATTERN.matcher(text);
		if (arxivMatcher.find()) {
			return "arXiv:" + arxivMatcher.group(1);
		}

		return null;
	}
}
