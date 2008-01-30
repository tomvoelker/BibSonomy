package scraper.url.kde.blackwell;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

/**
 * Scraper for blackwell-synergy.com
 * @author tst
 */
public class BlackwellSynergyScraper implements Scraper {

	/**
	 * scraper info
	 */
	private static final String INFO = "";
	
	/**
	 * pattern for form inputs
	 */
	private static final String PATTERN_INPUT = "<input\\b[^>]*>";
	
	/**
	 * pattern for value attribute
	 */
	private static final String PATTERN_VALUE = "value=\"[^\"]*\"";

	/**
	 * get Info
	 */
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith("blackwell-synergy.com")){
			try {
				String bibtex = null;
				String cookie = getCookie();
				
				// scrape selected snippet
				if(sc.getSelectedText() != null && !sc.getSelectedText().equals("")){
					bibtex = sc.getSelectedText();
				}
				
				// scrape bibtex page
				if(sc.getUrl().toString().contains("action/downloadCitation")){
					bibtex = getPageContent((HttpURLConnection) sc.getUrl().openConnection(), cookie);
				}else{
					// extract link to download page
					
					String currentPage = getPageContent((HttpURLConnection) sc.getUrl().openConnection(), cookie);
					
					// search input fields with doi
					Pattern inputPattern = Pattern.compile(PATTERN_INPUT);
					Matcher inputMatcher = inputPattern.matcher(currentPage);
					
					LinkedList<String> dois = new LinkedList<String>();
					
					while(inputMatcher.find()){
						String input = inputMatcher.group();
						if(input.contains("name=\"doi\"")){
							Pattern valuePattern = Pattern.compile(PATTERN_VALUE);
							Matcher valueMatcher = valuePattern.matcher(input);
							
							// extract doi
							if(valueMatcher.find()){
								String value = valueMatcher.group();
								value = value.substring(7,value.length()-1);
								// store doi
								dois.add(value);
							}
						}
					}
					
					// build download URL
					if(dois.size()>0){
						StringBuffer url = new StringBuffer();
						url.append("http://www.blackwell-synergy.com/action/downloadCitation?");
						url.append("include=abs");
						url.append("&format=bibtex");
						
						// add dois to URL
						for(String doi: dois){
							url.append("&doi=");
							url.append(doi);
						}
						
						// download publications(in bibtex) page
						URL publURL = new URL(url.toString());
						bibtex = getPageContent((HttpURLConnection) publURL.openConnection(), cookie);
					}
				}
				
				// return scraped bibtex
				if(bibtex != null){
					sc.setBibtexResult(bibtex);
					sc.setScraper(this);
					return true;
				}
			} catch (MalformedURLException ex) {
				throw new ScrapingException(ex);
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new ScrapingException(ex);
			}
		}
		return false;
	}

	/**
	 * Gets the cookie which is needed to extract the content of aip pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @param urlConn Connection to api page (from url.openConnection())
	 * @return The value of the cookie.
	 * @throws IOException
	 */
	private String getCookie() throws IOException{
		HttpURLConnection urlConn = (HttpURLConnection) new URL("http://www.blackwell-synergy.com/help").openConnection();
		String cookie = null;
		
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
		
		// extract cookie from header
		Map map = urlConn.getHeaderFields();
		cookie = urlConn.getHeaderField("Set-Cookie");
		if(cookie != null && cookie.indexOf(";") >= 0)
			cookie = cookie.substring(0, cookie.indexOf(";"));
		
		urlConn.disconnect();		
		return cookie;
	}

	/**
	 * Extract the content of a scitation.aip.org page.
	 * (changed code from ScrapingContext.getContentAsString)
	 * @param urlConn Connection to api page (from url.openConnection())
	 * @param cookie Cookie for auth.
	 * @return Content of aip page.
	 * @throws IOException
	 */
	private String getPageContent(HttpURLConnection urlConn, String cookie) throws IOException{

		urlConn.setAllowUserInteraction(true);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		urlConn.setFollowRedirects(true);
		urlConn.setInstanceFollowRedirects(false);
		urlConn.setRequestProperty("Cookie", cookie);

		urlConn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();
							  
		// build content
		StringWriter out = new StringWriter();
		InputStream in = new BufferedInputStream(urlConn.getInputStream());
		int b;
		while ((b = in.read()) >= 0) {
			out.write(b);
		}
		
		urlConn.disconnect();
		in.close();
		out.flush();
		out.close();
		
		return out.toString();
	}
	
}
