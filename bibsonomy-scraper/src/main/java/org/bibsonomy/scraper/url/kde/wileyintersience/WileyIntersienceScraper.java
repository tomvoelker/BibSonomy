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

package org.bibsonomy.scraper.url.kde.wileyintersience;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;


/**
 * Scraper for www3.interscience.wiley.com
 * @author tst
 */
public class WileyIntersienceScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "InterScience";
	private static final String SITE_URL = "http://www3.interscience.wiley.com";
	private static final String INFO = "Extracts publications from the abstract page of " + href(SITE_URL,SITE_NAME) + ".";

	/*
	 * urls and host from intersience.wiley.com 
	 */
	
	private static final String WILEY_INTERSIENCE_HOST = "interscience.wiley.com";
	
	private static final String WILEY_INTERSIENCE_CITEX = "http://www3.interscience.wiley.com/tools/citex?";
	
	private static final String WILEY_INTERSIENCE_CITATION = "http://www3.interscience.wiley.com/tools/citex?clienttype=1&subtype=1&mode=1&version=1&id=";
	
	/*
	 * standard fields for citation form and their values
	 */
	
	private static final String CITATION_FORM_MODE = "mode=2";
	
	private static final String CITATION_FORM_FORMAT = "format=1";
	
	private static final String CITATION_FORM_TYPE = "type=1";
	
	private static final String CITATION_FORM_FILE = "file=1";
	
	/*
	 * pattern for link and its href
	 */
	private static final String PATTERN_LINK = "<a(.*)</a>";
	
	private static final String PATTERN_HREF = "href=\"[^\"]*\"";
	
	// name of citation download link
	private static final String CITATION_DOWNLOAD_LINK = "download citation";

	// pattern for getting id from url between jorunal and abstract
	private static final String PATTERN_GET_ID_JOURNAL_ABSTRACT = "journal/(\\d*)/abstract";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + WILEY_INTERSIENCE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	/**
	 * Scraper for www3.interscience.wiley.com
	 * 
	 * supported page:
	 * - abtsract page
	 * - download citation page
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
			try{
				// get id from citation
				String citationId = sc.getUrl().getPath();
				
				// abstract page
				if(citationId.contains("cgi-bin")){
					citationId = citationId.substring(18, citationId.length()-9);
					
				// download citation page
				}else if(citationId.contains("tools/citex")){
					citationId = sc.getUrl().getQuery();
					int indexId = citationId.indexOf("id=");
					citationId = citationId.substring(indexId+3);
					int indexAnd = citationId.indexOf("&");
					citationId = citationId.substring(0, indexId);
				}
				
				// alternate url pattern for abstract page
				else{
					Pattern idPattern = Pattern.compile(PATTERN_GET_ID_JOURNAL_ABSTRACT);
					Matcher idMatcher = idPattern.matcher(citationId);
					if(idMatcher.find())
						citationId = idMatcher.group(1);
				}
				
				// get session cookie
				HttpURLConnection urlConn = null;
				urlConn = (HttpURLConnection) new URL(WILEY_INTERSIENCE_CITATION + citationId).openConnection();
				String cookie = getCookie(urlConn);
				
				
				// build url to publication data
				String urlCitationEndNote = WILEY_INTERSIENCE_CITEX + CITATION_FORM_MODE + "&" + CITATION_FORM_FORMAT + "&" + CITATION_FORM_TYPE + "&" + CITATION_FORM_FILE; 

				// get publication data
				urlConn = (HttpURLConnection) new URL(urlCitationEndNote).openConnection();
				String endNote = getContentWithCookie(urlConn, cookie);
				
				/*
				 * parse publication data
				 * every line start with a descripor for the bibiliogaphic meaning
				 */ 
				BufferedReader reader = new BufferedReader(new StringReader(endNote));
				String line = reader.readLine();
				
				String author = null;
				String title = null;
				String journal = null;
				String volume = null;
				String number = null;
				String pages = null;
				String year = null;
				String misc = null;
				String url = null;
				String abstractBibtex = null;
				String address = null;
				String publisher = null;
				
				while(line != null){
					// macth discripor with a bibtex field
					
					if(line.startsWith("AU: ")){
						String tmp = line.substring(4);
						
						if(author == null) {
							author = tmp.replaceAll(",", " and");
						} else {
							author = author + " and " + tmp.replaceAll(",", " and");
						}
					}else if(line.startsWith("TI: ")){
						title = line.substring(4);
					}else if(line.startsWith("SO: ")){
						journal = line.substring(4);
					}else if(line.startsWith("VL: ")){
						volume = line.substring(4);
					}else if(line.startsWith("NO: ")){
						number = line.substring(4);
					}else if(line.startsWith("PG: ")){
						pages = line.substring(4);
					}else if(line.startsWith("YR: ")){
						year = line.substring(4);
					}else if(line.startsWith("DOI: ")){
						misc = "doi = {" + line.substring(5) + "}";
					}else if(line.startsWith("US: ")){
						url = line.substring(4);
					}else if(line.startsWith("AB: ")){
						abstractBibtex = line.substring(4);
					}else if(line.startsWith("AD: ")){
						address = line.substring(4);
					}else if(line.startsWith("CP: ")){
						publisher = line.substring(4);
					}
					line = reader.readLine();
				}
				
				StringBuffer bibtex = new StringBuffer();
				bibtex.append("@article{");
				
				if(year != null)
					bibtex.append("wiley" + year + ",\n");
				else
					bibtex.append("wiley0000,\n");
				
				if(author != null)
					bibtex.append("\tauthor = {" + author + "},\n");
				if(title != null)
					bibtex.append("\ttitle = {" + title + "},\n");
				if(journal != null)
					bibtex.append("\tjournal = {" + journal + "},\n");	
				if(volume != null)
					bibtex.append("\tvolume = {" + volume + "},\n");
				if(number != null)
					bibtex.append("\tnumber = {" + number + "},\n");
				if(pages != null)
					bibtex.append("\tpages = {" + pages + "},\n");
				if(year != null)
					bibtex.append("\tyear = {" + year + "},\n");
				if(misc != null)
					bibtex.append("\t" + misc + ",\n");
				if(url != null)
					bibtex.append("\turl = {" + url + "},\n");
				if(abstractBibtex != null)
					bibtex.append("\tabstract = {" + abstractBibtex + "},\n");
				if(address != null)
					bibtex.append("\taddress = {" + address + "},\n");
				if(publisher != null)
					bibtex.append("\tpublisher = {" + publisher + "}\n");

				String bibResult = bibtex.toString();
				bibResult = bibResult.substring(0, bibResult.length()-1) + "\n}";

				// build bibtex and store it in context
				sc.setBibtexResult(bibResult);
				return true;
			}catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}catch (IOException ioe){
				throw new InternalFailureException(ioe);
			}
	}
	
	/**
	 * Gets the cookie which is needed to extract the content of special pages.
	 * (changed code from ScrapingContext.getContentAsString) 
	 * @param urlConn Connection to page (from url.openConnection())
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
		urlConn.setInstanceFollowRedirects(true);
		
		urlConn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		urlConn.connect();
		
		// extract cookie from header
		cookie = urlConn.getHeaderField("Set-Cookie"); 
		if(cookie != null && cookie.indexOf(";") >= 0)
			cookie = cookie.substring(0, cookie.indexOf(";"));
		
		urlConn.disconnect();		
		return cookie;
	}
	
	/**
	 * Extract the content of a of a HttpURLConnection with a cookie.
	 * (changed code from ScrapingContext.getContentAsString)
	 * @param urlConn Connection to page (from url.openConnection())
	 * @param cookie Cookie for auth.
	 * @return Content of page.
	 * @throws IOException
	 */
	private String getContentWithCookie(HttpURLConnection urlConn, String cookie) throws IOException{

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

	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {

return patterns;
}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
	
}
