package org.bibsonomy.scraper.url.kde.acm;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.UrlScraper;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.scraper.url.UrlMatchingHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class ACMBasicScraper implements Scraper, UrlScraper {
	private static final Logger log = Logger.getLogger(ACMBasicScraper.class);
	private static final String info = "ACM Scraper: This scraper parses a publication page from the  <a href=\"http://portal.acm.org/portal.cfm\">ACM Digital Library</a>  " +
									   "and extracts the adequate BibTeX entry. Author: KDE";

	private static final String ACM_HOST       		 = "portal.acm.org";
	private static final String ACM_HOST_NAME        = "http://portal.acm.org/";
	private static final String ACM_CITATION_URL     = ACM_HOST_NAME + "citation.cfm";
	private static final String BIBTEX_STRING_ON_ACM = "BibTex";

	private List<String> pathsToScrape; // holds the paths to the popup pages
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc != null && sc.getUrl() != null && supportsUrl(sc.getUrl())) {
			sc.setScraper(this);

			//create the URL string manually to add it to the bibtex string
			String url = "url = {" + sc.getUrl().toString() + "}}";
			String abstr = ",abstract = {";
			
			// This Scraper might handle the specified url
			try {
				
				StringBuffer bibtexEntries = new StringBuffer("");
				pathsToScrape  = new ArrayList<String>();
				
				// Parse the page and obtain a DOM
				Tidy tidy = new Tidy();
				tidy.setQuiet(true);
				tidy.setShowWarnings(false); // turn off warning lines
				Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);
				// save path to popup page of current bibtex entry
				extractSinglePath(doc);
				
				/*
				 * test if "ABSTRACT" is available in an anchor tag, this indicates that an abtract if at hand
				 */
				boolean testAbstract = false;
				NodeList as1 = doc.getElementsByTagName("a"); //get all <div>-Tags
				for (int i = 0; i < as1.getLength(); i++) {
					Node curr = as1.item(i);
					
					if (curr.hasAttributes()){
						Element g = (Element)curr;
						Attr own = g.getAttributeNode("name");

						if (own == null){
							continue;
						}
						
						if ("abstract".equals(own.getValue())) {
							if ("ABSTRACT".equals(curr.getChildNodes().item(0).getNodeValue())){
								testAbstract = true;
							break;
							}
							
						}
					}
				}
				
				//if this "ABSTRACT" is availabe search the parentnode an extract all the text
				if (testAbstract){
					NodeList as = doc.getElementsByTagName("p"); //get all <div>-Tags
					for (int i = 0; i < as.getLength(); i++) {
						Node curr = as.item(i);
						if (curr.hasAttributes()){
							Element g = (Element)curr;
							Attr own = g.getAttributeNode("class");
	
							if (own == null){
								continue;
							}
							
							if ("abstract".equals(own.getValue())) {
								//the abstract extracting method ist not reliable somtime it cant extract text 
								//out of a <p>-tag. Possibly its the tidy parser who causes this.
								abstr += getText(curr) + "}}";
								break;
							}
						}
					}
				}
				
				
				/*
				 * Scrape entries from popup BibTeX site. BibTeX entry on these
				 * pages looks like this: <PRE id="155273">@article{155273,
				 * author = {The Author}, title = {This is the title}...}</pre>
				 */
				for (String path: pathsToScrape) {
					doc = tidy.parseDOM(new ByteArrayInputStream(sc.getContentAsString(new URL(ACM_HOST_NAME + path)).getBytes()), null);
					
					NodeList pres = doc.getElementsByTagName("pre");
					for (int i = 0; i < pres.getLength(); i++) {
						Node currNode = pres.item(i);
						NodeList childnodes = currNode.getChildNodes();
						if (childnodes.getLength() > 0) {
							bibtexEntries.append(" " + currNode.getChildNodes().item(0).getNodeValue());
						}
					}
				}
				
				String result;
				
				if (bibtexEntries.toString().indexOf("url") == -1){
					result = bibtexEntries.toString().replaceFirst(".$",url);	
				} else {
					result = bibtexEntries.toString();
				}
				
				if (testAbstract){
					result = result.replaceFirst("}$",abstr);
				}
				

				
				if (!"".equals(result)) {
					sc.setBibtexResult(result);
					return true;
				} else
					throw new ScrapingFailureException("getting bibtex failed");
			} catch (Exception me) {
				throw new InternalFailureException(me);			}
		}
		// This Scraper can`t handle the specified url
		return false;
	}

	/**
	 * Extract the path from the onclick attribute of the <a>-Element including
	 * the value "BibTex". Watch out for changes of this element on ACM sites
	 * otherwise our extraction does not work anymore. On 04.09.2006 the element
	 * looked like this: <a
	 * href="citation.cfm?id=62605&dl=ACM&coll=ACM&CFID=72100071&CFTOKEN=98542378#"
	 * onClick="window.open('popBibTex.cfm?id=62605&ids=SERIES326.62597.62605&types=series.proceeding.article&reqtype=article&coll=ACM&dl=ACM&CFID=72100071&CFTOKEN=98542378',
	 * 'BibTex','width=800,height=100,top=100,left=100,scrollbars=Yes,resizable=yes');"
	 * class="small-link-text">BibTex</a>
	 * 
	 * @param nodeValue
	 *            the node value to extract the url from
	 * @return the extracted path
	 */
	private String extractPathFromOnclickNode(String nodeValue) {
		int firstPrimePos = nodeValue.indexOf("'") + 1;
		return nodeValue.substring(firstPrimePos, nodeValue.indexOf("'", firstPrimePos));
	}

	/**
	 * This method extracts the popup path of current page. We use this method
	 * whenever the snippet is empty.
	 * 
	 * @param doc The document to extract the popup path from.
	 * @param snippet
	 * @return the extracted path
	 */
	private void extractSinglePath(Document doc) {
		NodeList as = doc.getElementsByTagName("a");
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);
			NodeList childnodes = currNode.getChildNodes();

			if (childnodes.getLength() > 0) {
				if (BIBTEX_STRING_ON_ACM.equals(currNode.getChildNodes().item(0).getNodeValue())) {
					pathsToScrape.add(extractPathFromOnclickNode(currNode.getAttributes().getNamedItem("onclick").getNodeValue()));
				}
			}
		}
	}

	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper () {
		return Collections.singletonList((Scraper)this);
	}
	
	//function to extract the text in one parent node and all depth of it
	public String getText(Node node) {
    StringBuffer text = new StringBuffer();
    
    String value = node.getNodeValue();
    
    if (value != null){
    	text.append(value);
    }
    
    if (node.hasChildNodes()) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        text.append(getText(child));
      }
    }
    
    return text.toString();
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		List<Tuple<Pattern,Pattern>> list = new LinkedList<Tuple<Pattern,Pattern>>();
		list.add(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + ACM_HOST), UrlScraper.EMPTY_PATTERN));
		return list;
	}

	public boolean supportsUrl(URL url) {
		return UrlMatchingHelper.isUrlMatch(url, this);
	}
	
}
