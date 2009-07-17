/**
 *  
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.PageNotSupportedException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.TagStringUtils;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for scitation.aip.org
 * It supports following urls:
 * - http://scitation.aip.org/vsearch/servlet/VerityServlet?
 * - http://scitation.aip.org/getabs/servlet/GetCitation?
 * @author tst
 *
 */
public class AipScitationScraper extends AbstractUrlScraper {


	private static final String INFO = "AipScitationScraper: Extracts publications from " + href("http://scitation.aip.org/", "Scitation") + 
	". Publications can be entered as a marked bibtex snippet or by posting the page of the reference.";

	private static final Pattern hostPattern = Pattern.compile(".*" + "aip.org");
	private static final Pattern pathPattern = AbstractUrlScraper.EMPTY_PATTERN;

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

	private static final Pattern inputPattern = Pattern.compile("<input(.*)>");
	private static final Pattern valuePattern = Pattern.compile("value=\"([^\"]*)\"");
	private static final Pattern namePattern = Pattern.compile("name=\"([^\"]*)\"");
	private static final Pattern keywordsPattern = Pattern.compile("keywords = \\{[^\\}]*\\}");

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

	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(hostPattern, pathPattern));
	
	/**
	 * Extract snippets from a bibtex page and single references from overview pages 
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
		sc.setScraper(this);

		/*
		 * check of snippet
		 * snippet must be from a bibtex page
		 */			
		if(sc.getSelectedText() != null && sc.getUrl().getPath().startsWith(URL_AIP_CITATION_BIBTEX_PAGE_PATH) && sc.getUrl().toString().contains(HTML_INPUT_NAME_FN_AND_VALUE)){
			sc.setBibtexResult(cleanKeywords(sc.getSelectedText()));
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

				String selectcheck = null;
				Pattern selectCheckPattern = Pattern.compile("var cvipsstr = \\\"([^\\\"]*)\\\";");
				Matcher selectCheckMatcher = selectCheckPattern.matcher(aipContent);
				if(selectCheckMatcher.find())
					selectcheck = selectCheckMatcher.group(1);
				
				/*
				 * if bibtex content, then use this content as snippet
				 */
				if(urlConn.getContentType().startsWith(AIP_CONTENT_TYPE_PLAIN)){

					sc.setBibtexResult(cleanKeywords(aipContent));
					return true;

					/*
					 * if html content, build new link to bibtex content
					 */
				}else if(urlConn.getContentType().startsWith(AIP_CONTENT_TYPE_HTML)){
					String aipContent2 = WebUtils.getContentAsString(new URL(URL_AIP_CITATION_PAGE + "journals/help_system/getabs/actions/download_citation_form.jsp"), cookie);
					
					StringBuffer bibtexLink = getBibtexFromAIP(aipContent2, URL_AIP_CITATION_BIBTEX_PAGE, selectcheck);

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
						bibtexLink = getBibtexFromAIP(spieContent, URL_SPIE_AIP_CITATION_BIBTEX_PAGE, selectcheck);
					}

					/*
					 * download and scrape bibtex
					 */
					if(bibtexLink != null){
						urlConn = (HttpURLConnection) new URL(bibtexLink.toString()).openConnection();
						String bibtexResult = getAipContent(urlConn, cookie);
						sc.setBibtexResult(cleanKeywords(bibtexResult));
						return true;
					}else
						throw new ScrapingFailureException("getting bibtex failed");

				}
			} catch (ConnectException cex) {
				throw new InternalFailureException(cex);
			} catch (IOException ioe) {
				throw new InternalFailureException(ioe);
			}
		}
		throw new PageNotSupportedException("AipScitationScraper: Not supported aip page. no bibtex link in html.");
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
		final Matcher valueMatcher = valuePattern.matcher(input);
		if(valueMatcher.find()){

			String value = valueMatcher.group(1);

			// value must be encoded to be used in url
			value = URLEncoder.encode(value, "UTF-8");

			// build parameter for url
			result = inputName + "=" + value;
		}

		return result;
	}

	/** FIXME: refactor
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

	/** FIXME: refactor
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
		final Matcher keywordsMatcher = keywordsPattern.matcher(bibtex);

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

	private StringBuffer getBibtexFromAIP(String aipContent, String aipPath, String selectcheckScript) throws UnsupportedEncodingException{
		// sarch input fields
		final Matcher inputMatcher = inputPattern.matcher(aipContent);

		String prefaction = null;
		String preftype = null;
		String selectcheck = null;
		String source = null;

		//check all input fields
		while(inputMatcher.find()){

			String input = inputMatcher.group(1);

			// check name values
			final Matcher nameMatcher = namePattern.matcher(input);

			if(nameMatcher.find()){

				String name = nameMatcher.group(1);

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
		
		// if selectcheck not found, then try with selectcheck from script block 
		if(selectcheck == null || selectcheck.equals("SelectCheck=null"))
			selectcheck = "SelectCheck=" + selectcheckScript;

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

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}

}
