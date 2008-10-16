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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.converter.RisToBibtexConverter;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

public class ScienceDirectScraper implements Scraper {
	private static final Logger log = Logger.getLogger(ScienceDirectScraper.class);

	private static final String info = "ScienceDirect Scraper: This scraper parses a publication page from the  <a href=\"http://www.sciencedirect.com/\">ScienceDirect</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String SCIENCE_CITATION_URL     = "http://www.sciencedirect.com/science";
	
	private static final String PATTERN_DOWNLOAD_PAGE_LINK = "<a href=\"(/science\\?_ob=DownloadURL[^\"]*)\"";
	private static final String PATTERN_ACCT = "<input type=hidden name=_acct value=([^>]*)>";
	private static final String PATTERN_ARTICLE_LIST_ID = "<input type=hidden name=_ArticleListID value=([^>]*)>";
	private static final String PATTERN_USER_ID = "&_userid=([^&]*)";
	private static final String PATTERN_UIOKEY = "&_uoikey=([^&]*)";
	private static final String PATTERN_MD5 = "<input type=hidden name=md5 value=([^>]*)>";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(SCIENCE_CITATION_URL)) {
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
					postContent = "_ob=DownloadURL&_method=finish&_acct=" + acct + "&_userid=" + userId + "&_docType=FLA&_ArticleListID=" + arList + "&&_uoikey=" + uiokey + "&count=1&md5=" + md5 + "&JAVASCRIPT_ON=&format=cite-abs&citation-type=RIS&Export=Export&RETURN_URL=http://www.sciencedirect.com/science/home";
				
				if(postContent != null){
					String ris = getPostContent(new URL(SCIENCE_CITATION_URL), postContent);
						
					/*
					 * make RIS to Bibtex
					 * 
					 * 
					 */
	
					String bibtexEntries = new RisToBibtexConverter().RisToBibtex(ris);
	
					/*
					 * Job done
					 */
					if (bibtexEntries != null && !"".equals(bibtexEntries)) {
						sc.setBibtexResult(bibtexEntries);
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
		// This Scraper can`t handle the specified url
		return false;
	}


	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
	/**
	 * Post request to get RIS
	 * @param queryURL
	 * @param postContent
	 * @return
	 * @throws IOException
	 */
	private String getPostContent(URL queryURL, String postContent) throws IOException {

		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlConn.setRequestProperty("Referer", "http://www.sciencedirect.com/science?_ob=DownloadURL&_method=confirm&_uoikey=B6T6R-4SVKSVW-1&count=1&_docType=FLA&_acct=C000065416&_version=1&_userid=4861060&md5=7bdff2d8c4d638ed2de29c1f371976c6");
		urlConn.setRequestProperty("Cookie", "EUID=91c0da74-7b4f-11dd-9777-00000aac593b; MIAMISESSION=8466c6e4-9b85-11dd-b8d0-00000aac4911:3401616575; MIAMIAUTH=2627bde3c3be875724fe63267b124dafb1acc9927b66ff38dc572e307ac416653e0b6623cf4dda4ad4763cff840fa69bec27dcddf7ded5cd64d5ed1ed360908d37bb3f022690f6be1b42442383222e85e5760114fdca146e59667d90da53d92ce02efbfb17648ca0653923b4d9b7c3852200175d5152d6ea5f7df443ba5ee3c02798a86d2fff53686fdbce0005727736abe1ea9ea155046d443e9df1a83fcdfe5e0b596bae61c34d29a2411e4655ed4471517687527faf9d4dec0e18dd128ccea3fdc49fe7b0e87ea1e0bd8c1df8470fb32da21f395c852e6d56aa05742278ec; TARGET_URL=fcf74dd786744d87fbaaaf8652a764ab4a79b0d3ed681139e91069237606310567c0829015f2c9dd545bacad0076bfa36bee441378b06a633a3f8281d740af326d4e8b1b75f7a72ccafec6c5e5b4c9ee556a08ca3daa10dfe1a609c8d2aa3bd561129378ad56f8c493fbfcdb1b369a000d113e87a0682b456e50f0eda52c5c572543bb7b3f6961cf5257a727e77b41013e76026b2d04c2f5b389b34b64dc6e3c2c805ce0984c1d6193f712c2eaaa8f1ea81582e4debecb6d22c8b37e79e11795e0c524de8ebf3686496196ee4fd1c39e; BROWSER_SUPPORTS_COOKIES=1");
		
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
}
