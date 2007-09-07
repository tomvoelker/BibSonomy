package resources;

/**
 * Builds a query string for concept queries, similar to the query string built in SplittedTags.
 * The query implements the semantic described in [1], which means that for given tags
 * t1 t2 t3 ... tn and user u, every posts of user u which has for every tag t \in {t1, ..., tn}
 * at least one of its subtags or t itself attached is shown.
 * 
 *    [1] Hotho et. al: BibSonomy: A Social Bookmark and Publication Sharing System. Conceptual 
 *        Structures Tools Interoperability Workshop at the 13th ICCS Conf., Aalborg, Denmark, 2006
 *        http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199
 *
 */
public class SplittedConcepts extends SplittedTags {
	
	/**
	 * Needed, because SplittedTags extends LinkedList which implements "Serializable". 
	 */
	static final long serialVersionUID = 234234234234L;

	/** Until a documentation is written for this class, have a look at the one for {@link SplittedTags}. 
	 * Note, that ignoring case for concepts is not implemented as intended at the moment (if the concepts
	 * subtags are lowercase and ignoreCase is <code>true</code>, it matches all tas, ignoring their case).
	 * @param requTag
	 * @param index
	 * @param ignoreCase 
	 */
	public SplittedConcepts (String requTag, String index, boolean ignoreCase) {
		super(requTag, index, ignoreCase);
	}
	
	/**
	 * 
	 */
	private void generateQueryString() {
		StringBuffer from       = new StringBuffer ();	
		StringBuffer tagstables = new StringBuffer ();
		StringBuffer choose     = new StringBuffer (" WHERE ");
		// split string at space, tab, newline and form feed 
		for (int tagcounter = 1; tagcounter <= this.size(); tagcounter++) {
			// tas table
			from.append(", tas t" + tagcounter);
			// tagtagrelations
			if (ignoreCase) {
				from.append(", (SELECT lcase(lower) AS lower FROM tagtagrelations t" + tagcounter + " WHERE user_name=? AND lcase(upper)=lcase(?) UNION SELECT lcase(?) AS lower) AS r" + tagcounter);
				choose.append("t" + tagcounter + ".tag_lower = r" + tagcounter + ".lower ");
			} else {
				from.append(", (SELECT lower FROM tagtagrelations t" + tagcounter + " WHERE user_name=? AND upper=? UNION SELECT ? AS lower) AS r" + tagcounter);
				choose.append("t" + tagcounter + ".tag_name = r" + tagcounter + ".lower ");
			}
			// append " AND " for next round
			if (this.size() > 1) {
				choose.append(" AND ");
			}
			// append self join
			if (tagcounter > 1) {
				choose.append("t" + (tagcounter-1) + ".content_id=t" + tagcounter + ".content_id");
				if (tagcounter < this.size()) {
					choose.append(" AND ");
				}
			}
		}
		
		query = from.toString() + tagstables.toString() + choose.toString();
	}
	

	/* (non-Javadoc)
	 * @see resources.SplittedTags#getQuery()
	 */
	public String getQuery() {
		if (query == null) {
			generateQueryString();
		}
		return query;
	}
}