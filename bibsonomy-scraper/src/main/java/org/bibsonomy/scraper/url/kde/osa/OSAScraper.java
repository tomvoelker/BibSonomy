package org.bibsonomy.scraper.url.kde.osa;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;

/**
 * @author wbi
 * @version $Id$
 */
public class OSAScraper implements Scraper {

	private static final String info = "Optical Society of America Scraper: This Scraper parses a publication from http://josaa.osa.org/ "+
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String OSA_HOST_NAME  = "http://josaa.osa.org";
	private static final String OSA_BIBTEX_DOWNLOAD_PATH = "/custom_tags/IB_Download_Citations.cfm";
	
	private static final String PATTERN_INPUT = "<input\\b[^>]*>";
	private static final String PATTERN_VALUE = "value=\"[^\"]*\"";
	
	private static final String PATTERN_SELECT = "<select\\b[^>]*>";
	private static final String PATTERN_NAME = "name=\"[^\"]*\"";
	
	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc)
			throws ScrapingException {
		/*
		 * check, if URL is not NULL 
		 */
		if (sc.getUrl() != null) {
			/*
			 * extract URL and check against several (mirror) host names
			 */
			String url = sc.getUrl().toString();
			if(url.startsWith(OSA_HOST_NAME)) {

				String id = null;
				
				Pattern inputPattern = Pattern.compile(PATTERN_INPUT);
				Matcher inputMatcher = inputPattern.matcher(sc.getPageContent());
				
				while(inputMatcher.find()) {
					String input = inputMatcher.group();
					if(input.contains("name=\"articles\"")) {
						Pattern valuePattern = Pattern.compile(PATTERN_VALUE);
						Matcher valueMatcher = valuePattern.matcher(input);
						
						if(valueMatcher.find()) {
							String value = valueMatcher.group();
							id = value.substring(7,value.length()-1);
							break;
						}
					}
				}
				
				String actions = null;
				
				Pattern selectPattern = Pattern.compile(PATTERN_SELECT);
				Matcher selectMatcher = selectPattern.matcher(sc.getPageContent());
				
				while(selectMatcher.find()) {
					String select = selectMatcher.group();
					if(select.contains("name=\"actions")) {
						Pattern valuePattern = Pattern.compile(PATTERN_NAME);
						Matcher valueMatcher = valuePattern.matcher(select);
						
						if(valueMatcher.find()) {
							String name = valueMatcher.group();
							actions = name.substring(5,name.length()-1);
							break;
						}
					}
				}
								
				String bibResult = null;
				
				try {
					URL citUrl = new URL(OSA_HOST_NAME + OSA_BIBTEX_DOWNLOAD_PATH);
					bibResult = getContent(citUrl, getCookies(sc.getUrl()), id, actions);
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				} catch (IOException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibResult != null) {
					sc.setBibtexResult(bibResult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
	
					return true;
				}else
					throw new ScrapingFailureException("getting bibtex failed");

			}
		}
		return false;
	}
	
	private String getContent(URL queryURL, String cookie, String id, String actions) throws IOException {
		/*
		 * get BibTex-File from ACS
		 */
		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);
		urlConn.setRequestMethod("POST");
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		//insert cookie
		urlConn.setRequestProperty("Set-Cookie", cookie);
		
		StringBuffer sbContent = new StringBuffer();
		
		sbContent.append("Articles=");
		sbContent.append(URLEncoder.encode(id,"UTF-8") + "&");
		sbContent.append("ArticleAction=");
		sbContent.append(URLEncoder.encode("save_bibtex2","UTF-8") + "&");
		sbContent.append(actions + "=");
		sbContent.append(URLEncoder.encode("save_bibtex2","UTF-8"));
		
		urlConn.setRequestProperty("Content-Length", String.valueOf(sbContent.length()));
		
		DataOutputStream stream = new DataOutputStream(urlConn.getOutputStream());
		
		stream.writeBytes(sbContent.toString());
		stream.flush();
		stream.close();
		
		urlConn.connect();
		
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		urlConn.disconnect();
		
		return out.toString();
	}
	
	private String getCookies(URL queryURL) throws IOException {
		HttpURLConnection urlConn = null;
		
		urlConn = (HttpURLConnection) queryURL.openConnection();
		
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		
		urlConn.connect();
		/*
		 * extract cookie from connection
		 */
		List<String> cookies = urlConn.getHeaderFields().get("Set-Cookie");
		
		StringBuffer cookieString = new StringBuffer();
		
		for(String cookie : cookies) {
			cookieString.append(cookie.substring(0, cookie.indexOf(";") + 1) + " ");
		}
		
		urlConn.disconnect();
		
		return cookieString.toString();
	}
}
