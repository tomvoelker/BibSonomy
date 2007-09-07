package resources;

import helpers.constants;

/**
 * Builds a query string for concept queries. For given tags t1 t2 t3 ... tn posts of all users 
 * which has for every tag t \in {t1, ..., tn} at least one of its subtags or t itself attached 
 * are shown.
 */
public class SplittedEntireConcepts extends SplittedTags {

	/**
	 * needed, because we have different queries for bookmark and bibtex
	 */
	private int content_type; 
	
	/**
	 * constructor
	 * @param requTag requested supertag(s) of concept
	 * @param index
	 * @param ignoreCase 
	 */
	public SplittedEntireConcepts(String requTag, String index, boolean ignoreCase) {
		super(requTag, index, ignoreCase);		
	}

	/**
	 * it's serializable
	 */
	private static final long serialVersionUID = 2147671785074736362L;

	/**
	 * generates the subquerystring dependent on the requested 
	 * contentType to get all posts of the concept
	 */	
	private void generateQueryString() {
		StringBuffer select = new StringBuffer();
		StringBuffer from 	= new StringBuffer(" FROM (");
		StringBuffer choose	= new StringBuffer();
		
		// initialize choose
		if (content_type == Bookmark.CONTENT_TYPE) {
			choose.append(" JOIN bookmark b USING(content_id) JOIN urls u USING(book_url_hash) ");
		} else {
			choose.append(" JOIN bibtex b USING(content_id) JOIN bibhash h ON (h.hash = b.simhash" + Bibtex.INTER_HASH + " AND h.type = " + Bibtex.INTER_HASH + ")");
		}
		
		choose.append(" GROUP BY t.content_id HAVING ");
		
		for (int tagcounter = 1; tagcounter <= this.size(); tagcounter++) {				
			/*
			 * SELECT part
			 */
			if (ignoreCase) 			
				select.append(", MAX(IF (upper = LCASE(?), 1, 0)) AS s" + tagcounter); 
			else				
				select.append(", MAX(IF (upper = ?, 1, 0)) AS s" + tagcounter); 			
			
			/*
			 * FROM part
			 */
			if (ignoreCase) 
				from.append("SELECT LCASE(upper) AS upper, LCASE(lower) AS lower, user_name FROM tagtagrelations t WHERE LCASE(upper) = LCASE(?) UNION SELECT LCASE(?),LCASE(?),NULL");
			else
				from.append("SELECT upper, lower, user_name FROM tagtagrelations t WHERE upper = ? UNION SELECT ?,?,NULL");
			
			// next round
			if (tagcounter < this.size())
				from.append(" UNION ");
			else			
				from.append(") AS tags JOIN tas t FORCE INDEX (tag_lower_idx)"
						+ 	" ON (t.tag_lower = tags.lower "
						+ 	" AND (t.user_name = tags.user_name OR ISNULL(tags.user_name))"
						+	" AND t.content_type = " + content_type 
						+ 	" AND t.group = " + constants.SQL_CONST_GROUP_PUBLIC + ")");
						
			/*
			 * WHERE part
			 */			
			choose.append("s" + tagcounter + " > 0 ");
			if (tagcounter < this.size())
				choose.append(" AND ");
		}		
		
		query = select.toString() + from.toString() + choose.toString();		
	}
	
	/**
	 * public function to generate the query 
	 * @param content_type bookmark = 1, bibtex = 2
	 * @return subquery string to build entire SQL query ( @see RecourceHandler.java )
	 */
	public String getQuery(int content_type) {
		this.content_type = content_type;
		generateQueryString();		
		return query;
	}	
}