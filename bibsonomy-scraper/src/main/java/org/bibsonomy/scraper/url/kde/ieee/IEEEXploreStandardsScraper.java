package org.bibsonomy.scraper.url.kde.ieee;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class IEEEXploreStandardsScraper implements Scraper {
	private static final Logger log = Logger.getLogger(IEEEXploreStandardsScraper.class);
	private static final String info = "IEEEXplore Standards Scraper: This scraper creates a BibTeX entry for the standards at " +
			                           "<a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a>. Author: KDE";

	private static final String IEEE_HOST_NAME        	  = "http://ieeexplore.ieee.org/";
	private static final String IEEE_STANDARDS_PATH   	  = "xpl";
	private static final String IEEE_STANDARDS		 	  = "@misc";
	private static final String IEEE_STANDARDS_IDENTIFIER = "punumber";
	
	private static final String CONST_EISBN               = "E-ISBN: ";
	private static final String CONST_PAGE                = "Page(s): ";
	private static final String CONST_DATE                = "Publication Date: ";

	private static final String PATTERN_ARNUMBER = "arnumber=([^&]*)";
	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(IEEE_HOST_NAME+IEEE_STANDARDS_PATH) && sc.getUrl().toString().indexOf(IEEE_STANDARDS_IDENTIFIER) != -1 ) {
			sc.setScraper(this);
			
			Pattern pattern = Pattern.compile(PATTERN_ARNUMBER);
			Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if(matcher.find()){
				String downUrl = "http://ieeexplore.ieee.org/xpls/citationAct?dlSelect=cite_abs&fileFormate=BibTex&arnumber=<arnumber>" + matcher.group(1) + "</arnumber>";
				String bibtex = null;
				try {
					bibtex = sc.getContentAsString(new URL(downUrl));
				} catch (MalformedURLException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibtex != null){
					// clean up
					bibtex = bibtex.replace("<br>", "");
					
					sc.setBibtexResult(bibtex);
					return true;
					
				}else{
					log.debug("IEEEXploreStandardsScraper: direct bibtex download failed. Use JTidy to get bibliographic data.");
					sc.setBibtexResult(ieeeStandardsScrape(sc));
					return true;
					
				}
			}else{
				log.debug("IEEEXploreStandardsScraper use JTidy to get Bibtex from " + sc.getUrl().toString());
				sc.setBibtexResult(ieeeStandardsScrape(sc));
				return true;
			}
		}
		return false;
	}

	public String getInfo() {
		return info;
	}

	public Collection<Scraper> getScraper () {
		return Collections.singletonList((Scraper)this);
	}

	public String ieeeStandardsScrape (ScrapingContext sc) throws ScrapingException {
		try{
			//-- init all NodeLists and Node
			NodeList pres 		= null; 
			Node currNode 		= null;
			NodeList temp 		= null;
	
			//-- init String map for bibtex entries
			String type 		= IEEE_STANDARDS;
			String url 			= sc.getUrl().toString();
			String numpages 	= "";
			String title 		= "";
			String isbn 		= "";
			String abstr	 	= "";
			String year 		= "";
	
			//-- get the html doc and parse the DOM
			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false); // turn off warning lines
			Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);
	
			/* -- get the spans to extract the title and abstract
			 */
			pres = null;
			pres = doc.getElementsByTagName("span"); //get all <span>-Tags
			for (int i=0; i<pres.getLength(); i++){
				currNode = pres.item(i);
				if (currNode.hasAttributes()) {
					Element g = (Element)currNode;
					Attr own = g.getAttributeNode("class");
					//-- Extract the title
					if ("headNavBlueXLarge2".equals(own.getValue())){
						temp = currNode.getChildNodes();
						title = temp.item(temp.getLength()-1).getNodeValue().trim();
					}
					//-- Extract the abstract
					if ("sectionHeaders".equals(own.getValue()) && "Abstract".equals(currNode.getFirstChild().getNodeValue())){
						abstr = currNode.getParentNode().getLastChild().getNodeValue().trim();
					}
				}
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
					if ("bodyCopyBlackLargeSpaced".equals(own.getValue())){
						temp = currNode.getChildNodes();
	
						for(int j =0; j<temp.getLength(); j++){
							if (temp.item(j).getNodeValue().indexOf(CONST_DATE) != -1){
								String date = temp.item(j).getNodeValue().substring(CONST_DATE.length()).trim();
								year = date.substring(date.length()-4).trim();
							}
							if (temp.item(j).getNodeValue().indexOf(CONST_PAGE) != -1){
								numpages = temp.item(j).getNodeValue().substring(CONST_PAGE.length()).trim();
							}
							if (temp.item(j).getNodeValue().indexOf(CONST_EISBN) !=  -1){
								isbn = temp.item(j).getNodeValue().substring(CONST_EISBN.length()).trim();
							}
						}
					}
				}
			}
	
			//create valid bibtex snippet
			return type + " {," 
						+ "title = {" + title + "}, " 
						+ "year = {" + year + "}, " 
						+ "url = {" + url + "}, "
						+ "pages = {" + numpages + "}, " 
						+ "abstract = {" + abstr + "}, "
						+ "isbn = {" + isbn + "}}";
		
		}catch(Exception e){
			throw new InternalFailureException(e);
		}
	}
}