package org.bibsonomy.marc.extractors;

import java.util.ArrayList;

import org.bibsonomy.marc.AttributeExtractor;
import org.bibsonomy.marc.ExtendedMarcRecord;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;

/**
 * extracts a BibTex author attribute out of a MarcRecord
 * 
 * @author lha
 * @version $Id$
 */
public class AuthorExtractor implements AttributeExtractor {

	@Override
	public void extraxtAndSetAttribute(BibTex target, ExtendedMarcRecord src) {
		ArrayList<PersonName> authors = new ArrayList<PersonName>();
		
		//get fields containing firstname information
		final String primaryName = src.getFirstFieldValue("100", 'a');
		final String primaryNumeration = src.getFirstFieldValue("100", 'b');
		final String primaryTitle = src.getFirstFieldValue("100", 'c');
		
		//get fields containing lastname information
		final String secondaryName = src.getFirstFieldValue("700", 'a');
		final String secondaryNumeration = src.getFirstFieldValue("700", 'b');
		final String secondaryTitle = src.getFirstFieldValue("700", 'c');
		final String secondaryRelatorCode = src.getFirstFieldValue("700", '4');
		
		if (primaryName != null) {
			
			authors.add(getName(primaryName, primaryNumeration, primaryTitle));
			
		}
		if(secondaryRelatorCode != null && secondaryRelatorCode.equals("aut") && secondaryName != null) {
			
			authors.add(getName(secondaryName, secondaryNumeration, secondaryTitle));
			
		}
		
		//set the resulting BibTex information
		target.setAuthor(authors);
		
	}
	
	/**
	 * creates a name out of given information
	 * 
	 * @param name the name to set, format: lastname, firstname
	 * @param numeration 
	 * @param title
	 * @return the created name object
	 */
	private PersonName getName(String name, String numeration, String title) {
		
		PersonName result = new PersonName();
		//build lastName
		String lastName;
		//forename is available
		if (name.contains(", ")) {
			//get foreName
			result.setFirstName(name.split(", ")[1]);
			lastName = "";
			lastName += name.split(", ")[0].replace(",", "");
			lastName += (numeration != null ? (" " + numeration.trim()) : "");
			lastName += (title != null ? (" " + title.trim()) : "");

			result.setLastName(lastName);
		} 
		//maybe only lastname is given
		else {
			lastName = "";
			lastName += name.replace(",", "");
			lastName += (numeration != null ? (" " + numeration.trim()) : "");
			lastName += (title != null ? (" " + title.trim()) : "");
			
			result.setLastName(lastName);
		}
		
		return result;
	}

}
