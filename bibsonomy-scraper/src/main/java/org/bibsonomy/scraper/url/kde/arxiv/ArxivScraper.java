package org.bibsonomy.scraper.url.kde.arxiv;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


public class ArxivScraper implements Scraper {
	
	private static final String info = "arXiv Scraper: This scraper parses a publication page from <a href=\"http://arxiv.org/\">arXiv</a> and " +
	   								   "extracts the adequate BibTeX entry. Author: KDE";
	
	private static final String ARXIV_HOST = "arxiv.org";
	private static final String CITEBASE_HOST_NAME = "http://www.citebase.org";
	private static final String SLACSPIRES_HOST_NAME = "http://www.slac.stanford.edu";
	private static final String CITEBASE_STRING_ON_ARXIV = "CiteBase";
	private static final String SLACSPIRE_STRING_ON_ARXIV = "SLAC-SPIRES HEP";
	
	private static final String BIBTEX_STRING_ON_ARXIV = "BibTeX";
	private static final String BIBTEX_STRING_ON_SLACSPIRES = "BibTeX";
	private static final String BIBTEX_ABSTRACT_TAG = "blockquote";
	
	private static final Logger log = Logger.getLogger(ArxivScraper.class);
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(ARXIV_HOST)) {
			try {
				String bibtexResult = null; 
				
				Document doc = getDOM(sc.getPageContent());
				// get citebase url on arxiv publication page
				String testCitebaseUrl = extractUrlFromElementByTagNameAndValue(doc, "a", CITEBASE_STRING_ON_ARXIV, "href");
				String testSlacSpiresUrl = extractUrlFromElementByTagNameAndValue(doc, "a", SLACSPIRE_STRING_ON_ARXIV, "href");
				
				
				//decide which extraction method will be used
				if(testCitebaseUrl != null) {
					URL citebaseUrl    = new URL(testCitebaseUrl);
					String bibAbstract = extractAbstract(doc, BIBTEX_ABSTRACT_TAG); 
					
					// get bibtex url on citebase publication page
					URL bibtexUrl = new URL(CITEBASE_HOST_NAME + extractUrlFromElementByTagNameAndValue(getDOM(sc.getContentAsString(citebaseUrl)), "a", BIBTEX_STRING_ON_ARXIV, "href"));
					
					// get bibtex page and add abstract
					bibtexResult = sc.getContentAsString(bibtexUrl);
					if (bibAbstract != null) {						
						bibtexResult = addAbstractToBibtexEntry(bibtexResult, bibAbstract);
					}
				} else 	if(testSlacSpiresUrl != null) {
					String bibAbstract = extractAbstract(doc, BIBTEX_ABSTRACT_TAG); 
					URL slacSpiresUrl    = new URL(testSlacSpiresUrl);
					
					//get the bibtex url from spires
					URL bibtexUrl = new URL(SLACSPIRES_HOST_NAME + extractUrlFromElementByTagNameAndValue(getDOM(sc.getContentAsString(slacSpiresUrl)), "a", BIBTEX_STRING_ON_SLACSPIRES, "href"));
							
					//extract the pre-tagged bibtex entry from slacspires
					Document temp = getDOM(sc.getContentAsString(bibtexUrl));
					NodeList as = temp.getElementsByTagName("pre");
					for (int i = 0; i < as.getLength(); i++) {
						Node currNode = as.item(i);
						if (currNode.hasChildNodes()){
							bibtexResult = currNode.getChildNodes().item(0).getNodeValue();	
						}
					}	
					
					//add the abstract to the bibtex entry
					if (bibAbstract != null) {						
						bibtexResult = addAbstractToBibtexEntry(bibtexResult, bibAbstract);
					}
				} else {
					//if no citation manager is available scrape from html code
					bibtexResult = scrapeBySourceCode(doc);
				}
				
				// add url to bibentry
				bibtexResult = addUrlToBibtexEntry(sc.getUrl().toString(), bibtexResult);
				
				// set result
				sc.setBibtexResult(bibtexResult);
				/*
				 * returns itself to know, which scraper scraped this
				 */
				sc.setScraper(this);

				return true;
				
			} catch (MalformedURLException me) {
				throw new InternalFailureException(me);
			}
		}		
		return false;
	}

	/** Parses a page and returns the DOM
	 * @param content
	 * @return
	 */
	private Document getDOM(String content) {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);// turns off warning lines
		Document doc = tidy.parseDOM(new ByteArrayInputStream(content.getBytes()), null);
		return doc;
	}

	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
	}
	
	/**
	 * Extracts URLs from specific and page-unique elements. Unique means, that the node value (here: CiteBase)
	 * of the requested element "a" appears only once as node value.
	 * We handle some like this: <a href="http://blabla.com">CiteBase</a>
	 * @param pageContent Page content as InputStream
	 * @param tagName E.g. a 
	 * @param tagValue  E.g. CiteBase 
	 * @param attribute E.g. href
	 * @return The extracted URL as a String - e.g. http://blabla.com or null
	 * @throws DOMException 
	 * @throws MalformedURLException 
	 */
	private String extractUrlFromElementByTagNameAndValue(Document doc, String tagName, String tagValue, String attribute) throws MalformedURLException, DOMException{
		NodeList as = doc.getElementsByTagName(tagName);
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);

			if (currNode.getChildNodes().getLength() > 0) {
				if (tagValue.equals(currNode.getChildNodes().item(0).getNodeValue())){
					return currNode.getAttributes().getNamedItem(attribute).getNodeValue();						
				}
			}
		
		}		
		return null;
	}
	
	private String extractAbstract(Document doc, String tagName){		
		NodeList as = doc.getElementsByTagName(tagName);
		for (int i = 0; i < as.getLength(); i++) {
			Node currNode = as.item(i);
			
			if (currNode.getChildNodes().getLength() > 0) {	
				log.debug("abstract = " + currNode.getChildNodes().item(0).getNodeValue());
				return currNode.getChildNodes().item(1).getNodeValue();	
			}
		}		
		return null;
	}
	
	/**
	 *  Add abstract to bibtex entry by replacing the last occurrence of "}"
	 *  with ",abstract = {...}}"
	 */
	private String addAbstractToBibtexEntry(String bibtexEntry, String bibAbstract){
			StringBuffer buf = new StringBuffer (bibtexEntry);
			buf.replace(buf.lastIndexOf("}"), buf.length(), ", abstract={" + bibAbstract + "}}");
			return  buf.toString();		
	}
	
	/**
	 * Adds scraped URL to extracted bibtex entry.
	 * @param url 
	 * @param entry citation in Bibtex
	 * @return bibtex entry with scraped url
	 */
	private String addUrlToBibtexEntry(String url, String entry){
		String newUrl = "url = {\\\\url{" + url + "}}";
		// replace old url with scraped url
		if(entry.contains("url = {"))
			return entry.replaceAll("url = \\{[^\\}]*\\}", newUrl);
		
		// no old url available
		return entry.substring(0, entry.lastIndexOf("}")) + newUrl + "\n}";
	}
	
	private String scrapeBySourceCode (Document doc){
		//initalize all neede vars
		NodeList pres = null;
		StringBuffer bibtex = new StringBuffer();
		
		String year = "";
		String month = "";
		String title = "";
		String pages = "";
		String authors = "";
		String abstr = "";
		
		//add the bibtexkey
		bibtex.append("@article{");
		
		//get all h1 tag to extract the title
		pres = doc.getElementsByTagName("h1"); //get all <h1>-Tags
		for (int i = 0; i < pres.getLength(); i++) {
			Node curr = pres.item(i);
			if (curr.hasAttributes()){
				Element g = (Element)curr;
				Attr own = g.getAttributeNode("class");
				
				if (own == null){
					continue;
				}
			
				if ("title".equals(own.getValue())) {
					title = curr.getChildNodes().item(1).getNodeValue().trim();
				}
			}
		}
		
		//get all div tag to extract the authors, the date and the pages
		pres = doc.getElementsByTagName("div"); //get all <div>-Tags
		for (int i = 0; i < pres.getLength(); i++) {
			Node curr = pres.item(i);
			
			if (curr.hasAttributes()){
				Element g = (Element)curr;
				Attr own = g.getAttributeNode("class");

				if (own == null){
					continue;
				}
				
				//get all authors
				if ("authors".equals(own.getValue())) {
					for (int j = 1; j < curr.getChildNodes().getLength(); j++){
						if (curr.getChildNodes().item(j).hasChildNodes()){
							authors += curr.getChildNodes().item(j).getChildNodes().item(0).getNodeValue() + " and ";
						}
					}
				}
				
				//get the date
				if ("dateline".equals(own.getValue())) {
					String datePattern = "(\\d{1,2})? (\\w{3})? (\\d{4})?";
					String dateline = curr.getChildNodes().item(0).getNodeValue();
					
					
					Pattern dateP = Pattern.compile(datePattern);
					Matcher matcher = dateP.matcher(dateline);
					
					if (matcher.find()){
						month = matcher.group(2);
						year = matcher.group(3);
					}
				}
				
				//get the pages
				if ("tablecell subjects".equals(own.getValue())){
					String pagePattern = "\\d*";
					Pattern pageP = Pattern.compile(pagePattern);
					Matcher matcher = pageP.matcher(curr.getChildNodes().item(0).getNodeValue());
					
					if (matcher.find()){
						pages = matcher.group(0);
					}
				}
			}
		}
		
		//extract the abstract with the predefinied function
		abstr = extractAbstract(doc, "blockquote");
				
		//creat the bibtexkey
		String keyPattern = "\\w*";
		Pattern keyP = Pattern.compile(keyPattern);
		Matcher matcher = keyP.matcher(authors);
		
		if (matcher.find()){
			bibtex.append(matcher.group(0) + ":" + year + ",");
		}
		
		
		//form the bibtex string
		bibtex.append("title={" + title + " },");
		bibtex.append("author={" + authors + " },");
		bibtex.append("pages={" + pages + " },");
		bibtex.append("year={" + year + " },");
		bibtex.append("month={" + month + "},");
		bibtex.append("abstract={" + abstr + " },");
		bibtex.append("}");

		return bibtex.toString();
	}

}
