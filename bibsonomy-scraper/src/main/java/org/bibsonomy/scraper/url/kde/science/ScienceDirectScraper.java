package org.bibsonomy.scraper.url.kde.science;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.RisToBibtexConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class ScienceDirectScraper implements Scraper {
	private static final Logger log = Logger.getLogger(ScienceDirectScraper.class);

	private static final String info = "ScienceDirect Scraper: This scraper parses a publication page from the  <a href=\"http://www.sciencedirect.com/\">ScienceDirect</a>  " +
	"and extracts the adequate BibTeX entry. Author: KDE";

	private static final String SCIENCE_HOST_NAME        = "http://www.sciencedirect.com";
	private static final String SCIENCE_CITATION_URL     = "http://www.sciencedirect.com/science";
	private static final String BIBTEX_STRING_ON_SCIENCE = "DownloadURL";
	private static final String[] PARAMS1                = new String[] {"_acct","_userid","_docType","_uoikey","_ArticleListID","count", "_rdoc","md5","RETURN_URL"};
	private static final String[] PARAMS2                = new String[] {"_ob","_method","_acct","_userid","_docType","_ArticleListID","_uoikey","count", "_rdoc","md5","format","citation-type","x","y","RETURN_URL"};
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(SCIENCE_CITATION_URL)) {
			sc.setScraper(this);
			
			// This Scraper might handle the specified url
			try {

				/*
				 * Parse the page and obtain a DOM
				 */ 
				Tidy tidy = new Tidy();
				tidy.setQuiet(true);
				tidy.setShowWarnings(false);// turns off warning lines
				Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);

				/*
				 * retrieve export citation page 
				 */

				// Look for an export citation link
				//now we have: /science?_ob=DownloadURL&_method=confirm&_ArticleListID=221049524&_rdoc=1&_docType=FLA&_acct=C000010021&_version=1&_userid=121749&md5=5cc6c9b143ed2d9b07b54f9f463f027c
				//retrieve the page
				
				String NextUrl=extractSinglePath(doc, BIBTEX_STRING_ON_SCIENCE);
				String exportpage=null;
				if (NextUrl==null) {
					//Check wether extractSinglePath(doc) could found something
					//e.g. http://www.sciencedirect.com/science?_ob=ArticleListURL&_method=list&_ArticleListID=503774015&_sort=d&_acct=C000010578&_version=1&_urlVersion=0&_userid=128556&md5=e52fe42586f42ee54bcc423669b5b795&view=f
					// search for Abstract and then for Export Option etc.
					exportpage = sc.getContentAsString(new URL(extractSinglePath(doc, "Abstract")));
					
					doc = tidy.parseDOM(new ByteArrayInputStream(exportpage.getBytes()), null);
					NextUrl=extractSinglePath(doc, BIBTEX_STRING_ON_SCIENCE);
				}
				exportpage = sc.getContentAsString(new URL(SCIENCE_HOST_NAME + NextUrl));

				//retrieve all elements for the post request from the page
				SortedMap<String,String> paramap = new TreeMap<String,String>();
				for (String key: PARAMS1) {
					Pattern p = Pattern.compile("<input type=hidden name="+key+" value=([^>]*)>");
					Matcher m = p.matcher(exportpage);
					if (m.find()) {
						paramap.put(key,m.group(1));
					}
				}
				//all parameters are extracted from the website
				//add fixed parameter order is important

				paramap.put("_ob", "DownloadURL");
				paramap.put("_method", "finish");
				paramap.put("format", "cite-abs");
				paramap.put("citation-type", "RIS");
				paramap.put("x", "11");
				paramap.put("y", "14");

				//make query


				String urlRisExport = "http://www.sciencedirect.com/science?";
				for (String key: PARAMS2){
					String value = paramap.get(key);
					if (value != null && !"".equals(value)){
						urlRisExport += key + "=" + value;
						if (!key.equals(PARAMS2[PARAMS2.length - 1])){
							urlRisExport += "&";
						}
					}
				}
				


				/*
				 * request the export page
				 * 
				 * http://www.sciencedirect.com/science?_ob=DownloadURL&_method=finish&_acct=C000065416&_userid=4861060&_docType=FLA&encodedHandle=V-WA-A-W-A-MsSAYZA-UUA-U-AAZCZDWCBW-AAZBWCBBBW-CZEEWCUY-A-U&_rdoc=1&md5=0d3a2dbccc0eac646828858634df2c1d&format=cite-abs&citation-type=RIS&x=11&y=14&RETURN_URL=http://www.sciencedirect.com/science/home
				 * 
				 */
				String RisResult = sc.getContentAsString(new URL(urlRisExport));

				/*
				 * make RIS to Bibtex
				 */

				String bibtexEntries = new RisToBibtexConverter().RisToBibtex(RisResult);

				/*
				 * Job done
				 */
				if (bibtexEntries != null && !"".equals(bibtexEntries)) {
					sc.setBibtexResult(bibtexEntries);
					return true;
				} else
					throw new ScrapingFailureException("getting bibtex failed");

			} catch (MalformedURLException me) {
				throw new InternalFailureException(me);
			}
		}
		// This Scraper can`t handle the specified url
		return false;
	}


	/**
	 * This method extracts the path to the export citation page. 
	 * 
	 * @param doc The document to extract the path to the export citation page
	 * @param snippet
	 * @return the extracted path
	 */
	private String extractSinglePath(Document doc, String SearchString) {
		NodeList as = doc.getElementsByTagName("a");
		for (int i = 0; i < as.getLength(); i++) {
			Element currNode = (Element) as.item(i);
			
			String attr = currNode.getAttribute("href");
			if (attr.contains(SearchString)) {
				return attr;
			}
		}
		// nothing found
		return null;
	}

	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}

}
