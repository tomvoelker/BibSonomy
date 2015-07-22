/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.bibsonomy.scraper.converter;

import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author Mohammed Abed
 */
public class CslToBibtexConverter {

	/** Function is to convert csl format to bibtex
	 * 
	 * @param Ris
	 * @return The resulting BibTeX string.
	 */
	public String cslToBibtex(final String cslCitation)  throws JSONException {
		final StringBuilder result = new StringBuilder();
		String jsonRead = cslCitation;
		jsonRead = jsonRead.substring(16).replaceAll(";", "").replaceAll("\\/","/");
		String citationKey = "";
		String authorsFullName = "";
		String editorsFullName = "";
		
		final JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonRead);  
		
		String entryType = "";
		String lblTitle = "";
		
		final String type = json.getString("type");
		if (type.equalsIgnoreCase("book") || type.equalsIgnoreCase("statute")){ 
			entryType = "@book{";
			lblTitle = "booktitle";
		} else if (type.equalsIgnoreCase("journal") || type.equalsIgnoreCase("case") || 
				type.equalsIgnoreCase("computer_program") || type.equalsIgnoreCase("generic") ||
				type.equalsIgnoreCase("journal_article") || type.equalsIgnoreCase("magazine_article") ||
				type.equalsIgnoreCase("newspaper_article") || type.equalsIgnoreCase("working_paper")) {
			entryType = "@article{";
			lblTitle = "journal";
		}else if (type.equalsIgnoreCase("book_section")){
			entryType = "@inbook{";
			lblTitle = "booktitle";
		}else if(type.equalsIgnoreCase("thesis")){
			entryType = "@phdthesis{";
			lblTitle = "journal";
		}else if(type.equalsIgnoreCase("conference_proceedings")){
			entryType = "@inproceedings{";
			lblTitle = "journal";
		}else if(type.equalsIgnoreCase("report")){
			entryType = "@techreport{";
			lblTitle = "journal";
		}else { 
			entryType = "@misc{";
			lblTitle = "journal";
		}
		
		JSONArray authors = null;
		if (json.has("authors")) {
			authors = json.getJSONArray("authors");
		}
		if (authors != null) {
			for (final Object author : authors) {
				final JSONObject jsonAuthor = (JSONObject) author;
				String forename = "";
				if (jsonAuthor.has("forename")) {
					forename = jsonAuthor.getString("forename");
				}
				String surname = "";
				if (jsonAuthor.has("surname")) {
					surname = jsonAuthor.getString("surname");
				}
				citationKey += surname + "_";
				if (authorsFullName != "") {
					authorsFullName += " and ";
				} 
				authorsFullName += surname + "," + forename;
			}
		}
		JSONArray editors = null; 
		if (json.has("editors")) {
			editors = json.getJSONArray("editors");
		}
		if (editors != null) {
			for (final Object editor : editors){
				final JSONObject jsonEditor = (JSONObject) editor;
				String forename = "";
				if (jsonEditor.has("forename")) {
					forename = jsonEditor.getString("forename");
				}
				String surname = "";
				if  (jsonEditor.has("surname")) {
					surname = jsonEditor.getString("surname");
				}
				if (editorsFullName != "") {
					editorsFullName += " and ";
				}
				editorsFullName += surname + "," + forename;
			}
		}
		
		final long year = json.has("year") ? json.getLong("year") : 0;
		
		result.append(entryType);
		result.append(citationKey).append(year ).append( ",\n");
	
	    if (json.has("title")) {
			result.append( "title={").append(json.getString("title")).append( "},\n");
		}
	    if (json.has("volume")) {
			result.append( "volume={").append(json.getString("volume")).append("},\n");
		}
	    if (json.has("issue")) {
			result.append( "number={").append(json.getString("issue")).append("},\n");
		}
		if (json.has("website")) {
			result.append("url={").append(json.getString("website")).append("},\n");
		}
		if (json.has("published_in")) {
			result.append(lblTitle + "={").append(json.getString("published_in")).append("},\n");
		}
		if (json.has("publisher")) {
			result.append( "publisher={").append(json.getString("publisher")).append("},\n");
		}
		if (authorsFullName != "") {
			result.append( "author={").append(authorsFullName).append("},\n");
		}
		if (editorsFullName != "") {
			result.append( "editor={").append(editorsFullName).append("},\n"); 
		}	    
	    if (year != 0) {
			result.append( "year={").append(year).append("},\n");
		}	    
	    if (json.has("pages")) {
			result.append( "pages={").append(json.getString("pages")).append("}");
		}
	    result.append("}");
		return result.toString();		
	}
}
