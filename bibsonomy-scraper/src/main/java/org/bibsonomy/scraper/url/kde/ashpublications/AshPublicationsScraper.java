package org.bibsonomy.scraper.url.kde.ashpublications;

import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.generic.GenericBibTeXURLScraper;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AshPublicationsScraper extends GenericBibTeXURLScraper {
	private static final String SITE_NAME = "ASH Publications";
	private static final String SITE_URL = "https://ashpublications.org/";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> URL_PATTERNS = Collections.singletonList(
					new Pair<>(Pattern.compile(".*" + "ashpublications.org"), AbstractUrlScraper.EMPTY_PATTERN
					));
	private static final String DOWNLOAD_URL = "https://ashpublications.org/Citation/Download";
	private static final Pattern ARTICLE_PATTERN = Pattern.compile("article(?:-abstract)?/\\d*/.*?/\\d*/(\\d*)/?");
	private static final Pattern BOOK_PATTERN = Pattern.compile("book/(\\d+)");
	private static final Pattern CHAPTER_PATTERN = Pattern.compile("book/\\d*/chapter(?:-abstract)?/(\\d*)/?");


	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		Matcher m_article = ARTICLE_PATTERN.matcher(url.toString());
		if (m_article.find()){
			return DOWNLOAD_URL + "?resourceType=3&citationFormat=2&resourceId=" + m_article.group(1);
		}else {
			Matcher m_chapter = CHAPTER_PATTERN.matcher(url.toString());
			if (m_chapter.find()){
				return DOWNLOAD_URL + "?resourceType=3&citationFormat=2&resourceId=" + m_chapter.group(1);
			}else {
				Matcher m_book = BOOK_PATTERN.matcher(url.toString());
				if (m_book.find()){
					return DOWNLOAD_URL + "?resourceType=1&citationFormat=2&resourceId=" + m_book.group(1);
				}
			}
		}
		return null;
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

	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return URL_PATTERNS;
	}

}
