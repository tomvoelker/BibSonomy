
package org.bibsonomy.scraper.url.kde.ieee;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


public class IEEEXploreBookScraper implements Scraper {
	private static final Logger log = Logger.getLogger(IEEEXploreBookScraper.class);
	private static final String info = "IEEEXplore Book Scraper: This scraper creates a BibTeX entry for the books at " +
			                           "<a href=\"http://ieeexplore.ieee.org/books/bkbrowse.jsp\">IEEEXplore</a>. Author: KDE";
	
	private static final String IEEE_HOST_NAME = "http://ieeexplore.ieee.org/";
	private static final String IEEE_BOOK_PATH = "books";
	private static final String IEEE_BOOK	   = "@book";

	private static final String CONST_ISBN     = "ISBN: ";
	private static final String CONST_PAGES    = "Page(s): ";
	private static final String CONST_EDITION  = "Edition: ";
	private static final String CONST_DATE	   = "Publication Date: ";



	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(IEEE_HOST_NAME+IEEE_BOOK_PATH)) {
			try {
				sc.setBibtexResult(ieeeBookScrape(sc));
				sc.setScraper(this);
				return true;
			} catch (Exception e){
				log.fatal("could not scrape ieeexplore book " + sc.getUrl().toString());
				throw new ScrapingException(e);
			}
		}
		return false;
	}
	
	public String ieeeBookScrape (ScrapingContext sc) throws ScrapingException {
		//-- init all NodeLists and Node
		NodeList pres 		= null; 
		Node currNode 		= null;
		NodeList temp 		= null;
		
		//-- init String map for bibtex entries
		String type 		= IEEE_BOOK;
		String url 			= sc.getUrl().toString();
		String authors 		= "";
		String numpages 	= "";
		String title 		= "";
		String isbn 		= "";
		String publisher 	= "";
		String month 		= "";
		String year 		= "";
		String edition 		= "";
		String abstr 		= "";
		
		String bibtexkey	= null;
		String _tempabs		= null;
		String _format		= null;
		
		//-- get the html doc and parse the DOM
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setMakeClean(true);
		tidy.setDropFontTags(true);
		tidy.setShowWarnings(false); // turn off warning lines
		Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);
		

		/*-- Search title and extract --
		 * The title has always the css-class "headNavBlueXLarge".
		 * */
		pres = null;
		pres = doc.getElementsByTagName("span"); //get all <span>-Tags
		for (int i = 0; i < pres.getLength(); i++) {
			Node curr = pres.item(i);
			Element g = (Element)curr;
			Attr own = g.getAttributeNode("class");			
			
			//-- Extract the title
			if ("headNavBlueXLarge".equals(own.getValue())){
				title = curr.getFirstChild().getNodeValue();
			}
		}
		
		//get the abstract block
		String ident1 = "<strong>Abstract</strong>";
		String ident2 = "<strong>Table of Contents </strong>";
		if (sc.getPageContent().indexOf(ident1) != -1 && sc.getPageContent().indexOf(ident2) != -1 ){
			_tempabs = sc.getPageContent().substring(sc.getPageContent().indexOf(ident1)+ident1.length(),sc.getPageContent().indexOf(ident2)).replaceAll("\\s\\s+", "").replaceAll("(<.+?>)", "").trim();
			abstr = _tempabs;			
		}
		
		//get the book formats like hardcover
		ident1 = "<td class=\"bodyCopyBlackLarge\" nowrap>Hardcover</td>";
		ident2 = "<td class=\"bodyCopyBlackLarge\" nowrap><span class=\"sectionHeaders\">&raquo;</span>";
		if (sc.getPageContent().indexOf(ident1) != -1){
			_format = sc.getPageContent().substring(sc.getPageContent().indexOf(ident1),sc.getPageContent().indexOf(ident2)).replaceAll("\\s\\s+", "").replaceAll("(<.+?>)", "");

			_format = _format.substring(_format.indexOf(CONST_ISBN) + CONST_ISBN.length());
			isbn = _format.substring(0,_format.indexOf("&nbsp;"));
		}
		
		
		/*-- get all <p>-Tags to extract the standard informations
		 *  In every standard page the css-class "bodyCopyBlackLargeSpaced"
		 *  indicates the collection of all informations.
		 * */
		pres = null;
		pres = doc.getElementsByTagName("p"); //get all <p>-Tags
		for (int i=0; i<pres.getLength(); i++){
			currNode = pres.item(i);
			
			if (currNode.hasAttributes()) {
				Element g = (Element)currNode;
				Attr own = g.getAttributeNode("class");
				if ("bodyCopyBlackLargeSpaced".equals(own.getValue()) && currNode.hasChildNodes()){
					temp = currNode.getChildNodes();
					
					for(int j =0; j<temp.getLength(); j++){
						if (temp.item(j).getNodeValue().indexOf(CONST_DATE) != -1){
							String date = temp.item(j).getNodeValue().substring(18);
							year = date.substring(date.length()-5).trim();
							month = date.substring(0,date.length()-4).trim();
							publisher = temp.item(j+2).getNodeValue().trim();
						}
						if (temp.item(j).getNodeValue().indexOf(CONST_PAGES) != -1){
							numpages = temp.item(j).getNodeValue().substring(CONST_PAGES.length()).trim();
						}
						if (temp.item(j).getNodeValue().indexOf(CONST_EDITION) != -1){
							edition = temp.item(j).getNodeValue().substring(CONST_EDITION.length()).trim();
						}
					}
					break;
				}
			}
		}
		
		/*-- Search authors and save them --
		 * */
		pres = null;
		pres = doc.getElementsByTagName("a"); //get all <a>-Tags
		
		//init vars to count authors to form a bibtex String
		int numaut = 0;
		
		/*
		 * iterate trhough the a tags and search the attribute value "<in>aud)" 
		 * to identify the authors in the source of the ieeexplore page
		 * */
		for (int i = 39; i < pres.getLength(); i++) {
			Node curr = pres.item(i);
			Element g = (Element)curr;
			Attr own = g.getAttributeNode("href");
			
			if (own.getValue().indexOf("<in>au)") != -1){
				//Form Bibtex String by counting authors
				if (numaut > 0 ){
					authors += " and " + curr.getFirstChild().getNodeValue(); 
				}
				if (numaut == 0) {
					numaut=i;
					authors += curr.getFirstChild().getNodeValue();
					
					if (curr.getFirstChild().getNodeValue().indexOf(",") != -1 && bibtexkey == null){
						bibtexkey = curr.getFirstChild().getNodeValue().substring(0,curr.getFirstChild().getNodeValue().trim().indexOf(","));
					} else if (curr.getFirstChild().getNodeValue().trim().indexOf(" ") != -1 && bibtexkey == null){
						bibtexkey = curr.getFirstChild().getNodeValue().trim().substring(0,curr.getFirstChild().getNodeValue().trim().indexOf(" "));
					} else if (bibtexkey == null){
						bibtexkey = curr.getFirstChild().getNodeValue().trim();
					}
				}
			}
		}
		
		//-- kill special chars and add the year to bibtexkey
		bibtexkey = bibtexkey.replaceAll("[^0-9A-Za-z]", "") + ":" + year;
		
		//create the book-bibtex
		
		return type + " { " + bibtexkey + ", " 
	      + "author = {" + authors + "}, " 
	      + "title = {" + title + "}, " 
	      + "year = {" + year + "}, " 
	      + "url = {" + url + "}, "
	      + "pages = {" + numpages + "}, "
	      + "edition = {" + edition + "}, " 
	      + "publisher = {" + publisher + "}, "
	      + "isbn = {" + isbn + "}, " 
	      + "abstract = {" + abstr + "}, "
	      + "month = {" + month + "}}";
	}
	
	public String getInfo() {
		return info;
	}
	
	public Collection<Scraper> getScraper () {
		return Collections.singletonList((Scraper)this);
	}
	
}