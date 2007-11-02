package resources;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * Builds from a string containing white space separated author names
 * a subquery which selects all publications which are published by the given authors.
 *
 * TODO: There are several issues:
 * 
 * 1. This class "knows" system tags. That should not be neccessary, when those
 *    tags are removed before giving the tags to this class.
 * 2. Similiarly, this class and other classes parse - again and again - the (tag)
 *    string. This should be done only once (i.e., breaking the string into tokens
 *    and such).
 * 3. I'm not sure, but aren't authors searched in a separate box? Why can I enter 
 *    tags in that box? I think we're mixing there stuff. There are tags and there
 *    is fulltext search. If we want to merge that, we should think of how to do it
 *    in general and not just mix it halfhearted.
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
            
            // ignore system tags
            if (author.indexOf(SystemTags.SYSTEM_PREFIX) == -1)
                this.add(author);
        }       	
	}
	
	/**
	 * generates the substring of the database query which matches the author names
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
