package scraper.url.kde.ingenta;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import scraper.Scraper;
import scraper.ScrapingContext;
import scraper.ScrapingException;

public class IngentaconnectScraper implements Scraper{
	
	private static final String info = "Ingentaconnect Scraper: This scraper parses a publication page from <a href=\"http://www.ingentaconnect.com/\">Ingentaconnect</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";
	
	private static final Logger log = Logger.getLogger(IngentaconnectScraper.class);
	
	private static final String INGENTA_CITATION_URL = "http://www.ingentaconnect.com/";

	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && (sc.getUrl().toString().startsWith(INGENTA_CITATION_URL))) {

			// This Scraper might handle the specified url
			try {
				
				/* 
				 * create query URL
				 */
				String URLString = INGENTA_CITATION_URL.substring(0, INGENTA_CITATION_URL.length()-1);
				String page = sc.getPageContent();
				Pattern p = Pattern.compile("BibText Export\" href=\"(.*)\"");
				Matcher m = p.matcher(page);
				if (m.find()) {
					URLString = URLString + m.group(1);
				} else {
					return false;
				}
				
				URL queryURL = new URL(URLString);

				/*
				 * download BibTex-file
				 */
				//String bibResult = sc.getContentAsString(queryURL);
				String bibResult = "";
				try {
					bibResult = getBibTexFromIngenta(queryURL, getCookieFromIngenta());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * fix bibtex
				 */
				//System.out.println(bibResult);
				String[] lines = bibResult.split("\n");
				lines[0] = lines[0].replaceAll(" ", "");
				StringBuffer buffer = new StringBuffer();
				StringBuffer authorBuffer = new StringBuffer("author = \"");
				boolean firstAuthor = true;
				for (int i = 0; i < lines.length-1; i++) {
					//System.out.println(lines[i]);
					//transform author-lines to ONE author-line
					if (lines[i].contains("author")){
						if (firstAuthor){
							authorBuffer.append(lines[i].substring(lines[i].indexOf("\"")+1, lines[i].lastIndexOf("\"")));
							firstAuthor = false;
						}
						else{
							authorBuffer.append(" and " + lines[i].substring(lines[i].indexOf("\"")+1, lines[i].lastIndexOf("\"")));
						}
					}
					else{
						lines[i] = removeHTML(lines[i]);
						//append missing ","
						if (!lines[i].endsWith(",")){
							buffer.append(lines[i] + ",");
						}
						else{
							buffer.append(lines[i]);
						}
					}
				}

				//add author-line
				authorBuffer.append("\"}");
				buffer.append(authorBuffer);
				/*
				 * Job done
				 */
				//System.out.println(buffer.toString());
				sc.setBibtexResult(buffer.toString());
				/*
				* returns itself to know, which scraper scraped this
				*/
				sc.setScraper(this);
				return true;
			} catch (MalformedURLException e) {
				log.fatal("Could not connect to Ingentaconnect: " + e);
				throw new ScrapingException(e);
			}
		}
		// This Scraper can`t handle the specified url
		return false;
	}

	private String removeHTML(String line) {		
		line = line.replaceAll("<.?p>|<.?P>", "");
		line = line.replaceAll("<.?b>|<.?B>", "");
		line = line.replaceAll("<.?i>|<.?I>", "");
		line = line.replaceAll("<.?u>|<.?U>", "");
		line = line.replaceAll("<.?hr>|<.?HR>", "");
		line = line.replaceAll("<.?br>|<.?BR>", "");
		line = line.replaceAll("<.?sup>|<.?SUP>", "");
		line = line.replaceAll("<.?sub>|<.?SUB>", "");
		line = line.replaceAll("&#[0-9]*;", "");
		
		//replace images with alt
		line = line.replaceAll("<[iI][mM][gG] .* [aA][lL][tT]=\"", "");
		line = line.replaceAll("\">", "");
		//remove images without alt
		line = line.replaceAll("<[iI][mM][gG].*>", "");
		
		//remove everything
		String newline = line.replaceAll("<.*>", "");
		if (newline.length() < line.length()){
			System.out.println("DEBUG: irgendwas weggeworfen!");
			System.out.println("ALT: " + line);
			System.out.println("NEU: " + newline);
		}
		
		return newline;
	}
	
	private String getBibTexFromIngenta(URL queryURL, String cookie) throws IOException {
		/*
		 * get BibTex-File from Ingenta
		 */
		HttpURLConnection urlConn = (HttpURLConnection) queryURL.openConnection();
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoInput(true);
		urlConn.setDoOutput(false);
		urlConn.setUseCaches(false);
		/*
		 * set user agent (see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html) since some 
		 * pages require it to download content.
		 */
		urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		//insert cookie
		urlConn.setRequestProperty("Cookie", cookie);
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
	
	private String getCookieFromIngenta () throws IOException {
		/*
		 * receive cookie from springer
		 */
		URL mainURL = new URL(INGENTA_CITATION_URL);
		HttpURLConnection urlConn;
		urlConn = (HttpURLConnection) mainURL.openConnection();
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
		List<String> cookieContent = urlConn.getHeaderFields().get("Set-Cookie");
		//extract sessionID and store in cookie
		
		//TODO
		for (String crumb : cookieContent) {
			//System.out.println(crumb);
			if (crumb.contains("JSESSIONID")){
				return crumb;
			}
		}
		urlConn.disconnect();
		return null;
	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
