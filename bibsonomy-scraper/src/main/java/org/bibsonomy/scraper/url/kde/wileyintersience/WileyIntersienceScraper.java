/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.url.kde.wileyintersience;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.Tuple;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;


/**
 * Scraper for www3.interscience.wiley.com
 * @author tst
 */
public class WileyIntersienceScraper extends AbstractUrlScraper {
	
	private static final String SITE_NAME = "InterScience";
	private static final String SITE_URL = "http://www3.interscience.wiley.com";
	private static final String INFO = "Extracts publications from the abstract page of " + href(SITE_URL,SITE_NAME) + ".";

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
	
	// pattern for getting id from url between jorunal and abstract
	private static final String PATTERN_GET_ID_JOURNAL_ABSTRACT = "journal/(\\d*)/abstract";
	
	private static final List<Tuple<Pattern, Pattern>> patterns = Collections.singletonList(new Tuple<Pattern, Pattern>(Pattern.compile(".*" + WILEY_INTERSIENCE_HOST), AbstractUrlScraper.EMPTY_PATTERN));
	
	/**
	 * Scraper for www3.interscience.wiley.com
	 * 
	 * supported page:
	 * - abtsract page
	 * - download citation page
	 */
	protected boolean scrapeInternal(ScrapingContext sc) throws ScrapingException {
			sc.setScraper(this);
			
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
					citationId = citationId.substring(0, citationId.length());
				}
				
				// alternate url pattern for abstract page
				else{
					Pattern idPattern = Pattern.compile(PATTERN_GET_ID_JOURNAL_ABSTRACT);
					Matcher idMatcher = idPattern.matcher(citationId);
					if(idMatcher.find())
						citationId = idMatcher.group(1);
				}
				
				// save page to get the right cookie
				String pageToVisit = WILEY_INTERSIENCE_CITATION + citationId;
				
				// build url to publication data
				String urlCitationEndNote = WILEY_INTERSIENCE_CITEX + CITATION_FORM_MODE + "&" + CITATION_FORM_FORMAT + "&" + CITATION_FORM_TYPE + "&" + CITATION_FORM_FILE; 

				// visit cookie page and get publication data
				String endnote = WebUtils.getContentAsString(urlCitationEndNote, null, null, pageToVisit);
				
				//check wether WILEY did not return the proper information as expected
				//this happens randomly
				if(!this.containsMandatoryEndnoteInformation(endnote))
				{
					throw new ScrapingFailureException("The website did not return the expected information. Please try again later.");
				}
				/*
				 * parse publication data
				 * every line start with a descripor for the bibiliogaphic meaning
				 */ 
				BufferedReader reader = new BufferedReader(new StringReader(endnote));
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
						String tmp = line.substring(4);
						
						if(author == null) {
							author = tmp.replaceAll(",", " and");
						} else {
							author = author + " and " + tmp.replaceAll(",", " and");
						}
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
				return true;
			}catch (MalformedURLException e) {
				throw new InternalFailureException(e);
			}catch (IOException ioe){
				throw new InternalFailureException(ioe);
			}
	}
	
	public String getInfo() {
		return INFO;
	}

	public List<Tuple<Pattern, Pattern>> getUrlPatterns() {

return patterns;
}

	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	public String getSupportedSiteURL() {
		return SITE_URL;
	}
	
	private boolean containsMandatoryEndnoteInformation(String endnote)
	{
		return 	endnote.contains("AU:") &&
				endnote.contains("TI:") &&
				endnote.contains("YR:");
	}
}
