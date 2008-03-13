package org.bibsonomy.scraper.url.kde.spires;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.ScrapingException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class SpiresScraper implements Scraper{
	private static final Logger log = Logger.getLogger(SpiresScraper.class);
	private static final String info = "arXiv Scraper: This scraper parses a publication page from <a href=\"http://www.slac.stanford.edu/spires/hep/\">SPIRES-HEP</a> and " +
	   									"extracts the adequate BibTeX entry. Author: KDE";

	private static final String SPIRES_HOST = "slac.stanford.edu";
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().getHost().endsWith(SPIRES_HOST)) {
			try {
				String bibtexresult = null;
				
				//form the dom to extract the bibtex-url
				Document doc = getDOM(sc.getPageContent());
				
				//create the URL to the bibtex snippet
				URL bibtex = new URL("http://" + SPIRES_HOST + extractUrlFromElementByTagNameAndValue(doc, "a","BibTeX", "href"));
				
				//create the dom of the bibtex page
				Document temp = getDOM(sc.getContentAsString(bibtex));
				
				//extract the bibtex snippet which is embedded in pre tags
				NodeList nl = temp.getElementsByTagName("pre"); //get the pre tags (normally one)
				for (int i = 0; i < nl.getLength(); i++) {
					Node currNode = nl.item(i);
					if (currNode.hasChildNodes()){
						bibtexresult = currNode.getChildNodes().item(0).getNodeValue();	
					}
				}	
				
				//-- bibtex string may not be empty
				if (bibtexresult != null && !"".equals(bibtexresult)) {
					sc.setBibtexResult(bibtexresult);
					/*
					 * returns itself to know, which scraper scraped this
					 */
					sc.setScraper(this);
		
					return true;
				}
				
			} catch (Exception e) {
				log.fatal("could not scrape spires publication " + sc.getUrl().toString());
				log.fatal(e);
				throw new ScrapingException(e);
			}
		}
		return false;
	}

	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper() {
		return Collections.singletonList((Scraper) this);
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
}