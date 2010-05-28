package resources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Builds from a string containing white space separated tags
 * a query (big self join) which selects all posts which have all of the tags.
 *
 */
@Deprecated
public class SplittedTags extends LinkedList<String> {
	
	/**
	 * LinkedList implements "Serializable" therefore we need a serialVersionUID
	 */
	static final long serialVersionUID = 234234234234L;
	/**
	 * The query string which is built dynamically.
	 */
	protected String query = null;
	/**
	 * This is used to specifiy an INDEX for the database to use on the first joined table.
	 * Often this is just empty, since we can trust the database, that it chooses the correct
	 * index.
	 */
	protected String index = null;  
	/**
	 * If <code>true</code>, ignore case when comparing tags in the database.
	 */
	protected boolean ignoreCase; 
	
	/**
	 * @param tags a collection of tags
	 * @param index if the database takes the wrong index for the query, you can give 
	 * it here via "FORCE INDEX" statements (see <a href="http://dev.mysql.com/doc/refman/5.0/en/join.html">http://dev.mysql.com/doc/refman/5.0/en/join.html</a>)
	 * @param ignoreCase if <code>true</code> the case of tags is ignored doing database comparisons
	 */
	public SplittedTags (Collection<String> tags, String index, boolean ignoreCase) {
		super(tags);
		this.index = index;
		this.ignoreCase = ignoreCase;
		this.query = null;
	}

	/**
	 * Splits a string of white space separated tags and stores them in a list.
	 * 
	 * @param requTag the string containing the tags
	 * @param index if the database takes the wrong index for the query, you can give 
	 * it here via "FORCE INDEX" statements (see <a href="http://dev.mysql.com/doc/refman/5.0/en/join.html">http://dev.mysql.com/doc/refman/5.0/en/join.html</a>)
	 * @param ignoreCase if <code>true</code> the case of tags is ignored doing database comparisons
	 */
	public SplittedTags (String requTag, String index, boolean ignoreCase) {
		// stop processing on empty string
		if (requTag == null) return;
		// initialize attributes
		this.index = index;
		this.ignoreCase = ignoreCase;
		this.query = null;
		// split string on whitespace and collect tags
		/*
		 * TODO: another dblp hack ...
		 * A query for at least two tags which has "dblp" as first tag is horribly slow due to
		 * the braindead MySQL optimizer. Therefore we ensure here that the "dblp" tag is always
		 * the last tag in the list.
		 * TODO: this is just a simple heuristic ... if someone enters several "dblp" tags 
		 * (i.e., "dblp", "DBLP", "dBlP", "dbLP", ...) only the last one is added. Probably 
		 * this is not what we want.
		 */
		StringTokenizer token = new StringTokenizer (requTag);
		String tag;
		String dblp = null;
		while (token.hasMoreTokens()) {
			tag = token.nextToken();
			if ("dblp".equals(tag.toLowerCase())) {
				// dblp tag found, don't add it
				dblp = tag;
			} else {
				// "normal" tag, add it
				this.add(tag);
			}
		}
		// if dblp tag found, add it as last tag
		if (dblp != null) {
			this.add(dblp);
		}
	}
	

	/**
	 * Generates a query string from a collection of tags.
	 */
	private void generateQueryString() {

		StringBuffer from     = new StringBuffer ();	
		StringBuffer join     = new StringBuffer (" WHERE ");
		StringBuffer choose   = new StringBuffer ();
		// split string at space, tab, newline and form feed 
		for (int tagcounter = 1; tagcounter <= this.size(); tagcounter++) {
			// compare with or without case sensitivity
			if (ignoreCase) {
				choose.append(" t" + tagcounter + ".tag_lower = lower(?) AND ");
			} else {
				choose.append(" t" + tagcounter + ".tag_name= ? AND ");
			}
		}
		
		// generate from and join
		from.append(" tas t1 " + index + " ");
		for (int tagcounter=2; tagcounter<=this.size(); tagcounter++) {
			from.append(", tas t").append(tagcounter);
			join.append("t").append(tagcounter-1).append(".content_id=t").append(tagcounter).append(".content_id AND ");
		}
		// remove last AND
		int last_and = choose.lastIndexOf("AND");
		if (last_and > 0) {
			query = from.append(join).append(choose.substring(0, last_and)).toString();
		} else {
			// no AND --> add empty tag (its stupid, because if there is no tag, there is no tas entry)
			query = from.append(join).append(choose + " t1.tag_lower = ? ").toString();
			this.add("");
		}	
	}
	
	/**
	 * Generates query string for folkrank query.
	 */
	private void generateFolkrankQueryString() {
		StringBuffer query = new StringBuffer();
		
		for (int tagCounter = 0; tagCounter<this.size(); tagCounter++)  {
			query.append("r.item = ?");
			
			if (tagCounter != (this.size() - 1))
				query.append(" OR ");
		}
		
		this.query = query.toString();		
	}
	
	/**
	 * @return the generated query string 
	 */
	public String getFolkrankQuery() {
		if (query == null) {
			generateFolkrankQueryString();
		}
		return query;
	}
	
	
	/**
	 * @return the generated query string 
	 */
	public String getQuery() {
		if (query == null) {
			generateQueryString();
		}
		return query;
	}
}