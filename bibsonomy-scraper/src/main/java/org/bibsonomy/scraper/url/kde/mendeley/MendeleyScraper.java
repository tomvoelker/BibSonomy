/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
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

package org.bibsonomy.scraper.url.kde.mendeley;
import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.scraper.AbstractUrlScraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.InternalFailureException;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.scraper.exceptions.ScrapingFailureException;
import org.bibsonomy.util.WebUtils;
/**
 * @author Haile
 * @version $Id$
 */
public class MendeleyScraper extends AbstractUrlScraper{
	private final Log log = LogFactory.getLog(MendeleyScraper.class);

	private static final String SITE_NAME = "Mendeley";
	private static final String SITE_URL = "http://mendeley.com";
	private static final String INFO = "This scraper parses a publication page from the " + href(SITE_URL, SITE_NAME);
	private static final List<Pair<Pattern, Pattern>> PATTERNS = Collections.singletonList(new Pair<Pattern, Pattern>(Pattern.compile(".*" + "mendeley.com"), AbstractUrlScraper.EMPTY_PATTERN));
	private static final Pattern BIBTEX_PATTERN = Pattern.compile("citation_json.*");	
	
	@Override
	protected boolean scrapeInternal(final ScrapingContext scrapingContext) throws ScrapingException {
		scrapingContext.setScraper(this);

		final URL url = scrapingContext.getUrl();
	
		try {

			final String bibTex = WebUtils.getContentAsString(url);
			Matcher match = BIBTEX_PATTERN.matcher(bibTex);
			
			String strCitation = "";		
			if(match.find()){
				strCitation = match.group(0);
			}else{
				log.error("can't parse publication");
				return false;
			}
			
			strCitation = strBibtex(strCitation);
			
			if (present(strCitation)) {
				scrapingContext.setBibtexResult(strCitation);
				return true;
			} else {
				throw new ScrapingFailureException("getting bibtex failed");
			}
		} catch (final Exception e) {
			throw new InternalFailureException(e);
		}

	}
	private String strBibtex(String strCitation)  throws JSONException
	{
		StringBuilder result = new StringBuilder();
		String jsonRead = strCitation;
		jsonRead = jsonRead.substring(16).replaceAll(";", "").replaceAll("\\/","/");
		String citationKey = "",authorsFullName = "",editorsFullName = "";
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonRead );  
		
		String entryType = "",lblTitle="";
		String type = json.getString("type");
		if(type.contains("book")){ 
			entryType = "@book{"; lblTitle = "booktitle";
		}else if(type.contains("journal")){
			entryType = "@article{"; lblTitle = "journal";
		}else { 
			entryType = "@misc{"; lblTitle = "journal";
		}
		
			JSONArray authors = null;
			if(json.has("authors"))
				authors = json.getJSONArray("authors");
			if(authors != null){
				for (Object author : authors) {
					JSONObject jsonAuthor = (JSONObject) author;
					String forename = "";
						if(jsonAuthor.has("forename")) forename = jsonAuthor.getString("forename");
					String surname = "";
						if(jsonAuthor.has("surname")) surname = jsonAuthor.getString("surname");
					citationKey +=surname+"_";
					if(authorsFullName != "") authorsFullName += " and "; 
					authorsFullName += surname + "," + forename;
				}
			}
			JSONArray editors = null; 
					if(json.has("editors")) editors = json.getJSONArray("editors");
			if(editors != null){
				for(Object editor : editors){
					JSONObject jsonEditor = (JSONObject) editor;
					String forename = "";
						if(jsonEditor.has("forename")) forename = jsonEditor.getString("forename");
					String surname = "";
						if(jsonEditor.has("surname")) surname = jsonEditor.getString("surname");
					if(editorsFullName != "")	editorsFullName += " and ";
					editorsFullName += surname + "," + forename;
				}
			}
		
		long year = json.has("year") ? json.getLong("year") : 0;
		
		result.append(entryType);
	    result.append(citationKey + year + ",\n");
	    
	    if(json.has("title"))
	    	result.append( "title = {" + json.getString("title") + "},\n");
	    if(json.has("volume"))
	    	result.append( "volume = {" + json.getString("volume") + "},\n");	    
	    if(json.has("issue")) 
	    	result.append( "number = {" + json.getString("issue") + "},\n");	    
	    if(json.has("website")) 
	    	result.append("url = {" + json.getString("website") + "},\n");	    
	    if(json.has("published_in")) 
	    	result.append(lblTitle + " = {" + json.getString("published_in") + "},\n");	   
	    if(json.has("publisher")) 
	    	result.append( "publisher = {" + json.getString("publisher") + "},\n"); 	    
	    if(authorsFullName != "") 
	    	result.append( "author = {"+ authorsFullName+"},\n");	    
	    if (editorsFullName != "") 
	    	result.append( "editors = {"+ editorsFullName+"},\n");	    
	    if(year != 0) 
	    	result.append( "year = {" + year + "},\n");	    
	    if(json.has("pages")) 
	    	result.append( "pages = {" + json.getString("pages") +"}}");
	    
		return result.toString();		
	}
	
	@Override
	public List<Pair<Pattern, Pattern>> getUrlPatterns() {
		return PATTERNS;
	}

	@Override
	public String getSupportedSiteName() {
		return SITE_NAME;
	}

	@Override
	public String getSupportedSiteURL() {
		return SITE_URL;
	}

	@Override
	public String getInfo() {
		return INFO;
	}	
}
