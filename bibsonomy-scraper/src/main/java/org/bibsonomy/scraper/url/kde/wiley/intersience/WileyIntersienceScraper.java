package org.bibsonomy.scraper.url.kde.wiley.intersience;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;


/**
 * Scraper for www3.interscience.wiley.com
 * @author tst
 */
public class WileyIntersienceScraper implements Scraper {
	
	private static final String INFO = "WileyIntersienceScraper: Extracts publications from there abstract page.";

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

	/**
	 * Scraper for www3.interscience.wiley.com
	 * 
	 * supported page:
	 * - abtsract page
	 * - download citation page
	 */
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if(sc != null && sc.getUrl() != null && sc.getUrl().getHost().endsWith(WILEY_INTERSIENCE_HOST)){
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
						author = line.substring(4);
						author = author.replaceAll(",", " and");
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
				sc.setScraper(this);
				return true;
			}catch (MalformedURLException e) {
				throw new ScrapingException(e);
			}catch (IOException ioe){
				throw new ScrapingException(ioe);
			}catch(Exception e){
				throw new ScrapingException(e);
			}
		}
		return false;
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

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
}
