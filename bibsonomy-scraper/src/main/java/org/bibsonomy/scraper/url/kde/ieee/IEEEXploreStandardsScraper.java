package org.bibsonomy.scraper.url.kde.ieee;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Scraper for IEEE Explore
 * @author rja
 *
 */
public class IEEEXploreStandardsScraper extends AbstractUrlScraper {
	private static final Logger log = Logger.getLogger(IEEEXploreStandardsScraper.class);
	private static final String info = "IEEEXplore Standards Scraper: This scraper creates a BibTeX entry for the standards at " +
			                           href("http://ieeexplore.ieee.org/", "IEEEXplore");

	private static final String IEEE_HOST        	  = "ieeexplore.ieee.org";
	private static final String IEEE_HOST_NAME        	  = "http://ieeexplore.ieee.org/";
	private static final String IEEE_STANDARDS_PATH   	  = "xpl";
	private static final String IEEE_STANDARDS		 	  = "@misc";
	private static final String IEEE_STANDARDS_IDENTIFIER = "punumber";
	
	private static final String CONST_EISBN               = "E-ISBN: ";
	private static final String CONST_PAGE                = "Page(s): ";
	private static final String CONST_DATE                = "Publication Date: ";

	private static final Pattern pattern = Pattern.compile("arnumber=([^&]*)");
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + IEEE_HOST), Pattern.compile("/" + IEEE_STANDARDS_PATH + ".*")));

	
	protected boolean scrapeInternal (ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl().toString().indexOf(IEEE_STANDARDS_IDENTIFIER) != -1 ) {
			sc.setScraper(this);
			
			Matcher matcher = pattern.matcher(sc.getUrl().toString());
			if(matcher.find()){
				String downUrl = "http://ieeexplore.ieee.org/xpls/citationAct?dlSelect=cite_abs&fileFormate=BibTex&arnumber=<arnumber>" + matcher.group(1) + "</arnumber>";
				String bibtex = null;
				try {
					bibtex = WebUtils.getContentAsString(new URL(downUrl));
				} catch (IOException ex) {
					throw new InternalFailureException(ex);
				}
				
				if(bibtex != null){
					// clean up
					bibtex = bibtex.replace("<br>", "");
					
					// append url
					bibtex = BibTexUtils.addFieldIfNotContained(bibtex, "url", sc.getUrl().toString());
					
					// add downloaded bibtex to result 
					sc.setBibtexResult(bibtex.toString().trim());
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
			final Document document = XmlUtils.getDOM(sc.getPageContent());
	
			/* -- get the spans to extract the title and abstract
			 */
			pres = null;
			pres = document.getElementsByTagName("span"); //get all <span>-Tags
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
			pres = document.getElementsByTagName("p"); //get all <p>-Tags
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
	
	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {
		return patterns;
	}
	
}