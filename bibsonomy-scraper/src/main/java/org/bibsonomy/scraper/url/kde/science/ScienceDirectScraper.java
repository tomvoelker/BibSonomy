package org.bibsonomy.scraper.url.kde.science;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/** Scraper for ScienceDirect.
 * 
 * @author rja
 *
 */
public class ScienceDirectScraper extends UrlScraper {
	private static final String info = "ScienceDirect Scraper: This scraper parses a publication page from " + href("http://www.sciencedirect.com/", "ScienceDirect");

	private static final String SCIENCE_CITATION_HOST     = "sciencedirect.com";
	private static final String SCIENCE_CITATION_PATH     = "/science";
	private static final String SCIENCE_CITATION_URL     = "http://www.sciencedirect.com/science";
	
	private static final String PATTERN_DOWNLOAD_PAGE_LINK = "<a href=\"(/science\\?_ob=DownloadURL[^\"]*)\"";
	private static final String PATTERN_ACCT = "<input type=hidden name=_acct value=([^>]*)>";
	private static final String PATTERN_ARTICLE_LIST_ID = "<input type=hidden name=_ArticleListID value=([^>]*)>";
	private static final String PATTERN_USER_ID = "&_userid=([^&]*)";
	private static final String PATTERN_UIOKEY = "&_uoikey=([^&]*)";
	private static final String PATTERN_MD5 = "<input type=hidden name=md5 value=([^>]*)>";

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + SCIENCE_CITATION_HOST), Pattern.compile(SCIENCE_CITATION_PATH + ".*")));
	
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);

			// This Scraper might handle the specified url
			try {
				String downloadURl = null;
				
				// article page
				if(sc.getUrl().getPath().startsWith("/science") && sc.getUrl().getQuery().contains("_ob=ArticleURL")){
					String page = sc.getPageContent();
					
					// serach link to download page (there is only one download link on page)
					Pattern patternDownload = Pattern.compile(PATTERN_DOWNLOAD_PAGE_LINK);
					Matcher matcherDownload = patternDownload.matcher(page);
					if(matcherDownload.find())
						downloadURl = matcherDownload.group(1);
					
				// download page
				}else if(sc.getUrl().getPath().startsWith("/science") && sc.getUrl().getQuery().contains("_ob=DownloadURL")){
					downloadURl = sc.getUrl().toString();
				}else
					throw new  PageNotSupportedException("This page is currently not supported.");
				
				String cookie = getCookie("http://www.sciencedirect.com" + downloadURl);
				String downloadPage = getContentWithCookie(new URL("http://www.sciencedirect.com" + downloadURl), cookie);

				String acct = null;
				Pattern patternAcct = Pattern.compile(PATTERN_ACCT);
				Matcher matcherAcct = patternAcct.matcher(downloadPage);
				if(matcherAcct.find())
					acct = matcherAcct.group(1);
				
				String arList = null;
				Pattern patternArList = Pattern.compile(PATTERN_ARTICLE_LIST_ID);
				Matcher matcherArList = patternArList.matcher(downloadPage);
				if(matcherArList.find())
					arList= matcherArList.group(1);
				
				String userId = null;
				Pattern patternUserId = Pattern.compile(PATTERN_USER_ID);
				Matcher matcherUserId = patternUserId.matcher(downloadURl);
				if(matcherUserId.find())
					userId = matcherUserId.group(1);

				String uiokey = null;
				Pattern patternUiokey = Pattern.compile(PATTERN_UIOKEY);
				Matcher matcherUiokey = patternUiokey.matcher(downloadURl);
				if(matcherUiokey.find())
					uiokey = matcherUiokey.group(1);

				String md5 = null;
				Pattern patternMD5 = Pattern.compile(PATTERN_MD5);
				Matcher matcherMD5 = patternMD5.matcher(downloadPage);
				if(matcherMD5.find())
					md5 = matcherMD5.group(1);

				String postContent = null;
				if(acct != null && userId != null && uiokey != null && md5 != null)
					postContent = "_ob=DownloadURL&_method=finish&_acct=" + acct + "&_userid=" + userId + "&_docType=FLA&_ArticleListID=" + arList + "&_uoikey=" + uiokey + "&count=1&md5=" + md5 + "&JAVASCRIPT_ON=Y&format=cite-abs&citation-type=BIBTEX&Export=Export&RETURN_URL=http%3A%2F%2Fwww.sciencedirect.com%2Fscience%2Fhome";
				
				if(postContent != null){
					String bibtex  = null;
					bibtex = getPostContent(new URL(SCIENCE_CITATION_URL), postContent, cookie);
					
					bibtex = bibtex.replace("\r","");
					
					/*
					 * Job done
					 */
					if (bibtex != null) {
						sc.setBibtexResult(bibtex);
						return true;
					} else
						throw new ScrapingFailureException("getting bibtex failed");
				}else
					throw new ScrapingFailureException("Needed ID is missing.");
				
			} catch (MalformedURLException me) {
				throw new InternalFailureException(me);
			} catch (IOException ex) {
				throw new InternalFailureException(ex);
			}
	}


	public String getInfo() {
		return info;
	}
	
	/**
	 * Post request to get RIS
	 * @param queryURL
	 * @param postContent
	 * @return
	 * @throws IOException
	 */
	private String getPostContent(URL queryURL, String postContent, String cookie) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConn.setRequestProperty("Referer", "http://www.sciencedirect.com/science?_ob=DownloadURL&_method=confirm&_uoikey=B6T6R-4SVKSVW-1&count=1&_docType=FLA&_acct=C000065416&_version=1&_userid=4861060&md5=7bdff2d8c4d638ed2de29c1f371976c6");
		urlConn.setRequestProperty("Cookie", cookie);
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */

		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		// write query to body
		StringReader reader = new StringReader(postContent);
		OutputStream write = urlConn.getOutputStream();
		int b;
		while ((b = reader.read()) >= 0) {
			write.write(b);
		}
		write.flush();
		
		// connect
		urlConn.connect();

		// read citation
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		
		return out.toString();
	}

	private String getContentWithCookie(URL queryURL, String cookie) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setRequestProperty("Cookie", cookie);
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */

		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");

		// connect
		urlConn.connect();

		// read citation
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		
		return out.toString();
	}

	/**
	 * Gets the cookie which is needed to extract the content of pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @return The value of the cookie.
	 * @throws IOException
	 */
	private String getCookie(String url) throws IOException{
		HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();

		urlConn.setAllowUserInteraction(true);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setFollowRedirects(true);
		urlConn.setInstanceFollowRedirects(false);
		
		urlConn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();
		
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		
		StringBuffer cookieString = new StringBuffer();
		
		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}
		
		urlConn.disconnect();
		return cookieString.toString();
	}
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
