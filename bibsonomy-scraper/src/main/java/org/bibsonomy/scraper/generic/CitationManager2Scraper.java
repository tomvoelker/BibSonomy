package org.bibsonomy.scraper.generic;

import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
For sites which use a Get-Requests with Citation/Download as Path and resourceId, resourceType, citationFormat as parameters.
 */

public abstract class CitationManager2Scraper extends GenericBibTeXURLScraper {

	private static final Pattern ARTICLE_PATTERN = Pattern.compile("article(?:-abstract)?/(?:doi|\\d*)/.*?/.*?/(\\d*)/?");
	private static final Pattern BOOK_PATTERN = Pattern.compile("book/(\\d+)");
	private static final Pattern CHAPTER_PATTERN = Pattern.compile("book/\\d*/chapter(?:-abstract)?/(\\d*)/?");

	private static final String DOWNLOAD_PATH = "/Citation/Download";


	@Override
	protected String getDownloadURL(URL url, String cookies) throws ScrapingException, IOException {
		Matcher m_article = ARTICLE_PATTERN.matcher(url.toString());
		String downloadParams = null;
		if (m_article.find()) {
			downloadParams = "?resourceType=3&citationFormat=2&resourceId=" + m_article.group(1);
		} else {
			Matcher m_chapter = CHAPTER_PATTERN.matcher(url.toString());
			if (m_chapter.find()) {
				downloadParams = "?resourceType=3&citationFormat=2&resourceId=" + m_chapter.group(1);
			} else {
				Matcher m_book = BOOK_PATTERN.matcher(url.toString());
				if (m_book.find()) {
					downloadParams = "?resourceType=1&citationFormat=2&resourceId=" + m_book.group(1);
				}
			}
		}
		if (present(downloadParams)){
			return normalizeSiteURL(this.getSupportedSiteURL()) + DOWNLOAD_PATH + downloadParams;
		}else {
			return null;
		}
	}
	private static String normalizeSiteURL(String siteURL){
		try {
			URL url = new URL(siteURL);
			return "https://" + url.getHost();
		} catch (MalformedURLException e) {
			return siteURL;
		}
	}
}


