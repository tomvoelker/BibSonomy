package org.bibsonomy.scraper.url.kde.agu;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
import org.springframework.scripting.ScriptCompilationException;

/**
 * Scraper for publications from http://www.agu.org/pubs/ using the RIS export
 * @author tst
 * @version $Id$
 */
public class AGUScraper extends AbstractUrlScraper {
	
	private static final String INFO = "AGU Scraper: For Publications from the " + href("http://www.agu.org/pubs", "American Geophysical Union (AGU)");
	
	private static final String HOST = "agu.org";
	
	private static final String PATH = "/pubs";
	
	private Pattern patternDownloadUrl = Pattern.compile("href=\"([^\\\"]*)\">Export RIS Citation");
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + HOST), Pattern.compile(PATH + ".*")));

	@Override
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

	@Override
	protected boolean scrapeInternal(ScrapingContext scrapingContext)
			throws ScrapingException {
		scrapingContext.setScraper(this);
		
		String pageContent = null;
		pageContent = scrapingContext.getPageContent();
		
		if(pageContent != null){
			// get download url
			String downloadUrl = null;
			Matcher matcherDownloadUrl = patternDownloadUrl.matcher(pageContent);
			if(matcherDownloadUrl.find())
				downloadUrl = "http://www.agu.org" + matcherDownloadUrl.group(1);
			else
				throw new PageNotSupportedException("This AGU page is not supported.");
			
			if(downloadUrl!=null){
				
				// get RIS citation
				String ris = null;
				try {
					/*
					 * little bug fix:
					 * decode &amp; to & because it seems that AGU not decodes incoming URLs by
					 * itself (without this replacement the result is a error and not a RIS citation)
					 */
					ris = WebUtils.getContentAsString(new URL(downloadUrl.replace("&amp;", "&")));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				} catch (IOException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(ris != null){
					// convert ris to bibtex
					String bibtex = null;
					RisToBibtexConverter converter = new RisToBibtexConverter();
					bibtex = converter.RisToBibtex(ris);
					
					if(bibtex != null){
						// finish
						scrapingContext.setBibtexResult(bibtex);
						return true;
					}else
						throw new ScrapingFailureException("Converting RIS to bibtex failed");
					
				}else
					throw new ScrapingFailureException("Cannot get RIS citation.");
				
			}else
				return false;
			
		}else
			throw new ScrapingFailureException("Cannot download content from " + scrapingContext.getUrl().toString());
		
	}

	public String getInfo() {
		return INFO;
	}

}
