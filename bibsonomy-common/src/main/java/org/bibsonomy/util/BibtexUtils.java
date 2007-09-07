package org.bibsonomy.util;

public class BibtexUtils {

	public static String generateBibtexKey(String authors, String editors, String year, String title) {
		/*
		 * todo: pick either author or editor. DON'T use getAuthorlist (it sorts alphabetically!). CHECK for null values.
		 * What to do with Chinese authors and other broken names?
		 * How to extract the first RELEVANT word of the title?
		 * remove Sonderzeichen, LaTeX markup!
		 */
		StringBuffer b = new StringBuffer();
		/*
		 * get author
		 */
		String first = getFirstPersonsLastName(authors);
		if (first == null) {
			first = getFirstPersonsLastName(editors);
			if (first == null) {
				first = "noauthororeditor";
			}
		}
		b.append(first);
		/* the year */ 
		if (year != null) {
			b.append(year.trim());
		}
		/* first relevant word of the title */
		if (title != null) {
			/* best guess: pick first word with more than 4 characters, longest first word */
		}
		return b.toString().toLowerCase();
	}
	
	/**  Tries to extract the last name of the first person.
	 *  
	 * @return the last name of the first person
	 */
	public static String getFirstPersonsLastName (String person) {
		if (person != null) {
			String firstauthor;
			/*
			 * check, if there is more than one author
			 */
			int firstand = person.indexOf(" and ");
			if (firstand < 0) {
				firstauthor = person;
			} else {
				firstauthor = person.substring(0, firstand);				
			}
			/*
			 * first author extracted, get its last name
			 */
			int lastspace = firstauthor.lastIndexOf(' ');
			String lastname;
			if (lastspace < 0) {
				lastname = firstauthor;
			} else {
				lastname = firstauthor.substring(lastspace + 1, firstauthor.length());
			}
			return lastname;
		}
		return null;
	}
	
}
