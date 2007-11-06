package org.bibsonomy.importer.event.iswc;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.bibsonomy.importer.event.iswc.model.Publication;

/**
 * Class with some helpfull methods for working with bibtex.
 * @author tst
 *
 */
public class BibtexHelper {
	
	/**
	 * This method takes a {@link ArrayList} which contains author Strings. This String will
	 * be merged to one single author String, which can be used in BibTeX. The author will be
	 * seperated with "and". 
	 * @param authors which has to be merged to one single authors String 
	 * @return String which contains all authors seperated with "and"
	 */
	public static String buildPersonString(ArrayList<String> authors){
    	
		// build authors string
    	StringBuffer authorsBuffer = new StringBuffer();
    	for(String author: authors){
    		authorsBuffer.append(author);
    		authorsBuffer.append(" and ");
    	}
    	
    	// cut off last " and "
    	return authorsBuffer.toString().substring(0, authorsBuffer.lastIndexOf(" and "));
	}
	
	/**
	 * Seacrhs the first word in title which has 5 or more characters.
	 * @param title of a {@link Publication}
	 * @return first word in title which match
	 */
	public static String buildTitleKey(String title){
		// seperate title in tokens 
		StringTokenizer tokenizer = new StringTokenizer(title);
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			// search first token, which has 5 or more characters
			if(token.length() > 4)
				return token;
		}
		
		// if no token is big enough, then return a default value
		return "noTitleKey";
	}
	
	/**
	 * Extract the last word of the person String (hopefully the lastname of the person).
	 * @param person
	 * @return last word of the given String
	 */
	public static String extractLastname(String person){
		return person.substring(person.lastIndexOf(" ") + 1);
	}
	
	/**
	 * Builds a bibtex String with the given {@link Publication}. 
	 * @param publication The Publication which has to bo converted to bibtex
	 * @return bibtex String of this Publication, null if the given Publication 
	 */
	public static String buildBibtex(Publication publication){

		StringBuffer bibtex = new StringBuffer();
		
		// build first line
		bibtex.append("@");
		bibtex.append(publication.getEntrytype());
		bibtex.append("{");
		bibtex.append(publication.getBibtexkey());
		
		// build fields from publication
		addField(bibtex, "author", publication.getAuthor());
		addField(bibtex, "title", publication.getTitle());
		addField(bibtex, "keywords", publication.getKeywords());
		addField(bibtex, "abstract", publication.getBibabstract());
		addField(bibtex, "month", publication.getMonth());
		addField(bibtex, "year", publication.getYear());
		addField(bibtex, "address", publication.getAddress());
		addField(bibtex, "booktitle", publication.getBooktitle());
		addField(bibtex, "crossref", publication.getCrossref());
		addField(bibtex, "editor", publication.getEditor());
		addField(bibtex, "pages", publication.getPages());
		addField(bibtex, "publisher", publication.getPublisher());
		addField(bibtex, "series", publication.getSeries());
		addField(bibtex, "volume", publication.getVolume());
		addField(bibtex, "url", publication.getUrl());

		bibtex.append("\n}\n");
		
		return bibtex.toString();
	}
	
	/**
	 * Used add bibtex building. This method adds a new field to the bibtex {@link StringBuffer}.
	 * First it completes the previous line and then add the given field and value. 
	 * @param bibtex StringBuffer of the {@link Publication} which is current build  
	 * @param field current bibtex field which must be added to buffer
	 * @param fieldValue the value of the current bibtex field 
	 */
	private static void addField(StringBuffer bibtex, String field, String fieldValue){
		// add only if field and value are not empty
		if(field != null && fieldValue != null && !field.trim().equals("") && !fieldValue.trim().equals("")){
			// complete previous line
			bibtex.append(",\n");
			
			// add next line
			bibtex.append(field);
			bibtex.append(" = {");
			bibtex.append(fieldValue);
			bibtex.append("}");
		}
	}
	
	/**
	 * Builds a bibtexkey String with the surname from the given author,
	 * the year and the first word in the title which has the 
	 * lenght 5 or larger. 
	 * @param author Author String of a Publication
	 * @param year
	 * @param title
	 * @return
	 */
	public static String buildBibtexKey(String author, String year, String title){
    	StringBuffer bibKey = new StringBuffer();
    	bibKey.append(BibtexHelper.extractLastname(author));
    	bibKey.append("/");
    	bibKey.append(year);
    	bibKey.append("/");
    	bibKey.append(BibtexHelper.buildTitleKey(title));
    	return bibKey.toString();
	}


}
