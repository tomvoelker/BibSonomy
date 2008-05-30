package org.bibsonomy.scraper.url.kde.aip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.TagStringUtils;


/**
 * Scraper for scitation.aip.org
 * It supports following urls:
 * - http://scitation.aip.org/vsearch/servlet/VerityServlet?
 * - http://scitation.aip.org/getabs/servlet/GetCitation?
 * @author tst
 *
 */
public class AipScitationScraper implements Scraper {
	
	private static final String INFO = "AipScitationScraper: Extracts publications from scitation.aip.org . Publications can be entered as a marked bibtex snippet or by posting the page of the reference.";
	
	/*
	 * urls and parts of urls
	 */
	private static final String URL_AIP_HOST = "aip.org";
	private static final String URL_AIP_CITATION_PAGE = "http://scitation.aip.org/";
	private static final String URL_AIP_CITATION_BIBTEX_PAGE_PATH = "/getabs/servlet/GetCitation";
	private static final String URL_AIP_CITATION_BIBTEX_PAGE = "http://scitation.aip.org/getabs/servlet/GetCitation?";
	private static final String URL_SPIE_AIP_CITATION_BIBTEX_PAGE = "http://spiedl.aip.org/getabs/servlet/GetCitation?";
	private static final String URL_DOI = "http://dx.doi.org/";
	
	/*
	 * supported mime types
	 */
	private static final String AIP_CONTENT_TYPE_PLAIN = "text/plain";
	private static final String AIP_CONTENT_TYPE_HTML = "text/html";
	
	/*
	 * regular expressions
	 */
	private static final String PATTERN_INPUT = "<input(.*)>";
	private static final String PATTERN_VALUE = "value=\"[^\"]*\"";
	private static final String PATTERN_NAME = "name=\"[^\"]*\"";
	private static final String PATTERN_KEYWORDS = "keywords = \\{[^\\}]*\\}";
	
	/*
	 * html fields with static values
	 */
	private static final String HTML_INPUT_NAME_FN_AND_VALUE = "fn=view_bibtex2";
	private static final String HTML_INPUT_NAME_DOWNLOADCITATION_AND_VALUE = "downloadcitation=+Go+";
	
	/*
	 * html fields with dynamic values
	 */
	private static final String HTML_INPUT_NAME_SOURCE = "source";
	private static final String HTML_INPUT_NAME_PREFTYPE = "PrefType";
	private static final String HTML_INPUT_NAME_PREFACTION = "PrefAction";
	private static final String HTML_INPUT_NAME_SELECTCHECK = "SelectCheck";

	/*
	 * link bevor doi 
	 */
	private static final String LINK_BEVOR_DOI = "<a href=\"http://scitation.aip.org/jhtml/doi.jsp\">doi:</a>";
	
	/**
	 * Extract snippets from a bibtex page and single references from overview pages 
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(URL_AIP_HOST)){
			
			/*
			 * check of snippet
			 * snippet must be from a bibtex page
			 */			
			if(sc.getSelectedText() != null && sc.getUrl().getPath().startsWith(URL_AIP_CITATION_BIBTEX_PAGE_PATH) && sc.getUrl().toString().contains(HTML_INPUT_NAME_FN_AND_VALUE)){
				sc.setBibtexResult(cleanKeywords(sc.getSelectedText()));
				sc.setScraper(this);
				return true;
				
			/*
			 * no snippet, check content from url
			 */
			}else{
				

				HttpURLConnection urlConn = null;
				try {
					
					// get cookie data for auth
					urlConn = (HttpURLConnection) sc.getUrl().openConnection();
					String cookie = getCookie(urlConn);

					// get page content
					urlConn = (HttpURLConnection) sc.getUrl().openConnection();
					String aipContent = getAipContent(urlConn, cookie);
					
					/*
					 * if bibtex content, then use this content as snippet
					 */
					if(urlConn.getContentType().startsWith(AIP_CONTENT_TYPE_PLAIN)){
						
						sc.setBibtexResult(cleanKeywords(aipContent));
						sc.setScraper(this);
						return true;
						
					/*
					 * if html content, build new link to bibtex content
					 */
					}else if(urlConn.getContentType().startsWith(AIP_CONTENT_TYPE_HTML)){
						
						StringBuffer bibtexLink = getBibtexFromAIP(aipContent, URL_AIP_CITATION_BIBTEX_PAGE);
						
						// may be a spie link
						if(bibtexLink == null){
							//extract doi
							int indexOfDOILink = aipContent.indexOf(LINK_BEVOR_DOI) + LINK_BEVOR_DOI.length();
							String startDOI = aipContent.substring(indexOfDOILink);
							String doi = startDOI.substring(0, startDOI.indexOf("\n"));
							
							URL doiURL = new URL(URL_DOI + doi);
							HttpURLConnection doiConn = (HttpURLConnection) doiURL.openConnection();
							URL spieURL = new URL(getSpieLink(doiConn));
							URL spieURL2 = new URL(getSpieLink((HttpURLConnection) spieURL.openConnection()));
							
							// build cookie
							cookie = getCookie((HttpURLConnection) spieURL2.openConnection());
							
							// get SPIE Page which is referenced by DOI
							String spieContent = getAipContent((HttpURLConnection) spieURL2.openConnection(), cookie);
							bibtexLink = getBibtexFromAIP(spieContent, URL_SPIE_AIP_CITATION_BIBTEX_PAGE);
						}
						
						/*
						 * download and scrape bibtex
						 */
						if(bibtexLink != null){
							urlConn = (HttpURLConnection) new URL(bibtexLink.toString()).openConnection();
							String bibtexResult = getAipContent(urlConn, cookie);
							sc.setBibtexResult(cleanKeywords(bibtexResult));
							sc.setScraper(this);
							return true;
						}
						
					}
				} catch (ConnectException cex) {
					throw new ScrapingException(cex);
				} catch (IOException ioe) {
					throw new ScrapingException(ioe);
				}
			}
			throw new ScrapingException("AipScitationScraper: Not supported aip page. no bibtex link in html.");
		}
		return false;
	}
	
	/**
	 * Extract the value of the "value" attribute.
	 * @param input The input element as string.
	 * @param inputName The value of the name attribute.
	 * @return The value of the "value" attribute.
	 * @throws UnsupportedEncodingException
	 */
	private String getInputValue(String input, String inputName) throws UnsupportedEncodingException{
		String result = null;
		
		// search value attribute
		Pattern valuePattern = Pattern.compile(PATTERN_VALUE);
		Matcher valueMatcher = valuePattern.matcher(input);
		if(valueMatcher.find()){
			
			String value = valueMatcher.group();
			value = value.substring(7, value.length()-1);
			
			// value must be encoded to be used in url
			value = URLEncoder.encode(value, "UTF-8");
			
			// build parameter for url
			result = inputName + "=" + value;
		}
		
		return result;
	}
	
	/**
	 * Gets the cookie which is needed to extract the content of aip pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @param urlConn Connection to api page (from url.openConnection())
	 * @return The value of the cookie.
	 * @throws IOException
	 */
	private String getCookie(HttpURLConnection urlConn) throws IOException{
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
	private String getAipContent(HttpURLConnection urlConn, String cookie) throws IOException{

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
	
	/**
	 * The keywords field in bibtex references from scitation.aip.org are using ";" as delimiter. But it must be space seperated.
	 * @param bibtex Extracted bibtex content.
	 * @return Extracted bibtex content with valid keywords
	 */
	private String cleanKeywords(String bibtex){
		String result = bibtex;
		
		// storage for the parts of the bibtex reference
		String firstPart = null;
		String keywords = null;
		String secondPart = null;
		
		// search keywords field
		Pattern keywordsPattern = Pattern.compile(PATTERN_KEYWORDS);
		Matcher keywordsMatcher = keywordsPattern.matcher(bibtex);
				
		if(keywordsMatcher.find()){
			// cut reference in 3 pieces
			keywords = keywordsMatcher.group();
			firstPart = bibtex.substring(0, bibtex.indexOf(keywords));
			secondPart = bibtex.substring(bibtex.indexOf(keywords) + keywords.length());
			
			// get the value of the keywords field
			keywords = keywords.substring(12, keywords.length()-1);
			
			// clean tag string
			keywords = TagStringUtils.cleanTags(keywords, true, ";", "_");
			
			// join the parts back to a complete bibtex reference
			result = firstPart + "keywords = {" + keywords + "}" + secondPart;
		}
		
		return result;
	}
	
	private StringBuffer getBibtexFromAIP(String aipContent, String aipPath) throws UnsupportedEncodingException{
		// sarch input fields
		Pattern inputPattern = Pattern.compile(PATTERN_INPUT);
		Matcher inputMatcher = inputPattern.matcher(aipContent);
		
		String prefaction = null;
		String preftype = null;
		String selectcheck = null;
		String source = null;
		
		//check all input fields
		while(inputMatcher.find()){
			
			String input = inputMatcher.group();

			// check name values
			Pattern namePattern = Pattern.compile(PATTERN_NAME);
			Matcher nameMatcher = namePattern.matcher(input);
			
			if(nameMatcher.find()){
				
				String name = nameMatcher.group();
				name = name.substring(6, name.length()-1);
				
				// if name is supported, then extract its value
				if(name.contains(HTML_INPUT_NAME_PREFACTION)){
					prefaction = getInputValue(input, HTML_INPUT_NAME_PREFACTION);
				}else if(name.contains(HTML_INPUT_NAME_PREFTYPE)){
					preftype = getInputValue(input, HTML_INPUT_NAME_PREFTYPE);
				}else if(name.contains(HTML_INPUT_NAME_SELECTCHECK)){
					selectcheck = getInputValue(input, HTML_INPUT_NAME_SELECTCHECK);
				}else if(name.contains(HTML_INPUT_NAME_SOURCE)){
					source = getInputValue(input, HTML_INPUT_NAME_SOURCE);
				}
			}
		}
		
		/*
		 * build bibtex link
		 */
		StringBuffer bibtexLink = null;
		if(source != null && preftype != null && prefaction != null && selectcheck != null){
			bibtexLink = new StringBuffer(aipPath);
			bibtexLink.append(HTML_INPUT_NAME_FN_AND_VALUE);
			bibtexLink.append("&");
			bibtexLink.append(prefaction);
			bibtexLink.append("&");
			bibtexLink.append(preftype);
			bibtexLink.append("&");
			bibtexLink.append(selectcheck);
			bibtexLink.append("&");
			bibtexLink.append(source);
			bibtexLink.append("&");
			bibtexLink.append(HTML_INPUT_NAME_DOWNLOADCITATION_AND_VALUE);
		}
		
		return bibtexLink;
	}

	private String getSpieLink(HttpURLConnection urlConn) throws IOException{
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
		
		String spieLink = urlConn.getHeaderField("Location");
		urlConn.disconnect();
		
		return spieLink;
	}
	
	public String getInfo() {
		return INFO;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
