package resources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Builds from a string containing white space separated author names
 * a subquery which selects all publications which are published by the given authors.
 *
 */
public class SplittedAuthors extends LinkedList<String> {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The query string 
	 */
	String query = null;
		
	/**
	 * Constructor
	 * @param authors: String of authors and editor names
	 */
	public SplittedAuthors(String authors) {		
		this.query = null;
		
		Scanner s = new Scanner(authors);
        // s.useDelimiter(" "); FIXME: default delimiter is whitespace, which should be sufficient, right?
        while(s.hasNext()) {
            String author = s.next();
            
            // not consider system tags
            if (author.indexOf("system:") == -1)
                this.add(author);
        }       	
	}
	
	/**
	 * generates the substring of the database query which mathes the author names
	 * against author attribute
	 *
	 */
	private void generateQuery() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<this.size(); i++) {
			if (this.get(i).startsWith("!"))
				buf.append(" -" + this.get(i).substring(1, this.get(i).length()));
			else
				buf.append(" +" + this.get(i));
		}
		query = buf.toString();
	}
	
	/**
	 * 
	 * @return the generated sub-querystring
	 */
	public String getQuery() {
		if (query == null) {
			generateQuery();
		}
		return query;
	}
}
