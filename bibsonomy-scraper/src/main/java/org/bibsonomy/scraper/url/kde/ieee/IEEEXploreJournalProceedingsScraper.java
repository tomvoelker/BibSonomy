package org.bibsonomy.scraper.url.kde.ieee;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
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
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

public class IEEEXploreJournalProceedingsScraper implements Scraper {
	private static final Logger log = Logger.getLogger(IEEEXploreJournalProceedingsScraper.class);
	private static final String info = "IEEEXplore Journal Scraper: This scraper creates a BibTeX entry for the journals and proceedings " +
			                           "at <a href=\"http://ieeexplore.ieee.org/\">IEEEXplore</a>. Author: KDE";

	private static final String IEEE_HOST_NAME     = "http://ieeexplore.ieee.org/";
	private static final String IEEE_PATH 	  	   = "xpl";
	private static final String IEEE_JOURNAL	   = "@article";
	private static final String IEEE_PROCEEDINGS   = "@proceedings";
	private static final String IEEE_INPROCEEDINGS = "@inproceedings";

	private static final String CONST_DATE       = "Publication Date: ";
	private static final String CONST_VOLUME     = "Volume: ";
	private static final String CONST_PAGES      = "On page(s): ";
	private static final String CONST_BOOKTITLE	 = "This paper appears in: ";
	
	private static final String PATTERN_ARNUMBER = "chklist=([^%]*)";

	
	public boolean scrape(ScrapingContext sc) throws ScrapingException {
		if (sc.getUrl() != null && sc.getUrl().toString().startsWith(IEEE_HOST_NAME+IEEE_PATH)  && sc.getUrl().toString().indexOf("punumber") == -1 ) {
			
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
					sc.setScraper(this);
					return true;
					
				}else{
					sc.setBibtexResult(ieeeJournalProceedingsScrape(sc));
					sc.setScraper(this);
					return true;
					
				}
			}else{
				sc.setBibtexResult(ieeeJournalProceedingsScrape(sc));
				sc.setScraper(this);
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

	public String ieeeJournalProceedingsScrape (ScrapingContext sc) throws ScrapingException {

		//-- init all NodeLists and Node
		NodeList pres 		= null; 
		Node currNode 		= null;
		NodeList temp 		= null;

		//-- init Strings for bibtex entries
		// month uncased because of multiple date types
		String type 		= "";
		String url 			= sc.getUrl().toString();
		String author 		= "";
		String year 		= "";
		String abstr		= "";
		String title		= "";
		String booktitle	= "";
		String volume = null;
		String pages  = null;
		String issn   = null;
		String isbn   = null;
		String doi    = null;

		String authors[] 	= null; 
		String tempAuthors 	= null;


		//-- get the html doc and parse the DOM
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setCharEncoding(Configuration.UTF8);
		tidy.setShowWarnings(false); // turn off warning lines
		Document doc = tidy.parseDOM(new ByteArrayInputStream(sc.getPageContent().getBytes()), null);

		//get the abstract block
		String ident1 = "<span class=\"sectionHeaders\">Abstract</span><br>";
		String ident2 = "<td class=\"bodyCopyGrey\"><p class=\"bodyCopyGreySpaced\"><strong>Index";
		if (sc.getPageContent().indexOf(ident1) != -1 && sc.getPageContent().indexOf(ident2) != -1 ){
			abstr = sc.getPageContent().substring(sc.getPageContent().indexOf(ident1)+ident1.length(),sc.getPageContent().indexOf(ident2)).replaceAll("\\s\\s+", "").replaceAll("(<.+?>)", "").trim();			
		}

		/*-- Get the title of the journal --
		 * Iterate through all spans
		 */
		pres = null;
		pres = doc.getElementsByTagName("span"); //get all <span>-Tags
		for (int i = 0; i < pres.getLength(); i++) {
			Node curr = pres.item(i);
			Element g = (Element)curr;
			Attr own = g.getAttributeNode("class");

			if ("headNavBlueXLarge2".equals(own.getValue())) {
				title = curr.getFirstChild().getNodeValue();
				temp = pres.item(i+1).getChildNodes();

				if (!"".equals(temp.item(0).getNodeValue())) {
					tempAuthors = temp.item(0).getNodeValue();

					if ("\u00A0\u00A0".equals(tempAuthors))	{
						authors = new String[] {"N/A"};
					} else {
						authors = tempAuthors.split("\u00A0\u00A0");
					}
				}
				break;
			}
		}

		/*-- Get the global infomation like publication date, number of pages ... --
		 * iterate through all p's stop at "This paper appears in:" because its
		 * available in all journals.
		 * Save Nodelist and break the loops.
		 * */
		pres = null;
		NodeList match = null;
		pres = doc.getElementsByTagName("p"); //get all <p>-Tags
		for (int i=0; i<pres.getLength(); i++){
			currNode = pres.item(i);
			temp = currNode.getChildNodes();
			//iterate through childs to find "Publication Date:"
			for (int j=0; j<temp.getLength(); j++){
				if (temp.item(j).getNodeValue().indexOf(CONST_BOOKTITLE) != -1){
					if (!"".equals(temp.item(1).getFirstChild().getFirstChild().getNodeValue())){
						booktitle = temp.item(1).getFirstChild().getFirstChild().getNodeValue();
					}
					match=temp;
					break;
				}
			}
		}
		//get the different childs of the founded p-tag
		for (int i=0; i<match.getLength(); i++){
			if (!"".equals(match.item(i).getNodeValue())){
				String infoMatches = null;
				if (match.item(i).getNodeValue().indexOf(CONST_DATE) != -1){
					//extract the year
					infoMatches = match.item(i).getNodeValue().substring(CONST_DATE.length());
					StringTokenizer tokenizer = new StringTokenizer(infoMatches);
					String yearPattern = "\\d{4}";
					Pattern yearP = Pattern.compile(yearPattern);

					while ( tokenizer.hasMoreTokens() ){
						String token = tokenizer.nextToken();
						Matcher matcherYear = yearP.matcher(token);
						if (matcherYear.matches()){
							year = token;
						}
					}
				}
				if (volume == null && match.item(i).getNodeValue().indexOf(CONST_VOLUME) != -1){
					infoMatches = match.item(i).getNodeValue();
					volume = infoMatches.substring(infoMatches.indexOf(CONST_VOLUME) + CONST_VOLUME.length(),infoMatches.indexOf(",")).trim();
				}
				if (pages == null && match.item(i).getNodeValue().indexOf(CONST_PAGES) != -1){
					infoMatches = match.item(i).getNodeValue();
					pages = infoMatches.substring(infoMatches.indexOf(CONST_PAGES) + CONST_PAGES.length()).trim();
				}
				if (issn == null) issn = getField(match, i, "ISSN: ");
				if (isbn == null) isbn = getField(match, i, "ISBN: "); 
				if (doi  == null) doi  = getField(match, i, "Digital Object Identifier: ");
			}
		}

		//-- set bibtex type @article for journals & @proceeding for proceedings
		if ((isbn == null || isbn.trim().equals("")) && issn != null && !issn.trim().equals("")) {
			type = IEEE_JOURNAL;
		} else {
			if (title.equals(booktitle)){
				type = IEEE_PROCEEDINGS;
			} else {
				type = IEEE_INPROCEEDINGS;
			}
		}


		//-- get all authors out of the arraylist and prepare them to bibtex entry "author"
		for (int i=0; i<authors.length; i++){
			if (i==authors.length-1){
				author += authors[i].trim();
			} else {
				author += authors[i].trim() + " and ";
			}
		}


		//-- kill spaces and add the year to bibtexkey
		//- replace all special chars to avaoid crashes through bibtexkey
		StringBuffer b = new StringBuffer (type + "{" + getName(authors[0]) + ":" + year + ",");
		appendBibtexField(b, "author", author);
		appendBibtexField(b, "abstract", abstr);
		
		appendBibtexField(b, "title", title);
		appendBibtexField(b, "booktitle", booktitle);
		appendBibtexField(b, "url", url);
		appendBibtexField(b, "year", year);
		appendBibtexField(b, "isbn", isbn);
		appendBibtexField(b, "issn", issn);
		appendBibtexField(b, "doi", doi);
		appendBibtexField(b, "volume", volume);
		appendBibtexField(b, "pages", pages);
		b.append("}");

		return b.toString();
	}

	private String getName(String author) {
		if (author != null) {
			final int indexOfComma = author.indexOf(",");
			if (indexOfComma != -1) {
				return author.substring(0, indexOfComma).replaceAll("[^a-zA-Z]", "");
			} else {
				return author.replaceAll("[^a-zA-Z]", "");
			}
		}
		return null;
	}

	private String getField(NodeList match, int i, final String field) {
		final String nodeValue = match.item(i).getNodeValue();
		if (nodeValue.indexOf(field) != -1){
			return nodeValue.substring(field.length()).trim();
		}
		return null;
	}
	
	private void appendBibtexField (StringBuffer b, String field, String value) {
		if (value != null) {
			b.append(field + " = {" + value + "},");
		}
	}

}