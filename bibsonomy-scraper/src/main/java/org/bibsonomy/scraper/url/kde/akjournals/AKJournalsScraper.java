package org.bibsonomy.scraper.url.kde.akjournals;

import static org.bibsonomy.util.ValidationUtils.present;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AKJournalsScraper extends AbstractUrlScraper{
	private static final String SITE_NAME = "AKJournals";
	private static final String SITE_URL = "https://akjournals.com/";
	private static final String INFO =  "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final String HOST = "akjournals.com";

	private static final List<Pair<Pattern, Pattern>> patterns = new LinkedList<>();

	static {
		patterns.add(new Pair<Pattern, Pattern>(Pattern.compile(".*" + HOST), AbstractUrlScraper.EMPTY_PATTERN));
	}

	private static final String DOWNLOAD_URL = "https://akjournals.com/rest/citation/export";
	private static final Pattern JSON_DOCUMENT_URI_PATTERN = Pattern.compile("(/journals.*)");

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		try {
			URL url = WebUtils.getRedirectUrl(scrapingContext.getUrl());
			if (!present(url)){
				url = scrapingContext.getUrl();
			}
			// documentUri ist a part of the path of the url and is needed for the json
			String documentUri;
			Matcher m_documentUri = JSON_DOCUMENT_URI_PATTERN.matcher(url.getPath());
			if (m_documentUri.find()){
				documentUri = m_documentUri.group(1);
			}else {
				throw new ScrapingException("can't find documentUri in " + url.getPath());
			}
			// creating a post-request with the json as body. post returns the bibtex
			HttpPost post = new HttpPost(DOWNLOAD_URL);
			post.setHeader("Content-Type", "application/json");
			String jsonForPost = "{\"format\":\"bibtex\",\"citationExports\":[{\"documentUri\":\""+ documentUri + "\",\"citationId\":null}]}";
			post.setEntity(new StringEntity(jsonForPost));
			String bibtex = WebUtils.getContentAsString(WebUtils.getHttpClient(), post);

			if (!present(bibtex)){
				throw new ScrapingException("can't get bibtex from " + DOWNLOAD_URL);
			}
			scrapingContext.setBibtexResult(bibtex);
			return true;
		} catch (final IOException | HttpException e) {
			throw new ScrapingFailureException(e);
		}
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

	@Override
	public String getInfo() {
		return INFO;
	}
}
